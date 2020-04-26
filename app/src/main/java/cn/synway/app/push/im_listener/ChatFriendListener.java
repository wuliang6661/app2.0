package cn.synway.app.push.im_listener;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.synway.app.bean.event.RefreshMsgEvent;
import cn.synway.app.config.Config;
import cn.synway.app.db.dbmanager.MessageIml;
import cn.synway.app.db.dbmanager.RecentContactIml;
import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.db.table.RecentContactsEntry;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.push.NotifacationUtils;
import cn.synway.app.ui.chat.ImActivity;
import cn.synway.app.utils.AppManager;
import cn.synway.app.widget.imutil.Constants;
import im.bean.SingleMessage;
import im.event.Events;
import im.event.I_CEventListener;
import im.im.MessageType;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2215:59
 * desc   :  收到单聊消息的事件监听
 * version: 1.0
 */
public class ChatFriendListener implements I_CEventListener {


    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        switch (topic) {
            case Events.CHAT_FRIEND_MESSAGE: {
                final SingleMessage message = (SingleMessage) obj;
                if (StringUtils.isEmpty(message.getContent())) {
                    return;
                }
                MessageEntry entry = new Gson().fromJson(message.getContent(), MessageEntry.class);
                if (message.getMsgContentType() == MessageType.MessageContentType.
                        SNAP_READ_CHAT.getMsgContentType()) {   //消息类型为阅后即焚的已读消息
                    MessageIml.deleteByMsgId(entry.getMsgId());
                    //修改会话最后一条消息的关联
                    recentClip(message.getFromId());
                } else {
                    entry.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                    entry.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                    entry.setRecentId(entry.getSendUserId());
                    entry.id = 0;
                    MessageIml.addData(entry);
                    //会话消息处理
                    RecentContactsEntry item = recentManager(entry);
                    //消息在后台的处理
                    appBackMsg(entry, item);
                    //播放提示音
                    playNewMsgVoice();
                }
                EventBus.getDefault().post(new RefreshMsgEvent());
                break;
            }
            default:
                break;
        }
    }

    /**
     * 修改会话的最后一条消息关联
     */
    public static void recentClip(String recentId) {
        List<RecentContactsEntry> recents = RecentContactIml.getDataByContactId(recentId);
        if (!recents.isEmpty()) {
            RecentContactsEntry item = recents.get(0);
            item.msgLocalTime = System.currentTimeMillis();
            List<MessageEntry> messageEntries = MessageIml.getDataByRecentId(recentId);
            if (messageEntries.isEmpty()) {
                item.chatMsg.setTarget(null);
            } else {
                item.chatMsg.setTarget(messageEntries.get(messageEntries.size() - 1));
            }
            RecentContactIml.addData(item);
        }
    }


    /**
     * 收到消息时创建会话列表
     */
    public static RecentContactsEntry recentManager(MessageEntry entry) {
        //查看是否该会话列表已经在表中，如果不在表中，加入表，如果在，则更新最后一条消息
        List<RecentContactsEntry> recents = RecentContactIml.getDataByContactId(entry.getRecentId());
        if (recents.isEmpty()) {
            RecentContactsEntry item = new RecentContactsEntry();
            item.chatMsg.setTarget(entry);
            item.contactHeadUrl = entry.getHeader();
            item.contactId = entry.getRecentId();
            item.contactName = entry.getSendUserName();
            item.contactType = "0";  //单聊
            item.msgLocalTime = System.currentTimeMillis();
            if (!(AppManager.getAppManager().curremtActivity() instanceof ImActivity)) {   //如果不在在聊天界面
                item.unReadNum += 1;
            }
            RecentContactIml.addData(item);
            return item;
        } else {
            RecentContactsEntry item = recents.get(0);
            item.msgLocalTime = System.currentTimeMillis();
            item.chatMsg.setTarget(entry);
            if (!(AppManager.getAppManager().curremtActivity() instanceof ImActivity)) {   //如果不在在聊天界面
                item.unReadNum += 1;
            }
            RecentContactIml.addData(item);
            return item;
        }
    }

    /**
     * 发送消息时创建会话列表
     *
     * @param userBO 发送目标人的user
     * @param entry  发送的消息
     */
    public static void recentManager(UserEntry userBO, MessageEntry entry) {
        //查看是否该会话列表已经在表中，如果不在表中，加入表，如果在，则更新最后一条消息
        List<RecentContactsEntry> recents = RecentContactIml.getDataByContactId(entry.getRecentId());
        if (recents.isEmpty()) {
            RecentContactsEntry item = new RecentContactsEntry();
            item.chatMsg.setTarget(entry);
            item.contactHeadUrl = userBO.getUserPic();
            item.contactId = entry.getRecentId();
            item.contactName = userBO.getUserName();
            item.contactType = "0";  //单聊
            item.msgLocalTime = System.currentTimeMillis();
            if (!(AppManager.getAppManager().curremtActivity() instanceof ImActivity)) {   //如果不在在聊天界面
                item.unReadNum += 1;
            }
            RecentContactIml.addData(item);
        } else {
            RecentContactsEntry item = recents.get(0);
            item.msgLocalTime = System.currentTimeMillis();
            item.chatMsg.setTarget(entry);
            if (!(AppManager.getAppManager().curremtActivity() instanceof ImActivity)) {   //如果不在在聊天界面
                item.unReadNum += 1;
            }
            RecentContactIml.addData(item);
        }
    }


    /**
     * 如果app 在后台
     */
    private void appBackMsg(MessageEntry entry, RecentContactsEntry item) {
        if (Config.AppInBack) {
            NotifacationUtils.showNotification(Utils.getApp(), entry.getHeader(),
                    entry.getSendUserName(), entry.getSendUserName(),
                    "收到一条消息", (int) item.id);
        }
    }


    /**
     * 新消息来时，播放提示音并震动
     */
    private void playNewMsgVoice() {
        if (!(AppManager.getAppManager().curremtActivity() instanceof ImActivity)) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  //播放系统默认提示音
            Ringtone r = RingtoneManager.getRingtone(Utils.getApp(), notification);
            r.play();

            //手机震动
//                Vibrator vibrator = (Vibrator) Utils.getApp().getSystemService(VIBRATOR_SERVICE);
//                vibrator.vibrate(1000);
        }
    }

}
