package synway.module_interface.invoke;

import android.app.Activity;
import android.support.v4.app.Fragment;


/**
 * Created by zjw on 2016/12/2.
 */

public abstract class HandlerUserInfo {
    public ResultLsn lsn = null;

    public void setLsn(ResultLsn lsn) {
        this.lsn = lsn;
    }

    public interface ResultLsn {
        void onResult();
    }
    //如果是fragment 界面打开activity,需要传递fragment，如果是Activity 调用该方法，fragment =null;
    public  abstract  void goUserInfoAct(int REQUEST_CODE, Activity act, String userID, Fragment fragment);



}
