package synway.module_interface.invoke;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by zjw on 2016/12/2.
 */

public abstract class HandlerSelectContact {
    public  ResultLsn lsn = null;

    public void setLsn(ResultLsn lsn) {
        this.lsn = lsn;
    }

    public interface ResultLsn {
        void onResult(ArrayList<String> userList);
    }
    //    public
    public abstract  void goContactSelectAct(int REQUEST_CODE,Activity act, ArrayList<String> userList,boolean isSupportRemote);

}
