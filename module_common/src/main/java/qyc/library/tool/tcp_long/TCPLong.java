package qyc.library.tool.tcp_long;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import qyc.library.tool.tcp_long.ReIniter.OnReInitResult;
import qyc.library.tool.tcp_long.TCPLongListener.OnTcpLongListen;
import qyc.library.tool.tcp_long.TcpLongAdapter.AdapterResult;

//关于不提供数据发送函数的声明:
//当你看完源码，你会发现它不发送数据。我希望你明白,移动网络长连接的脆弱性和复杂性决定了这个连接有、或将来会加入各种连接策略。
//所以我封装的这个推送长连接没有提供发送数据的入口，我的目的是希望它能够专注于推送类的业务。
//如果你修改源码的目的是为了破除这种约束,那么我希望你先停下来思考，我的问题是为什么你想要改动它?
//是否出于整体设计的考虑？如果"仅仅想要一种简单的实现方式"，那么我的建议是三思而后行。
//移动网络用于接收数据的长连接涉及一些不可控的优化策略，例如重连的节奏，也不排除将来加入更多的优化策略，总之它是不可靠的。
//如果你让过多业务依赖它，最后就不得不花大量时间，反过来让它去兼容不断变化的业务。
//在不可靠的连接和多变的业务之间作兼容，代价将是程序质量和开发成本，当然也包括你未来的时间和精力。
//可以参考一下科学的移动网络模型，来实现你的设计：
//科学的安卓终端移动网络模型应该是:长连接建立后拿到登录凭证,和服务器保持弱长连接(弱长连接即不一定可靠的连接,它随时会断开又随时会连上)。
//手机保存这个登录凭证,后面所有业务都借助短连接、HTTP来发数据。
//对服务器来说,弱长连接的断开或连接不代表手机登录状态,手机登录状态通过登录请求和注销请求决定。
//发送频繁需要特别优化的，可以通过弱长连接+短连接(TCP、Http)的方式来发送数据。（有长连接就用长连接发，没有就用短连接发）

/**
 * TCP长连接,专门用于推送.
 * 
 * @author 钱园超 [2015年7月29日 上午10:08:49]
 */
public class TCPLong {

	/** 长连接的三种状态 -1=未初始化 0=正在注册 1.连接正常 */
	private int state = -1;

	private String IP = null;
	private int port = 0;
	private TcpLongAdapter adapter = null;

	private Socket socket = null;

	private Timer heartTimer = null;
	private ReIniter reIniter = null;
	private boolean isDestory = false;
	private Object destoryLock = new Object();

	private TCPLongListener tcpLongListener = new TCPLongListener();

	private byte[] initOverData = null;

	public void setAddress(String IP, int port) {
		this.IP = IP;
		this.port = port;
	}

	public void setAdapter(TcpLongAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * <p>
	 * 对长连接进行初始化,此时不触发任何观察者,只返回初始化适配器得出的结果。
	 * <p>
	 * 如果初始化失败，它会自动销毁连接。如果初始化成功，它将建立连接，然后你要保持长连接。
	 * <p>
	 * 需要注意的是业务上的失败例如密码错误，不属于初始化失败，而是属于成功。需要调用者主动断开连接。
	 * 
	 * @see TCPLong#keep(byte[])
	 * @see TCPLong#destory()
	 * */
	public Object init(byte[] initData, long outTime) {
		isDestory = false;
		state = 0;
		InitRunnable initRunnable = new InitRunnable(IP, port, initData, outTime, adapter);
		Thread thread = new Thread(initRunnable);
		thread.start();
		try {
			thread.join();
		}
		catch (InterruptedException e) {
		}

		AdapterResult initResult = initRunnable.initResult;
		if (!(initResult.result instanceof TcpLongInitFail)) {
			// 如果初始化是成功的,那么拿出socket和剩余数据
			socket = initRunnable.socket;
			initOverData = initRunnable.initResult.overData;
			System.out.println("TCP长连接-->初始化后剩余数据长度=" + initOverData.length);
			state = 1;
		} else {
			state = -1;
		}
		return initRunnable.initResult.result;
	}

	/** 开始保持长连接,这时会启动心跳和断线重连机制,并开始触发观察者. */
	public void keep(byte[] reInitData) {
		new Thread(new ReceiveRunnable(reInitData, initOverData)).start();
		initOverData = null;
	}

	/**
	 * 断开长连接,不再触发任何观察者,不再重连.
	 */
	public void destory() {
		synchronized (destoryLock) {
			// 关闭接收部分(如果初始化成功拿到了SOCKET的话) 另外由于心跳和接收是同步的，所以心跳也会停止
			isDestory = true;
			if (socket != null) {
				try {
					socket.close();
				}
				catch (IOException e) {
				}
			}
			// 关闭重连部分(如果已经启动重连的话)
			if (reIniter != null) {
				reIniter.drop();
			}
		}

		System.out.println("TCP长连接-->destory()");
	}

	/** 获取长连接状态 -1=未初始化 0=正在重新注册 1=连接正常 */
	public int getState() {
		return state;
	}

	/** 设置长连接监听 */
	public void addTcpLongListen(OnTcpLongListen onTcpLong) {
		tcpLongListener.addTcpLongListen(onTcpLong);
	}

	/** 取消长连接监听 */
	public void removeTcpLongListen(OnTcpLongListen onTcpLong) {
		tcpLongListener.removeTcpLongListen(onTcpLong);
	}

	private class ReceiveRunnable implements Runnable {

		private byte[] reInitData;
		private byte[] initOverData;

		private ReceiveRunnable(byte[] reInitData, byte[] initOverData) {
			this.reInitData = reInitData;
			this.initOverData = initOverData;
		}

		@Override
		public void run() {
			System.out.println("TCP长连接-->接收线程被启动");
			// 开始接收则立即启动心跳
			heartTimer = new Timer();
			heartTimer.schedule(new HeatTimerTask(), 120000, 120000);// 两分钟一次哈

			// 初始化剩余数据缓存和接收缓存
			byte[] overTemp;
			if (initOverData != null && initOverData.length > 0) {
				overTemp = initOverData;// 补入登录剩余数据，到剩余数据缓存中
				initOverData = null;
			} else {
				overTemp = new byte[0];
			}
			byte[] receiveTemp = new byte[1024];
			int readLength = 0;

			while (true) {
				// 把数据给适配器去解析
				toAdapter : {
					// 将剩余数据和接收数据合并起来
					overTemp = byteCopy(overTemp, overTemp.length, receiveTemp, readLength);
					if (overTemp.length == 0) {
						// 合并后的整段数据如果长度为0，就跳出解析部分
						break toAdapter;
					}

					while (true) {
						// 循环的给adapter去解析
						AdapterResult ar = adapter.onReceiveDecode(overTemp, overTemp.length);

						if (ar != null) {
							// 有了解析结果
							// 反馈
							tcpLongListener.onReceive(ar.result);
							// 将剩余数据拿来，用在下一次接收后的合并
							if (ar.overData != null) {
								overTemp = ar.overData;
							} else {
								overTemp = new byte[0];
								System.out.println("TCP长连接-->缓存被清空");
							}
						} else {
							// 没有解析结果，跳出解析部分，继续接收数据
							break toAdapter;
						}

					}
				}

				// 接收数据,一旦接收错误就立即停止接收
				try {
					readLength = socket.getInputStream().read(receiveTemp);// 这里不能把inputStream拿出来,因为getInputStrean本身就有判断
				}
				catch (IOException e) {
					break;
				}
				System.out.println("TCP长连接-->收到长度=" + readLength);
				if (readLength < 0) {
					break;
				}
			}

			// 接收结束则立即停止心跳
			heartTimer.cancel();

			// 立即关闭socket
			try {
				socket.close();
			}
			catch (IOException e) {
			}

			// 立即启动重连,启动重连要防止跟destory产生并发
			synchronized (destoryLock) {
				if (!isDestory) {
					if (reIniter != null) {
						reIniter.drop();
					}
					reIniter = new ReIniter(IP, port, reInitData, adapter, onReInitResult);
					reIniter.fly();
				} else {
					System.out.println("TCP长连接-->接收线程正式结束,并不再起来");
					state = -1;
				}
			}
		}
	}

    private OnReInitResult onReInitResult = new OnReInitResult() {

		@Override
		public void onReInitSuccess(Socket newSocket, byte[] reInitData, byte[] initOverData) {
			System.out.println("TCP长连接-->重连成功,登陆剩余数据长度=" + initOverData.length);
			// 重连成功的通知,也要防止跟destory产生并发,如果在通知的瞬间并发destory了,很可能destory无法销毁重连出来的socket
			synchronized (destoryLock) {
				if (isDestory) {
					try {
						newSocket.close();
					}
					catch (IOException e) {
					}
				} else {
					socket = newSocket;
					state = 1;
					new Thread(new ReceiveRunnable(reInitData, initOverData)).start();// 启动接收线程
					// 通知
					tcpLongListener.onReLoginResult(true);
				}
			}
		}

		@Override
		public void onReInitStart() {
			// synchronized (reConnectTimer) {
			state = 0;
			// 通知
			tcpLongListener.onReLogin();
		}

		@Override
		public void onReInitFailure() {
			// 通知
			tcpLongListener.onReLoginResult(false);
		}

	};

	// 心跳Timer
	private class HeatTimerTask extends TimerTask {

		@Override
		public void run() {
			System.out.println("TCP长连接-->心跳了一次");
			try {
				socket.getOutputStream().write(new byte[]{1, 1, 1, 1});
				socket.getOutputStream().flush();
			}
			catch (IOException e) {
			}
		}
	}

	private static final byte[] byteCopy(byte[] byte1, int byte1Length, byte[] byte2, int byte2Length) {
		byte[] byteTemp_receiveByte = new byte[byte1Length + byte2Length];
		System.arraycopy(byte1, 0, byteTemp_receiveByte, 0, byte1Length);
		System.arraycopy(byte2, 0, byteTemp_receiveByte, byte1Length, byte2Length);
		return byteTemp_receiveByte;
	}

}
