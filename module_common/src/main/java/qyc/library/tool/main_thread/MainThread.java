package qyc.library.tool.main_thread;

import android.os.Handler;
import android.os.Message;

/**
 * <p>
 * 主线程跨越
 * <p>
 * 请在你的Application里调用一次init()进行初始化
 * */
public class MainThread {

	// 如果直接在这里初始化,那么它真正初始化的时间是第一次被调用,因为它是一个静态变量
	// 所以这里只能定义,去APP里面初始化
	private static MainThreadHandler mainThreadHandler = null;

	private static final class MainThreadHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Runnable_MainThread mainThreadCode = (Runnable_MainThread) msg.obj;
			mainThreadCode.run();
			super.handleMessage(msg);
		}
	}

	public static void init() {
		mainThreadHandler = new MainThreadHandler();
	}

	/** 加入主线程队列 */
	public static void joinMainThread(Runnable_MainThread run) {
		Message msg = new Message();
		msg.obj = run;
		mainThreadHandler.sendMessage(msg);
	}

	/** 跨越到主线程的接口 */
	public interface Runnable_MainThread {
		/**
		 * 在主线程里运行的代码
		 */
        void run();
	}

}
