package synway.module_interface.invoke;

import android.app.Activity;

import java.util.ArrayList;

import synway.module_interface.chatinterface.obj.ChatMsgObj;


/**
 * Created by zjw on 2016/12/2.
 * 消息转情报
 */

public abstract class HandlerIntelligence {
//    public ResultLsn lsn = null;
//
//    public void setLsn(ResultLsn lsn) {
//        this.lsn = lsn;
//    }
//
//    public abstract interface ResultLsn {
//        public void onResult();
//    }
    /**返回结果 boolean 跳转成功返回true,跳转失败返回false
     * @targetID 个人 群，公众号的id
     * @targetType 消息来源，个人:0,群:1,公众号:2
     * @chatMsgObj 待转换的消息
     *
     * */
    public  abstract  boolean goIntelligenceAct( Activity act,String targetID,int targetType, ArrayList<ChatMsgObj> chatMsgObj);



}
