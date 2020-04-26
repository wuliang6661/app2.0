package synway.module_publicaccount.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Locale;

import synway.module_publicaccount.R;
import synway.module_publicaccount.config.HomeFlag;

public class NewMsgNotifyDeal {

    private Context context = null;

    private int requestID = 0;

    private static final int notifyId = 123;

    private long lastNotifTime = 0;

    private SimpleDateFormat sdf_Parse = new SimpleDateFormat("HH:mm",
            Locale.CHINA);

    private NotificationManager notificationManager = null;

    public NewMsgNotifyDeal(Context context) {
        this.context = context;
        this.requestID = 0;

        this.notificationManager = (NotificationManager) this.context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * @param targetID     目标ID
     * @param fromUserName 来自用户的名称
     * @param targetType   目标类型
     * @param msg          jsonMsg
     * @return 是否显示通知栏，true=需要通知
     */
//    public boolean notiyNewMsg(SQLiteDatabase db, String targetID,
//                               String fromUserName, int targetType, JSONObject msg) {
//
//        // 判断是否 是当前聊天的用户
//        String currentID = Sps_Notify.getCurrent(context);
//        String currentID_Again = Sps_NotifyAgain.getCurrent(context);
//        if ((null != currentID && currentID.equals(targetID))
//                || (null != currentID_Again && currentID_Again.equals(targetID))) {
//            // this.vibrator.vibrate(500);
//            return false;
//        }
//
//        // 先判断是否在忽略名单中
//        boolean isIgnore = IgnoreDeal.isIgnore(db, targetID);
//        if (isIgnore) {
//            return false;
//        }
//
//        String targetName = null;
//        if (targetType == 1) {
//            targetName = GroupInfoDeal.getGroupName(db, targetID);
//            if (targetName == null) {
//                targetName = "群组";
//            }
//        } else {
//            targetName = fromUserName;
//        }
//
//        int count = LastContactDeal.getUnReadCount(db, targetID);
//        count = count + 1;
//        if (count > 99) {
//            count = 99;
//        }
//
//        String targetMsg = JsonParseMsg.getAnnexType(msg);
//        Uri uri = GetNewMsgRing.getRingUri(context, db, targetID);
////		Log.i("lmly", uri.toString());
//        this.notifyget(requestID, targetID, targetName, targetType,
//                fromUserName, targetMsg, count, uri);
//
//        requestID = requestID + 1;
//        return true;
//    }

    /**
     * @param targetID    目标ID
     * @param targetName  目标名称
     * @param targetType  目标类型：0单聊，1群聊
     * @param msg         当前消息内容
     * @param unReadCount 目标未读消息数量
     */
    private void pushNewMsg(int requestID, String targetID, String targetName,
                            int targetType, String fromUserName, String msg, int unReadCount,
                            Uri uri) {

        String tickText = fromUserName + ":" + msg;

        Notification notification = new Notification();

        notification.icon = R.drawable.notify_small_2;
        notification.tickerText = tickText;

        notification.when = System.currentTimeMillis();

        boolean isTip = false;
        long currentTime = System.currentTimeMillis();
        if ((lastNotifTime == 0) || ((currentTime - lastNotifTime) > 2500)) {
            // 提示
            isTip = true;
            lastNotifTime = currentTime;
        }

        if (isTip) {
            notification.sound = uri; // 自定义声音
        }
        notification.flags = Notification.FLAG_AUTO_CANCEL; // 点击清除按钮或点击通知后会自动消失
        notification.flags |= Notification.FLAG_SHOW_LIGHTS; // 呼吸灯
        // notification.flags = Notification.FLAG_ONGOING_EVENT; // 常驻
        // notification.flags = Notification.FLAG_INSISTENT; //
        // 一直进行，比如音乐一直播放，知道用户响应

        // 通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
        // 如果要全部采用默认值, 用 DEFAULT_ALL.
        // notification.defaults = Notification.DEFAULT_SOUND; // 调用系统自带声音
        if (isTip) {
            notification.defaults = Notification.DEFAULT_VIBRATE; // 设置默认震动
        }
        // notification.defaults = Notification.DEFAULT_ALL; // 把所有的属性设置成默认

        // 设置呼吸灯
        // 白色0xff0000f 这边的颜色跟设备有关，不是所有的颜色都可以，要看具体设备。
        notification.ledARGB = 0x869ABE;// Led颜色
        notification.ledOnMS = 1500;// led亮的时间
        notification.ledOffMS = 1500;// led灭的时间

        RemoteViews contentView = new RemoteViews(context.getPackageName(),
                R.layout.model_notifi_newmsg_contentview);

        contentView.setTextViewText(R.id.textView1, targetName);

        String strUnRead = "[" + unReadCount + "条]";
        contentView.setTextViewText(R.id.textView2, strUnRead);

        contentView.setTextViewText(R.id.textView3, msg);

        contentView.setTextViewText(R.id.textView4,
                sdf_Parse.format(System.currentTimeMillis()));

        contentView.setTextViewText(R.id.textView5, fromUserName + ": ");

        notification.contentView = contentView;

        Intent intent = new Intent();

        Class<?> cls = null;
        try {
            cls = Class.forName("synway.osc.home.HomeAct");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        intent.setClass(context, cls);
        intent.putExtra("TARGET_ID", targetID);
        intent.putExtra("TARGET_NAME", targetName);
        intent.putExtra("TARGET_TYPE", targetType);
        intent.putExtra(HomeFlag.HOME_FLAG, HomeFlag.EXTRA_FLAG_GOCHAT);
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

    /**
     * @param targetID    目标ID
     * @param targetName  目标名称
     * @param targetType  目标类型：0单聊，1群聊
     * @param msg         当前消息内容
     * @param unReadCount 目标未读消息数量
     */
    private void notifyget(int requestID, String targetID, String targetName,
                           int targetType, String fromUserName, String msg, int unReadCount,
                           Uri uri) {
        String tickText = fromUserName + ":" + msg;
        Intent intent = new Intent();
        Class<?> cls = null;
        try {
            cls = Class.forName("synway.osc.home.HomeAct");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        intent.setClass(context, cls);
        intent.putExtra("TARGET_ID", targetID);
        intent.putExtra("TARGET_NAME", targetName);
        intent.putExtra("TARGET_TYPE", targetType);
        intent.putExtra(HomeFlag.HOME_FLAG, HomeFlag.EXTRA_FLAG_GOCHAT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        boolean isTip = false;
        long currentTime = System.currentTimeMillis();
        if ((lastNotifTime == 0) || ((currentTime - lastNotifTime) > 2500)) {
            // 提示
            isTip = true;
            lastNotifTime = currentTime;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);// FLAG_UPDATE_CURRENT

        Notification notification;

        if (isTip) {
            notification = new Notification.Builder(context)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                    .setSound(uri)
                    .setLights(0x869ABE, 1500, 1500)
                    .setTicker(tickText)
                    .setContentTitle(targetName)
                    .setContentText("[" + unReadCount + "条]" + fromUserName + ":" + msg)
                    .setSmallIcon(R.drawable.notify_small_2)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true).build();
        } else {
            notification = new Notification.Builder(context)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                    .setLights(0x869ABE, 1500, 1500)
                    .setTicker(tickText)
                    .setContentTitle(targetName)
                    .setContentText("[" + unReadCount + "条]" + fromUserName + ":" + msg)
                    .setSmallIcon(R.drawable.notify_small_2)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true).build();
        }
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