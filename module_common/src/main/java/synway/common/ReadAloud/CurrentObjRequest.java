package synway.common.ReadAloud;
import android.os.Handler;


/**
 * 用于反馈开始
 * Created by 钱园超 on 2018/7/10.
 */
public class CurrentObjRequest {

    private OnReadAloundListener onReadAloundListener = null;
    private ReadAloudObj readAloudObj;
    private Handler handler;

    private volatile boolean isCanceled = false;
    private volatile boolean isStartRequested = false;

    CurrentObjRequest(ReadAloudObj readAloudObj, OnReadAloundListener onReadAloundListener, Handler handler) {
        this.onReadAloundListener = onReadAloundListener;
        this.readAloudObj = readAloudObj;
        this.handler = handler;
    }

    public ReadAloudObj readAloudObj() {
        return readAloudObj;
    }

    public boolean prepare() {
        if (!isCanceled) {
            return this.onReadAloundListener.onPrepare(readAloudObj);
        } else {
            return false;
        }
    }

    public synchronized void start() {
        if (!isCanceled) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!isCanceled) {
                        isStartRequested = true;
                        onReadAloundListener.onStartRead(readAloudObj);
                    }
                }
            });
        }
    }

    public synchronized void finish() {
        //Log.d("QYC",readAloudObj.getId()+"->finish");
        if (isStartRequested && !isCanceled) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onReadAloundListener.onFinishRead(readAloudObj);
                }
            });
        }
        isCanceled = true;
    }

    public synchronized boolean isFinish()
    {
        return isCanceled;
    }


}
