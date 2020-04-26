package synway.module_interface.groupinterface;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by zjw on 2016/12/2.
 */

public abstract class HandlerMyLocation {
    public  ResultLsn lsn = null;

    public void setLsn(ResultLsn lsn) {
        this.lsn = lsn;
    }

    public interface ResultLsn {
         void onResult(ArrayList<String> userList);
    }


    public abstract  void goLocationAct(int REQUEST_CODE,Activity act, String targetID,String savePath);



    //返回的截图的路径
    public static final String INTENT_KEY_PIC_PATH = "PIC_PATH";
    //返回的位置
    public static final String INTENT_KEY_JSONARRAY = "JSONARRAY";

}
