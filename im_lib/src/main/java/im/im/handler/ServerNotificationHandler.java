package im.im.handler;

import android.annotation.SuppressLint;
import android.util.Log;

import im.bean.AppMessage;
import im.bean.SingleMessage;
import im.event.CEventCenter;
import im.event.Events;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/8/269:09
 * desc   : 服务端发送的杂七杂八的消息处理，  比如禁用设备，刷新应用列表等等
 * version: 1.0
 */
public class ServerNotificationHandler extends AbstractMessageHandler {


    @SuppressLint("LongLogTag")
    @Override
    protected void action(AppMessage message) {
        Log.d("ServerNotificationHandler", "收到服务端推送的设备管理推送，message=" + message);

        SingleMessage msg = new SingleMessage();
        msg.setMsgId(message.getHead().getMsgId());
        msg.setMsgType(message.getHead().getMsgType());
        msg.setMsgContentType(message.getHead().getMsgContentType());
        msg.setFromId(message.getHead().getFromId());
        msg.setToId(message.getHead().getToId());
        msg.setTimestamp(message.getHead().getTimestamp());
        msg.setExtend(message.getHead().getExtend());
        msg.setContent(message.getBody());


        CEventCenter.dispatchEvent(Events.SERVER_NOTIFICATION, 0, 0, msg);
    }
}
