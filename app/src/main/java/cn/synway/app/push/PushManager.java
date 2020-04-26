package cn.synway.app.push;

import android.util.Base64;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.freddy.im.IMSConfig;
import com.freddy.im.listener.IMSConnectStatusCallback;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import cn.synway.app.base.SynApplication;
import cn.synway.app.bean.event.KeepEvent;
import cn.synway.app.bean.event.MessageRefreshEvent;
import cn.synway.app.config.Config;
import cn.synway.app.push.data.PushBo;
import cn.synway.app.push.im_listener.ChatFriendListener;
import cn.synway.app.push.im_listener.ChatServerStateListener;
import cn.synway.app.push.im_listener.ServerNotificationListener;
import cn.synway.app.utils.AESUtils;
import im.bean.SingleMessage;
import im.event.CEventCenter;
import im.event.Events;
import im.event.I_CEventListener;
import im.im.IMSClientBootstrap;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2610:23
 * desc   :
 * version: 1.0
 */
public class PushManager implements I_CEventListener, IMSConnectStatusCallback {

    private static PushManager pushManager;

    public static PushManager getInstance() {
        if (pushManager == null) {
            pushManager = new PushManager();
        }
        return pushManager;
    }


    private ChatFriendListener friendListener;
    private ChatServerStateListener serverStateListener;
    private ServerNotificationListener notificationListener;

    private PushManager() {
        friendListener = new ChatFriendListener();
        serverStateListener = new ChatServerStateListener();
        notificationListener = new ServerNotificationListener();
    }


    /**
     * 启动长连接服务
     */
    public void startServer() {
        if (StringUtils.isEmpty(Config.pushIp) || StringUtils.isEmpty(Config.pushPort)) {
            return;
        }
        String hosts = "[{\"host\":\"" + Config.pushIp + "\", \"port\":" + Config.pushPort + "}]";
        String userId = SynApplication.spUtils.getString("userId");
        String token = "token_" + userId;
        IMSClientBootstrap.getInstance().init(userId, token, hosts, IMSConfig.APP_STATUS_FOREGROUND, this);
        CEventCenter.registerEventListener(this, Events.CHAT_SINGLE_MESSAGE);
        CEventCenter.registerEventListener(friendListener, Events.CHAT_FRIEND_MESSAGE);
        CEventCenter.registerEventListener(serverStateListener, Events.CHAT_SERVER_STATE);
        CEventCenter.registerEventListener(notificationListener, Events.SERVER_NOTIFICATION);
    }


    /**
     * 销毁长连接服务
     */
    public void destory() {
        CEventCenter.unregisterEventListener(this, Events.CHAT_SINGLE_MESSAGE);
        CEventCenter.unregisterEventListener(friendListener, Events.CHAT_FRIEND_MESSAGE);
        CEventCenter.unregisterEventListener(serverStateListener, Events.CHAT_SERVER_STATE);
        CEventCenter.unregisterEventListener(notificationListener, Events.SERVER_NOTIFICATION);
        IMSClientBootstrap.getInstance().destory();
    }


    /**
     * 接收消息的处理
     *
     * @param topic      事件名称
     * @param msgCode    消息类型
     * @param resultCode 预留参数
     * @param obj
     */
    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        switch (topic) {
            case Events.CHAT_SINGLE_MESSAGE: {
                final SingleMessage message = (SingleMessage) obj;
                if (StringUtils.isEmpty(message.getContent())) {
                    return;
                }
                String strContent = message.getContent();
                byte[] newBytes = Base64.decode(strContent, Base64.DEFAULT);
                //对字节数组进行解密操作
                String decryptString = AESUtils.decrypt(newBytes);
                //对解密的字符串进行处理
                int position = decryptString.lastIndexOf("}");
                String jsonString = decryptString.substring(0, position + 1);
                PushBo pushBo = new Gson().fromJson(jsonString, PushBo.class);
                NotifacationUtils.showNotification(Utils.getApp(), pushBo.getPushTitle(),
                        pushBo.getPushTitle(), pushBo.getPushContent());
                Log.e("wuliang", jsonString);
//                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  //播放系统默认提示音
//                Ringtone r = RingtoneManager.getRingtone(Utils.getApp(), notification);
//                r.play();
                EventBus.getDefault().post(new MessageRefreshEvent());
                break;
            }
            default:
                break;
        }
    }


    /**
     * 连接中
     */
    @Override
    public void onConnecting() {

    }

    /**
     * 连接成功
     */
    @Override
    public void onConnected() {
        EventBus.getDefault().post(new KeepEvent(true));
    }

    /**
     * 连接失败
     */
    @Override
    public void onConnectFailed() {
        EventBus.getDefault().post(new KeepEvent(false));
    }
}
