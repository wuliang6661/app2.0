package im.im.handler;

import android.util.Log;

import im.bean.AppMessage;
import im.bean.ServerStateMessage;
import im.event.CEventCenter;
import im.event.Events;


/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       ServerReportMessageHandler.java</p>
 * <p>@PackageName:     com.freddy.chat.im.handler</p>
 * <b>
 * <p>@Description:     服务端返回的消息发送状态报告</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/22 19:16</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class ServerReportMessageHandler extends AbstractMessageHandler {

    private static final String TAG = ServerReportMessageHandler.class.getSimpleName();

    @Override
    protected void action(AppMessage message) {
        Log.d(TAG, "收到消息状态报告，message=" + message);

        ServerStateMessage msg = new ServerStateMessage();
        msg.setMsgId(message.getHead().getMsgId());
        msg.setStatusReport(message.getHead().getStatusReport());

        CEventCenter.dispatchEvent(Events.CHAT_SERVER_STATE, 0, 0, msg);
    }
}
