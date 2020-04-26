package synway.common.ReadAloud;

/**
 * 等待初始化完成的锁
 * Created by 钱园超 on 2018/8/3.
 */
public class InitLock {

    private volatile boolean isFinish = false;


    public void setFinish() {
        isFinish = true;
        synchronized (this) {
            notify();
        }
    }

    public void waitUntilFinish() {
        if (isFinish) {
            return;
        }
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                //
            }
        }
    }
}
