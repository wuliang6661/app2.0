package synway.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 系统运行时统一使用的线程池,任何业务都在这里开辟新线程,以便统一管理线程的生命周期.
 * <p>
 *
 * @author 钱园超 2016年11月11日 下午3:36:21
 */
public final class ThreadPool {

	private static ThreadPool threadPool = new ThreadPool();

	public static ThreadPool instance() {
		return threadPool;
	}

	private ExecutorService executorService = null;

	public void start() {
		executorService = Executors.newCachedThreadPool();
	}

	public void stop() {
		executorService.shutdownNow();
	}

	public Future<?> submit(Runnable task) {
		return executorService.submit(task);
	}

	public <T>Future<T> submit(Callable<T> task) {
		return executorService.submit(task);
	}

	public void execute(Runnable task) {
		executorService.execute(task);
	}

}
