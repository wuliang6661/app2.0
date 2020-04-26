package qyc.library.tool.webservice;

import org.ksoap2.serialization.SoapObject;

import java.util.HashMap;

import qyc.library.tool.main_thread.MainThread;
import qyc.library.tool.main_thread.MainThread.Runnable_MainThread;

/**
 * 用来访问webservice
 * 
 * 访问的时候单独开一个线程，可以监听webservice的返回结果，并跨越到主线程
 * 
 * 使用者请根据自己的情况替换掉访问线程中的ResultParser。这个类主要是帮助解析Webservice返回的Soap对象，提取json字符串
 * 
 * 
 */
public class WSInThread {
	// 访问webservice必须的参数
	private String nameSpace = "";
	private String url = "";
	private String methodName = "";
	private String soapAction = "";
	private HashMap<String, Object> params = null;

	// 超时时间，单位秒
	private int timeout = 0;

	// 防止并发
	private Object listenLock = new Object();
	// 结果监听
	private OnResultListener onResultListener = null;

	// 构造函数
	public WSInThread() {
	}

	public WSInThread(String url, String nameSpace, String methodName, String soapAction, HashMap<String, Object> params) {
		setViariables(url, nameSpace, methodName, soapAction, params);
	}

	public void setViariables(String url, String nameSpace, String methodName, String soapAction,
			HashMap<String, Object> params) {
		this.nameSpace = nameSpace;
		this.url = url;
		this.methodName = methodName;
		this.soapAction = soapAction;
		this.params = params;
	}

	/** 设置超时时间，单位秒 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	// 开启线程，从webservice获取数据
	public void onDownload() {
		new Thread(new WebserviceRunnable(url, nameSpace, methodName, soapAction, params)).start();
	}

	/** 真正访问webservice的线程 */
	private class WebserviceRunnable implements Runnable {
		private String nameSpace = "";
		private String url = "";
		private String methodName = "";
		private String soapAction = "";
		private HashMap<String, Object> params = null;

		public WebserviceRunnable(String url, String nameSpace, String methodName, String soapAction,
				HashMap<String, Object> params) {
			this.nameSpace = nameSpace;
			this.url = url;
			this.methodName = methodName;
			this.soapAction = soapAction;
			this.params = params;
		}

		@Override
		public void run() {
			/** 阻塞连接,等待接收webservice结果，默认有超时连接，底层其实是SOCKET */
			SoapObject so = WSConnHelper.getSoapObject(url, nameSpace, methodName, soapAction, params, timeout);

			// /** 提取webservice端返回结果中的JSON字符串 */
			// JSONObject jsonObject = WSResultParser.getJSONObject(so);

			/** 降低网速 */
			// try {
			// Thread.sleep(3000);
			// } catch (Exception e) {
			// }
			/** 降低网速 */

			// 防止返回结果的时候和退出的时候发生并发
			synchronized (listenLock) {
				if (onResultListener != null) {
					boolean turnToMainThread = onResultListener.onDonwloadResult(so);
					if (turnToMainThread) {
						MainThread.joinMainThread(new ToMainThread(so));
					}
				}
			}
		}
	}

	/** 设置webservice返回结果监听 */
	public void setOnResultListener(OnResultListener l) {
		synchronized (listenLock) {
			onResultListener = l;
		}
	}

	/** 移除webservice返回结果监听 */
	public void removeOnResultListener() {
		synchronized (listenLock) {
			onResultListener = null;
		}
	}

	public interface OnResultListener {

		boolean onDonwloadResult(SoapObject so);

		void onHttpResult_MainThead(SoapObject so);
	}

	private class ToMainThread implements Runnable_MainThread {
		SoapObject so = null;

		public ToMainThread(SoapObject so) {
			this.so = so;
		}

		@Override
		public void run() {
			synchronized (listenLock) {
				// 当结果返回的时候，可能界面已经退出，onResultListener被置为null
				// 虽然跨越到主线程的代码加了锁，但那只不过是一瞬
				// 不会作用到这里，所以要加一个空的判断
				// 避免onResultListener == null的情况
				if (onResultListener != null) {
					onResultListener.onHttpResult_MainThead(so);
				}
			}
		}
	}

}
