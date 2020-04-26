package cn.synway.app.push.im_listener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.synway.app.bean.event.RefreshMsgEvent;
import cn.synway.app.db.dbmanager.MessageIml;
import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.widget.imutil.Constants;
import im.bean.ServerStateMessage;
import im.event.Events;
import im.event.I_CEventListener;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2316:59
 * desc   :  收到消息的发送状态报告
 * version: 1.0
 */
public class ChatServerStateListener implements I_CEventListener {
    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        switch (topic) {
            case Events.CHAT_SERVER_STATE: {
                ServerStateMessage message = (ServerStateMessage) obj;
                List<MessageEntry> entries = MessageIml.getDataByMsgId(message.getMsgId());
                if (entries.isEmpty()) {
                    return;
                }
                MessageEntry item = entries.get(0);
                if (message.getStatusReport() == 0) {  //发送失败
                    item.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
                } else {
                    item.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                }
                MessageIml.addData(item);
                EventBus.getDefault().post(new RefreshMsgEvent());
                break;
            }
            default:
                break;
        }
    }
}
