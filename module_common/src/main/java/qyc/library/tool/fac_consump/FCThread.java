package qyc.library.tool.fac_consump;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 工厂消费模式封装的简易类。它有一个专门用于处理数据的线程，平时处于休眠状态，并不消耗系统资源。一旦有数据便启动运行。
 *
 * @author 钱园超 2016年3月28日上午11:22:29
 * @author 钱园超 2016年7月15日下午17:00:00 修复一个BUG,导致调用stop后可能继续处理消息.
 */
public class FCThread<T extends Object> {
    private LinkedBlockingQueue<T> msgs = null;
    private OnFCThreadRun<T> onFCThreadRun = null;
    private Thread thread = null;
    private boolean isStart = false;

    public FCThread(OnFCThreadRun<T> onFCThreadRun) {
        this.onFCThreadRun = onFCThreadRun;
        this.msgs = new LinkedBlockingQueue<>();
    }

    public FCThread(OnFCThreadRun<T> onFCThreadRun, int maxLength) {
        this.onFCThreadRun = onFCThreadRun;
        this.msgs = new LinkedBlockingQueue<>(maxLength);
    }

    public synchronized void start() {
        // 启动消息处理线程
        isStart = true;
        thread = new Thread(runnable);
        thread.start();
    }

    public void stop() {
        isStart = false;
        // 取消数据等待
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    public void add(T t) {
        if (!isStart) {
            return;
        }

        if (t == null) {
            return;
        }

        try {
            msgs.put(t);
        } catch (InterruptedException e) {
        }
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            onFCThreadRun.threadStart();

            deal:
            {
                while (true) {
                    // 获取数据模块
                    T t = null;

                    try {
                        if (isStart) {
                            t = msgs.take();
                        } else {
                            msgs.clear();
                            break deal;
                        }
                    } catch (InterruptedException e) {
                        msgs.clear();
                        break deal;
                    }

                    // 处理
                    onFCThreadRun.dealData(t);
                }
            }

            onFCThreadRun.threadStop();
        }
    };

    public interface OnFCThreadRun<T extends Object> {
        /**
         * 线程开始,可以初始化一些资源
         */
        void threadStart();

        /**
         * 线程开始运行
         */
        void dealData(T data);

        /**
         * 线程结束,可以释放一些资源
         */
        void threadStop();
    }
}
