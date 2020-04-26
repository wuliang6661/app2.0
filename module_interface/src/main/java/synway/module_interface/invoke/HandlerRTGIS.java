package synway.module_interface.invoke;

import android.app.Activity;

/**
 * Created by zjw on 2016/12/6.
 */

public abstract class HandlerRTGIS {
    public ResultLsn lsn = null;

    public void setLsn(ResultLsn lsn) {
        this.lsn = lsn;
    }

    public interface ResultLsn {
        void onResult();
    }
    public abstract  void goRTGISAct(Activity act, String publicGUID);
}
