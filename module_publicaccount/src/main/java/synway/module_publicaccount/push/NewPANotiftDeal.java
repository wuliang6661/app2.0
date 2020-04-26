package synway.module_publicaccount.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Locale;
import synway.module_publicaccount.R;
import synway.module_publicaccount.config.HomeFlag;
import synway.module_publicaccount.public_chat.ring.RingSettingAct;
import synway.module_publicaccount.until.StringUtil;

public class NewPANotiftDeal {

    private Context context = null;

    private int requestID = 0;

    private static final int notifyId = 124;

    private SimpleDateFormat sdf_Parse = new SimpleDateFormat("HH:mm",
            Locale.CHINA);

    private NotificationManager notificationManager = null;

    public NewPANotiftDeal(Context context) {
        this.context = context;
        this.requestID = 0;

        this.notificationManager = (NotificationManager) this.context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * @param 公众号码ID
     * @return 是否显示通知栏，true=需要通知
     */
    public boolean notiyNewMsg(SQLiteOpenHelper sqliteHelp, String publicGUID) {
        // 判断是否 是当前聊天的用户
        String currentID = Sps_Notify.getCurrent(context);
        if (null != currentID && currentID.equals(publicGUID)) {
            return false;
        }

        String name = PublicAccountDeal.getPAName(
                sqliteHelp.getWritableDatabase(), publicGUID);
        if (name == null) {
            name = "未知公众号";
        }

        requestID = requestID + 1;
        String[] ringinfo = GetNewMsgRing.getRingUri(context, sqliteHelp.getReadableDatabase(), publicGUID);



        this.pushNewMsg(requestID, publicGUID, name,ringinfo[0],ringinfo[1]);
        return true;
    }

    /**
     * @param context
     * @param targetName 目标名称
     * @param targetID   目标ID
     * @param uri 铃声
     * @param isVibrateOpen
     */
    private void pushNewMsg(int requestID, String targetID, String fromUserName, String uri, String isVibrateOpen) {

        String tickText = fromUserName + "有新消息";

        Notification notification = new Notification();

        notification.icon = R.drawable.notify_small_2;
        notification.tickerText = tickText;

        notification.when = System.currentTimeMillis();

        notification.flags = Notification.FLAG_AUTO_CANCEL; // 点击清除按钮或点击通知后会自动消失
        notification.flags |= Notification.FLAG_SHOW_LIGHTS; // 呼吸灯
        // 一直进行，比如音乐一直播放，知道用户响应

        // 通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
        // 如果要全部采用默认值, 用 DEFAULT_ALL.
        if("1".equals(isVibrateOpen)){//震动开启
            notification.defaults = Notification.DEFAULT_VIBRATE; // 设置默认震动
            if(StringUtil.isEmpty(uri)){//uri为空使用默认铃声
                notification.defaults |= Notification.DEFAULT_SOUND;
            }else if(!RingSettingAct.MUTE.equals(uri)){//uri不为空且不静音，则获取播放铃声
                Uri uritemp = Uri.parse(uri);
                notification.sound =uritemp;
            }//其他情况不设置
        }else{//震动关闭
            if(StringUtil.isEmpty(uri)){
                notification.defaults = Notification.DEFAULT_SOUND;
            }else if(!RingSettingAct.MUTE.equals(uri)){
                Uri uritemp = Uri.parse(uri);
                notification.sound =uritemp;
            }
        }
        // notification.defaults = Notification.DEFAULT_ALL; // 把所有的属性设置成默认

//        if(uri!=null){
//            notification.sound =uri;
//        }else{
//            notification.defaults |= Notification.DEFAULT_SOUND; // 调用系统自带声音
//        }
        // 设置呼吸灯
        // 白色0xff0000f 这边的颜色跟设备有关，不是所有的颜色都可以，要看具体设备。
        notification.ledARGB = 0xff0000f;// Led颜色
        notification.ledOnMS = 1000;// led亮的时间
        notification.ledOffMS = 1000;// led灭的时间

        RemoteViews contentView = new RemoteViews(context.getPackageName(),
                R.layout.model_notifi_new_pamsg_contentview);

        contentView.setTextViewText(R.id.textView1, fromUserName);

        contentView.setTextViewText(R.id.textView2, "有新消息");

        contentView.setTextViewText(R.id.textView4,
                sdf_Parse.format(System.currentTimeMillis()));

        notification.contentView = contentView;

        Intent intent = new Intent();

        Class<?> cls = null;
        try {
            cls = Class.forName("synway.osc.home.HomeAct");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        intent.setClass(context, cls);
        intent.putExtra("ACCOUNT_ID", targetID);
        intent.putExtra("ACCOUNT_NAME", fromUserName);
        intent.putExtra("TARGET_TYPE", 0);
        intent.putExtra(HomeFlag.HOME_FLAG, HomeFlag.EXTRA_FLAG_GOPUBLIC);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // android.app.PendingIntent.FLAG_UPDATE_CURRENT:如果PendingIntent已经存在，保留它并且只替换它的extra数据。
        // android.app.PendingIntent.FLAG_CANCEL_CURRENT:如果PendingIntent已经存在，那么当前的PendingIntent会取消掉，
        // 然后产生一个新的PendingIntent。
        // android.app.PendingIntent.FLAG_ONE_SHOT:PendingIntent只能使用一次。调用了实例方法send()之后，
        // 它会被自动cancel掉，再次调用send()方法将失败。
        // android.app.PendingIntent.FLAG_NO_CREATE如果PendingIntent不存在，简单了当返回null。
        // requestID 相同的情况下，就会覆盖掉之前的Intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);// FLAG_UPDATE_CURRENT

        // 主程序的进程被销毁的时候，收到推送消息，
        // 点击推送消息跳转到 如上所写的Intent的
        // 这样好像默认使得 主程序的入口界面是如上所写的intent

        notification.contentIntent = pendingIntent;

        notificationManager.notify(targetID, notifyId, notification);
    }

    public void pushNoticeNewMsg(String targetID, String targetName) {

        // 判断是否 是当前聊天的用户

        String currentID = Sps_Notify.getCurrent(context);
        Log.d("dym------------------->", "targetID= "+targetID+",currentID= "+currentID);
        if (null != currentID && currentID.equals(targetID)) {
            return;
        }

        requestID = requestID + 1;

        String tickText = targetName + "有新消息";

        Notification notification = new Notification();

        notification.icon = R.drawable.notify_small_2;
        notification.tickerText = tickText;

        notification.when = System.currentTimeMillis();

        notification.flags = Notification.FLAG_AUTO_CANCEL; // 点击清除按钮或点击通知后会自动消失
        notification.flags |= Notification.FLAG_SHOW_LIGHTS; // 呼吸灯
        // 一直进行，比如音乐一直播放，知道用户响应

        // 通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
        // 如果要全部采用默认值, 用 DEFAULT_ALL.
        notification.defaults = Notification.DEFAULT_SOUND; // 调用系统自带声音
        notification.defaults |= Notification.DEFAULT_VIBRATE; // 设置默认震动
        // notification.defaults = Notification.DEFAULT_ALL; // 把所有的属性设置成默认

        // 设置呼吸灯
        // 白色0xff0000f 这边的颜色跟设备有关，不是所有的颜色都可以，要看具体设备。
        notification.ledARGB = 0xff0000f;// Led颜色
        notification.ledOnMS = 1000;// led亮的时间
        notification.ledOffMS = 1000;// led灭的时间

        RemoteViews contentView = new RemoteViews(context.getPackageName(),
            R.layout.model_notifi_new_pamsg_contentview);

        contentView.setTextViewText(R.id.textView1, targetName);

        contentView.setTextViewText(R.id.textView2, "有新消息");

        contentView.setTextViewText(R.id.textView4,
            sdf_Parse.format(System.currentTimeMillis()));

        notification.contentView = contentView;

        Intent intent = new Intent();

        Class<?> cls = null;
        try {
            cls = Class.forName("synway.osc.home.HomeAct");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        intent.setClass(context, cls);
        intent.putExtra(HomeFlag.HOME_FLAG, HomeFlag.EXTRA_FLAG_GOPUBLIC_NOTICE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // android.app.PendingIntent.FLAG_UPDATE_CURRENT:如果PendingIntent已经存在，保留它并且只替换它的extra数据。
        // android.app.PendingIntent.FLAG_CANCEL_CURRENT:如果PendingIntent已经存在，那么当前的PendingIntent会取消掉，
        // 然后产生一个新的PendingIntent。
        // android.app.PendingIntent.FLAG_ONE_SHOT:PendingIntent只能使用一次。调用了实例方法send()之后，
        // 它会被自动cancel掉，再次调用send()方法将失败。
        // android.app.PendingIntent.FLAG_NO_CREATE如果PendingIntent不存在，简单了当返回null。
        // requestID 相同的情况下，就会覆盖掉之前的Intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
            requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);// FLAG_UPDATE_CURRENT

        // 主程序的进程被销毁的时候，收到推送消息，
        // 点击推送消息跳转到 如上所写的Intent的
        // 这样好像默认使得 主程序的入口界面是如上所写的intent

        notification.contentIntent = pendingIntent;

        notificationManager.notify(targetID, notifyId, notification);
    }

    public static void dismissPushMsgAll(Context context) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }

    public static void dismissPushMsg(Context context, String tag) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(tag, notifyId);
    }

}