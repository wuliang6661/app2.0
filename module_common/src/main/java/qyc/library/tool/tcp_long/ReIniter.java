package qyc.library.tool.tcp_long;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import qyc.library.tool.tcp_long.TcpLongAdapter.AdapterResult;

/** 重注册助手,它的生命周期为 fly---连接成功或fly---drop 完成生命周期后它不能再次使用,必须整个重新初始化 */
class ReIniter {

	private boolean isDrop = false;
	private String IP;
	private int port;
	private byte[] initData;
	private TcpLongAdapter adapter;
	private Timer reInitTimer = null;
	private OnReInitResult onReInitResult = null;

	ReIniter(String IP, int port, byte[] initData, TcpLongAdapter adapter,
			OnReInitResult onReInitResult) {
		this.IP = IP;
		this.port = port;
		this.initData = initData;
		this.adapter = adapter;
		this.onReInitResult = onReInitResult;
	}

	public void fly() {
		if (isDrop) {
			Integer.valueOf("A");// 崩死你,娘希匹~
		}
		reInitTimer = new Timer();
		reInitTimer.schedule(new ReInitTimer(), 3000, 3000);
	}

	/**
	 * <p>
	 * 如果已经废弃,drop()什么都不干. 如果从未启用,drop()什么都不干. 如果处于重连的间隔,drop()将终止Timer使重连不再启动.
	 * 如果正处于重连中,drop()会使重连完成后又断开socket,并不再反馈任何信息.
	 * <p>
	 * drop后整个对象都不可以再用,说到这里,我已经有了一种失恋的感觉.
	 * <p>
	 * 当你用new一个Reiniter进行fly前,可以先调用旧Reiniter执行一次drop.它不会阻塞,在任何状态下都可以调用.
	 * */
	public void drop() {
		System.out.println("TCP长连接封装:重连被人为放弃");
		isDrop = true;
		if (reInitTimer != null) {
			reInitTimer.cancel();
		}
	}

	// 重连Timer
	private class ReInitTimer extends TimerTask {
		// 重连的过程其实就是注册的过程,重连后会形成全新的socket,旧的socket则废弃.在重连被接收线程触发之前,接收线程已经废弃了socket
		// 重连和注册的微小区别在于它不返回结果,而是通知观察者.
		// 它不改变state,因为接下来还会继续连.
		// 连接成功会启动接收线程
		// 它连接失败拿不到新socket,也不置空已废弃的socket,这是为了重连过程中能够确保destory正常调用(即使调用的是已废弃的socket).
		@Override
		public void run() {
			System.out.println("TCP长连接封装:开始执行重接动作");
			// 通知
			onReInitResult.onReInitStart();

			InitRunnable initRunnable = new InitRunnable(IP, port, initData,
					5000, adapter);
			Thread thread = new Thread(initRunnable);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
			AdapterResult initResult = initRunnable.initResult;
			if (!(initResult.result instanceof TcpLongInitFail)) {
				// 如果初始化是成功的,那么拿出socket
				Socket socket = initRunnable.socket;
				if (isDrop) {
					System.out.println("TCP长连接封装:重连动作执行成功,但已经弃用,接下来将断开这个连接");
					try {
						socket.close();
					} catch (IOException e) {
					}
				} else {
					System.out.println("TCP长连接封装:重连动作执行成功,反馈出socket,并自我弃用.");
					isDrop = true;
					reInitTimer.cancel();// 关闭重连计时器
					onReInitResult.onReInitSuccess(socket, initData,initResult.overData);
				}
			} else {
				System.out.println("TCP长连接封装:重连动作执行失败.");
				if (!isDrop) {
					onReInitResult.onReInitFailure();
				}
			}
		}
	}

	interface OnReInitResult {
		void onReInitStart();

		void onReInitFailure();

		void onReInitSuccess(Socket newSocket, byte[] reInitData,
                             byte[] initOverData);
	}

}
