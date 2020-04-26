package synway.module_publicaccount;


public abstract class WatchThread extends Thread {
    /** 该线程存活标志，kill()方法将该标志置为false。 */
    private boolean alive = true;
    public static final ThreadGroup tg = new ThreadGroup("watch-thread");
    /** 当前线程状态信息。用于告知外界该线程正在做什么。 */
    private String status = null;
    /** 该类的所有子类对象均创建到这个线程组中。 */
    /**
     * 构造函数，提供一个线程名参数。构造方法只创建线程，并不启动。
     *
     * @param name
     *            线程的名字，为线程起个好名字对调试和日志记录很有帮助。
     */
    public WatchThread(String name) {
        super(tg, name);
        setDaemon(true); // 设置成精灵线程（程序在只剩下精灵线程运行时将自动结束）
    }
    /**
     * 杀死该线程的方法，将alive标志置为false，当run()方法的while循环发现该标志为
     * false时将跳出循环结束线程。需注意的是kill()方法返回时并不一定线程立即死掉。 要等到线程主体从一次task()方法返回后才会结束。
     */
    public void kill() {
        alive = false;
    }

    /**
     * 线程主体，循环运行task()方法，直到调用了kill()方法。
     */
    /**
     * public void run() {
     *
     * //无论出现什么异常都不能使该线程终止！ while (alive) { try { task(); } catch (Exception ex)
     * { ex.printStackTrace(); } catch (Throwable t) { //出现严重错误，搞不好系统会死掉
     * t.printStackTrace(); } } }
     **/

    public void run() {
        // 无论出现什么异常都不能使该线程终止！
        while (alive) {
            try {
                task();
            } catch (Exception ex) {
                ex.printStackTrace();
            } catch (Throwable t) { // 出现严重错误，搞不好系统会死掉
                t.printStackTrace();
            }
        }
    }

    /**
     * 设置状态信息。用来告诉外界该线程正在干什么。
     *
     * @param state
     *            新的状态信息。
     */
    protected void setStatus(String newStatus) {
        this.status = newStatus;
    }

    /**
     * 获取状态信息。告诉外界该线程正在干什么。
     *
     * @return 状态信息。
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * 子类必须覆盖的抽象方法，需要循环做的事情。
     */
    abstract protected void task();
}
