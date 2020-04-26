package qyc.library.tool.tcp_short;

import qyc.library.tool.main_thread.MainThread;
import qyc.library.tool.main_thread.MainThread.Runnable_MainThread;

/** 针对TCP短连接更深一层的封装,把它封装成了类似于HttpInThread的方式 */
public class TcpShort {

	private String IP;
	private int port;
	private long outTime = 8000;
	private TcpShortAdapter adapter = null;
	private OnTcpShortResult onTcpShortResult = null;
	private Object listenLock = new Object();

	/** TCP短连接,发一收一然后断开 */
	public TcpShort() {

	}

	/** TCP短连接,发一收一然后断开 */
	public TcpShort(String IP, int port) {
		this.IP = IP;
		this.port = port;
	}

	/** TCP短连接,发一收一然后断开 */
	public TcpShort(String IP, int port, long milliseconds) {
		this.IP = IP;
		this.port = port;
		this.outTime = milliseconds;
	}

	/** 设置TCP接收反馈数据的超时时间,默认8秒 */
	public void setOutTime(long milliseconds) {
		this.outTime = milliseconds;
	}

	/** 设置解码器 TCP需要知道数据是否已经收完,收完后它就会断开.如果不设置解码器,收到任何数据后就会立即断开 */
	public void setAdapter(TcpShortAdapter adapter) {
		this.adapter = adapter;
	}

	/** 设置短连接结果监听 */
	public void setOnTcpShortListener(OnTcpShortResult onTcpShortResult) {
		synchronized (listenLock) {
			this.onTcpShortResult = onTcpShortResult;
		}
	}

	/** 取消短连接结果监听 */
	public void removeTcpShortListener() {
		synchronized (listenLock) {
			this.onTcpShortResult = null;
		}
	}

	public void postByte(int id, byte[] data) {
		new Thread(new TcpShortThread(id, IP, port, data, adapter, outTime, null)).start();
    }

	public void postByte(int id, byte[] data, TcpShortTakeObj takeObj) {
		new Thread(new TcpShortThread(id, IP, port, data, adapter, outTime, takeObj)).start();
    }

	public void postByte(int id, byte[] data, TcpShortTakeObj takeObj, long outTime) {
		new Thread(new TcpShortThread(id, IP, port, data, adapter, outTime, takeObj)).start();
    }

	private class TcpShortThread implements Runnable {
		// 这些参数和全局独立开,是为了真正执行的部分更加松散.使公开函数的参数更自由.
		private int id;
		private String ip;
		private int port;
		private byte[] data;
		private TcpShortAdapter adapter;
		private long outTime;
		private TcpShortTakeObj takeObj;

		public TcpShortThread(int id, String ip, int port, byte[] data, TcpShortAdapter adapter, long outTime,
				TcpShortTakeObj takeObj) {
			this.id = id;
			this.ip = ip;
			this.port = port;
			this.data = data;
			this.adapter = adapter;
			this.outTime = outTime;
			this.takeObj = takeObj;
		}

		@Override
		public void run() {
			TcpShortClass shortClass = new TcpShortClass();
			Object tcpShprtResult = shortClass.post(ip, port, data, outTime, adapter);
			synchronized (listenLock) {
				if (onTcpShortResult != null) {
					boolean turnToMainThread = onTcpShortResult.onTcpShortResult(tcpShprtResult, id, takeObj);
					if (turnToMainThread) {
						MainThread.joinMainThread(new ToMainThread(tcpShprtResult, id, takeObj));
					}
				}
			}
		}
	}

	private class ToMainThread implements Runnable_MainThread {
		private Object resultObj = null;
		private int id;
		private TcpShortTakeObj takeObj;

		public ToMainThread(Object resultObj, int id, TcpShortTakeObj takeObj) {
			this.resultObj = resultObj;
			this.id = id;
			this.takeObj = takeObj;
		}

		@Override
		public void run() {
			synchronized (listenLock) {
				if (onTcpShortResult != null) {
					onTcpShortResult.onTcpShortResult_MainThead(resultObj, id, takeObj);
				}
			}
		}
	}

	public interface OnTcpShortResult {

		boolean onTcpShortResult(Object resultObj, int id, TcpShortTakeObj takeObj);

		void onTcpShortResult_MainThead(Object resultObj, int id, TcpShortTakeObj takeObj);
	}

}
