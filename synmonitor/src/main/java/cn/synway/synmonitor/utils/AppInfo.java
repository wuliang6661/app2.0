package cn.synway.synmonitor.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.synway.synmonitor.config.Config;
import cn.synway.synmonitor.logutil.LogUtil;

public class AppInfo {
    private final String TAG = "AppInfo";

    private Map<String, Class> manifestActivityMap = null;
    private String launchActivityName = null;

    private AppInfo() {

    }

    public static AppInfo getInstance() {
        return AppInfoHolder.instance;
    }

    private static class AppInfoHolder {
        private static AppInfo instance = new AppInfo();
    }

    //获得app的applicationID
    public String getApplicationID() {
        try {
            Context context = Config.context;
            if (context == null) {
                return "";
            }
            return context.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //获得app的名称
    public String getApplicationName() {
        try {
            Context context = Config.context;
            if (context == null) {
                return "";
            }

            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = info.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //获得app的versionName
    public String getAppVersionName() {
        try {
            Context context = Config.context;
            if (context == null) {
                return "";
            }

            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    //获得启动Activity的类名
    public String getAppLaunchActivityName() {
        Context context = Config.context;
        if (context == null) {
            return null;
        }

        if (TextUtils.isEmpty(launchActivityName)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(context.getPackageName());
            PackageManager pManager = context.getPackageManager();
            List<ResolveInfo> launchInfo = pManager.queryIntentActivities(intent, 0);
            if (launchInfo != null && launchInfo.size() > 0) {
                ResolveInfo resolveInfo = launchInfo.get(0);
                launchActivityName = resolveInfo.activityInfo.name;
            }
        }

        return launchActivityName;
    }

    //获得指定的Activity是否在AndroidManifest文件中所有注册
    public boolean isIncludedinAndroidManifest(String activityName) {
        if (manifestActivityMap == null) {
            Context context = Config.context;
            if (context == null) {
                return false;
            }

            manifestActivityMap = new HashMap<String, Class>();
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
                if (packageInfo.activities != null) {
                    for (ActivityInfo ai : packageInfo.activities) {
                        Class c;
                        try {
                            c = Class.forName(ai.name);
                            manifestActivityMap.put(c.getName(), c);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        LogUtil.d(TAG, manifestActivityMap.toString());

        if (manifestActivityMap.containsKey(activityName)) {
            return true;
        } else {
            return false;
        }
    }
}
