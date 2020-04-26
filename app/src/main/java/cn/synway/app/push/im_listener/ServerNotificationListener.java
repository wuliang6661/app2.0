package cn.synway.app.push.im_listener;

import android.content.Intent;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import cn.synway.app.base.SynApplication;
import cn.synway.app.bean.event.MessageRefreshEvent;
import cn.synway.app.db.DataManager;
import cn.synway.app.push.PushManager;
import cn.synway.app.push.data.ServerNotificationBO;
import cn.synway.app.ui.login.LoginActivity;
import cn.synway.app.utils.AppManager;
import cn.synway.app.utils.SynCountlyFactory;
import im.bean.SingleMessage;
import im.event.Events;
import im.event.I_CEventListener;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/8/269:24
 * desc   : 收到服务端推送的设备管理推送应用层监听
 * version: 1.0
 */
public class ServerNotificationListener implements I_CEventListener {


    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        switch (topic) {
            case Events.SERVER_NOTIFICATION: {
                SingleMessage message = (SingleMessage) obj;
                String body = message.getContent();
                if (StringUtils.isEmpty(body)) {
                    return;
                }
                ServerNotificationBO notificationBO = new Gson().fromJson(body, ServerNotificationBO.class);
                switch (notificationBO.type) {
                    case "1":
                        ToastUtils.showShort("该账号被注销或设备已被禁用，请联系管理员！");
                        SynApplication.spUtils.clear();
                        PushManager.getInstance().destory();
                        DataManager.getInstance().clearAll();
                        SynCountlyFactory.clearSynCountlyUserData();
                        SynCountlyFactory.destorySynCountly();
                        Intent intent = new Intent(AppManager.getAppManager().curremtActivity(), LoginActivity.class);
                        AppManager.getAppManager().curremtActivity().startActivity(intent);
                        AppManager.getAppManager().goLogin();
                        break;
                    case "2":
                        EventBus.getDefault().post(new MessageRefreshEvent());
                        break;
                }
                break;
            }
            default:
                break;
        }
    }
}
