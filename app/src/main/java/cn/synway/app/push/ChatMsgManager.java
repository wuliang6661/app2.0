package cn.synway.app.push;

import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.UUID;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.event.RefreshMsgEvent;
import cn.synway.app.db.dbmanager.MessageIml;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.push.im_listener.ChatFriendListener;
import cn.synway.app.widget.imutil.Constants;
import im.bean.SingleMessage;
import im.im.MessageProcessor;
import im.im.MessageType;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2314:05
 * desc   :  消息发送管理器
 * version: 1.0
 */
public class ChatMsgManager {

    private static ChatMsgManager chatManager;

    public static ChatMsgManager getInstance() {
        if (chatManager == null) {
            chatManager = new ChatMsgManager();
        }
        return chatManager;
    }


    /**
     * 发送一个文字消息
     */
    public void sendTextMsg(UserEntry toUser, MessageEntry content) {
        SingleMessage message = new SingleMessage();
        message.setMsgId(content.getMsgId());
        message.setMsgType(MessageType.MESSAGE_CHAT.getMsgType());
        message.setMsgContentType(MessageType.MessageContentType.TEXT.getMsgContentType());
        message.setFromId(UserIml.getUser().getUserID());
        message.setToId(toUser.getUserID());
        message.setTimestamp(System.currentTimeMillis());
        String msg = new Gson().toJson(content);
        message.setContent(msg);

        //存储消息
        MessageIml.addData(content);
        ChatFriendListener.recentManager(toUser, content);

        MessageProcessor.getInstance().sendMsg(message);
    }


    /**
     * 发送一个语音消息
     */
    public void sendVoiceMsg(UserEntry toUser, MessageEntry content) {
        MsgFile file = new MsgFile(new onUpdateListener() {
            @Override
            public void onSourss(String fileUrl) {
                content.setFilepath(fileUrl);
                sendTextMsg(toUser, content);
            }

            @Override
            public void onFiled() {
                content.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
                //存储消息
                MessageIml.addData(content);
                ChatFriendListener.recentManager(toUser, content);
                EventBus.getDefault().post(new RefreshMsgEvent());
            }
        });
        file.updateFile(content.filepath);
    }


    /**
     * 发送一个图片信息
     */
    public void sendImageMsg(UserEntry toUser, MessageEntry content) {
        MsgFile file = new MsgFile(new onUpdateListener() {
            @Override
            public void onSourss(String fileUrl) {
                content.setFilepath(fileUrl);
                sendTextMsg(toUser, content);
            }

            @Override
            public void onFiled() {
                content.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
                //存储消息
                MessageIml.addData(content);
                ChatFriendListener.recentManager(toUser, content);
                EventBus.getDefault().post(new RefreshMsgEvent());
            }
        });
        file.updateImage(content.filepath);
    }


    /**
     * 发送一个文件消息
     */
    public void sendFileMsg(UserEntry toUser, MessageEntry content) {
        sendVoiceMsg(toUser, content);
    }


    /**
     * 发送一条阅后即焚消息
     */
    public void sendSnap(UserEntry toUser, MessageEntry content) {
        MsgFile file = new MsgFile(new onUpdateListener() {
            @Override
            public void onSourss(String fileUrl) {
                content.setFilepath(fileUrl);
                SingleMessage message = new SingleMessage();
                message.setMsgId(content.getMsgId());
                message.setMsgType(MessageType.MESSAGE_CHAT.getMsgType());
                message.setMsgContentType(MessageType.MessageContentType.SNAP_CHAT.getMsgContentType());
                message.setFromId(UserIml.getUser().getUserID());
                message.setToId(toUser.getUserID());
                message.setTimestamp(System.currentTimeMillis());
                String msg = new Gson().toJson(content);
                message.setContent(msg);

                //存储消息
                MessageIml.addData(content);
                ChatFriendListener.recentManager(toUser, content);

                MessageProcessor.getInstance().sendMsg(message);
            }

            @Override
            public void onFiled() {
                content.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
                //存储消息
                MessageIml.addData(content);
                ChatFriendListener.recentManager(toUser, content);
                EventBus.getDefault().post(new RefreshMsgEvent());
            }
        });
        file.updateImage(content.filepath);
    }


    /**
     * 发送一条阅后即焚的已读消息
     */
    public void sendSnapRead(UserEntry toUser, MessageEntry content) {
        SingleMessage message = new SingleMessage();
        message.setMsgId(content.getMsgId());
        message.setMsgType(MessageType.MESSAGE_CHAT.getMsgType());
        message.setMsgContentType(MessageType.MessageContentType.SNAP_READ_CHAT.getMsgContentType());
        message.setFromId(UserIml.getUser().getUserID());
        message.setToId(toUser.getUserID());
        message.setTimestamp(System.currentTimeMillis());
        String msg = new Gson().toJson(content);
        message.setContent(msg);

        MessageProcessor.getInstance().sendMsg(message);

        MessageIml.deleteByMsgId(content.getMsgId());   //删除当前消息
        ChatFriendListener.recentClip(content.getRecentId());
        EventBus.getDefault().post(new RefreshMsgEvent());
    }


    /**
     * 发送一条分享链接的消息
     */
    public void sendShareLink(UserEntry toUser, String shareTitle, String shareMessage
            , String shareImg, String shareUrl) {
        MessageEntry content = new MessageEntry();
        content.setFileType(Constants.CHAT_FILE_TYPE_LINK);
        content.setContent(shareTitle + "%" + shareMessage + "%" + shareImg + "%" + shareUrl);
        content.setHeader(UserIml.getUser().getUserPic());  //发送人头像
        content.setSendUserId(UserIml.getUser().getUserID());  // 发送人id
        content.setSendUserName(UserIml.getUser().getUserName());   //发送人名字
        content.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        content.setSendState(Constants.CHAT_ITEM_SENDING);
        content.setRecentId(toUser.getUserID());
        content.setTime(TimeUtils.getNowString());
        content.setMsgId(UUID.randomUUID().toString());

        sendLink(toUser, content);
    }

    /**
     * 发送网页
     */
    public void sendLink(UserEntry toUser, MessageEntry content) {
        SingleMessage message = new SingleMessage();
        message.setMsgId(content.getMsgId());
        message.setMsgType(MessageType.MESSAGE_CHAT.getMsgType());
        message.setMsgContentType(MessageType.MessageContentType.SHARE_LINK.getMsgContentType());
        message.setFromId(UserIml.getUser().getUserID());
        message.setToId(toUser.getUserID());
        message.setTimestamp(System.currentTimeMillis());
        String msg = new Gson().toJson(content);
        message.setContent(msg);

        //存储消息
        MessageIml.addData(content);
        ChatFriendListener.recentManager(toUser, content);
        EventBus.getDefault().post(new RefreshMsgEvent());

        MessageProcessor.getInstance().sendMsg(message);
    }


    /**
     * 上传文件
     */
    class MsgFile {

        private onUpdateListener listener;

        MsgFile(onUpdateListener listener) {
            this.listener = listener;
        }

        /**
         * 上传文件
         */
        void updateFile(String filePath) {
            HttpServerImpl.imUpdateFile(filePath).subscribe(new HttpResultSubscriber<String>() {
                @Override
                public void onSuccess(String s) {
                    if (listener != null) {
                        listener.onSourss(s);
                    }
                }

                @Override
                public void onFiled(String message) {
                    if (listener != null) {
                        listener.onFiled();
                    }
                }
            });
        }

        /**
         * 上传图片
         */
        void updateImage(String imagePath) {
            File file = new File(imagePath);
            HttpServerImpl.imUpdateImage(file).subscribe(new HttpResultSubscriber<String>() {
                @Override
                public void onSuccess(String s) {
                    if (listener != null) {
                        listener.onSourss(s);
                    }
                }

                @Override
                public void onFiled(String message) {
                    if (listener != null) {
                        listener.onFiled();
                    }
                }
            });
        }
    }


    interface onUpdateListener {

        void onSourss(String fileUrl);

        void onFiled();
    }

}
