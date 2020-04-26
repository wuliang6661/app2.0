package synway.module_interface.invoke;

import android.app.Activity;

/**
 * Created by zjw on 2016/12/5.
 */

public abstract class HandlerVideoPlayer {
    public ResultLsn lsn = null;

    public void setLsn(ResultLsn lsn) {
        this.lsn = lsn;
    }

    public interface ResultLsn {
        void onResult();
    }
    public abstract  void goVideoPlayerAct(Activity act, String url);
}
