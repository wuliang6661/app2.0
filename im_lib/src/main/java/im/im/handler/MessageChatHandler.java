package im.im.handler;

import im.bean.AppMessage;
import im.bean.SingleMessage;
import im.event.CEventCenter;
import im.event.Events;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2215:52
 * desc   :  单聊消息分发
 * version: 1.0
 */
public class MessageChatHandler extends AbstractMessageHandler {


    @Override
    protected void action(AppMessage message) {


        SingleMessage msg = new SingleMessage();
        msg.setMsgId(message.getHead().getMsgId());
        msg.setMsgType(message.getHead().getMsgType());
        msg.setMsgContentType(message.getHead().getMsgContentType());
        msg.setFromId(message.getHead().getFromId());
        msg.setToId(message.getHead().getToId());
        msg.setTimestamp(message.getHead().getTimestamp());
        msg.setExtend(message.getHead().getExtend());
        msg.setContent(message.getBody());


        CEventCenter.dispatchEvent(Events.CHAT_FRIEND_MESSAGE, 0, 0, msg);
    }
}
