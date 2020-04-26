package synway.common.tool;

import android.app.Activity;

/**
 * Created by zjw on 2017/3/9.
 * 为了解决Acitivity的onDestroy方法回调比较缓慢，可能导致的生命周期紊乱
 */
public abstract class ActivityQD extends Activity {
    private boolean isDestroyed = false;

    /**
     * 为了解决Acitivity的onDestroy方法回调比较缓慢，在onPause执行的自定义资源回收
     * 界面onpause后，并非是关闭界面，而是锁屏或者切回桌面等操作，之后由系统回收内存，导致onpause
     */
    public abstract void destroyQD();

    private void destroy() {
        if (isDestroyed) {
            return;
        }
        // 回收资源
        destroyQD();
        isDestroyed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            destroy();
        }
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }
}
