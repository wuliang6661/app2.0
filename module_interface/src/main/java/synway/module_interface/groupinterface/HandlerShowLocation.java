package synway.module_interface.groupinterface;

import android.content.Context;

/**
 * Created by zjw on 2017/5/22.
 * 跳转地图显示界面
 */

public abstract class HandlerShowLocation {
    //目标群id 地图消息JSON String
    public abstract  void goLocationAct(Context context, String targetID,String jsonString);
}
