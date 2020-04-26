package cn.synway.app.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.bumptech.glide.Glide;

import cn.synway.app.R;
import cn.synway.app.ui.main.MainActivity;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/279:14
 * desc   :  推送一条通知栏消息到通知栏
 * version: 1.0
 */
public class NotifacationUtils {


    private static int idPosition = 0;

    /**
     * 显示一个普通的通知
     *
     * @param context 上下文
     */
    public static synchronized void showNotification(Context context, String messageType, String title,
                                                     String message) {
        Notification notification = new NotificationCompat.Builder(context)
                /**设置通知左边的大图标**/
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ydjw_logo))
                /**设置通知右边的小图标**/
                .setSmallIcon(R.drawable.ydjw_logo)
                /**通知首次出现在通知栏，带上升动画效果的**/
                .setTicker(messageType)
                /**设置通知的标题**/
                .setContentTitle(title)
                /**设置通知的内容**/
                .setContentText(message)
                /**通知产生的时间，会在通知信息里显示**/
                .setWhen(System.currentTimeMillis())
                /**设置该通知优先级**/
                .setPriority(6)
                /**设置这个标志当用户单击面板就可以让通知将自动取消**/
                .setAutoCancel(true)
                /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
                .setOngoing(false)
                /**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：**/
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setChannelId("cn.synway.app")
                .setContentIntent(PendingIntent.getActivity(context, 1, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "cn.synway.app",
                    "通知栏",
                    NotificationManager.IMPORTANCE_DEFAULT

            );
            notificationManager.createNotificationChannel(channel);
        }

        /**发起通知**/
        notificationManager.notify(idPosition++ >= 300 ? idPosition = 0 : idPosition++, notification);
    }


    /**
     * 显示一个即时通信的会话通知
     *
     * @param context 上下文
     */
    public static void showNotification(Context context, String headUrl, String messageType, String title, String message, int id) {
        Glide.with(context).load(headUrl).asBitmap();

        Notification notification = new NotificationCompat.Builder(context)
                /**设置通知左边的大图标**/
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ydjw_logo))
                /**设置通知右边的小图标**/
                .setSmallIcon(R.drawable.ydjw_logo)
                /**通知首次出现在通知栏，带上升动画效果的**/
                .setTicker(messageType)
                /**设置通知的标题**/
                .setContentTitle(title)
                /**设置通知的内容**/
                .setContentText(message)
                /**通知产生的时间，会在通知信息里显示**/
                .setWhen(System.currentTimeMillis())
                /**设置该通知优先级**/
                .setPriority(6)
                /**设置这个标志当用户单击面板就可以让通知将自动取消**/
                .setAutoCancel(true)
                /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
                .setOngoing(false)
                /**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：**/
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setChannelId("cn.synway.app")
                .setContentIntent(PendingIntent.getActivity(context, 1, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "cn.synway.app",
                    "通知栏",
                    NotificationManager.IMPORTANCE_DEFAULT

            );
            notificationManager.createNotificationChannel(channel);
        }

        /**发起通知**/
        notificationManager.notify(id, notification);
    }

}
