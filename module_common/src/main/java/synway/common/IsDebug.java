package synway.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * 当前APP是否为调试模式
 * Created by 钱园超 on 2017/6/5.
 */

public class IsDebug {

    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & 2) != 0;
        } catch (Exception var2) {
            return false;
        }
    }

}
