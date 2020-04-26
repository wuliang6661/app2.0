package synway.module_publicaccount.until.DownLoad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static synway.module_publicaccount.until.BroastCastUtil.INSTALLBROAST;
import static synway.module_publicaccount.until.BroastCastUtil.PACKGENAME;
import static synway.module_publicaccount.until.BroastCastUtil.UNINSTALLBROAST;


/**
 * Created by ysm on 2017/11/22.
 * 监听手机上app的安装卸载情况
 */

public  class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
            Intent installintent = new Intent();
            installintent.setAction(INSTALLBROAST);
            installintent.putExtra(PACKGENAME,packageName);
            context.sendBroadcast(installintent);
            System.out.println("安装了:" +packageName + "包名的程序");
        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
            Intent uninstallintent = new Intent();
            uninstallintent.setAction(UNINSTALLBROAST);
            uninstallintent.putExtra(PACKGENAME,packageName);
            context.sendBroadcast(uninstallintent);
            System.out.println("卸载了:"  + packageName + "包名的程序");

        }
    }
}