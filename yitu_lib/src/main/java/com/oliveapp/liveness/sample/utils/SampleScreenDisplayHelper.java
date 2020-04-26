package com.oliveapp.liveness.sample.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.oliveapp.liveness.sample.R;

/**
 * Created by cli on 1/10/17.
 */

public class SampleScreenDisplayHelper {

    public static String TAG = SampleScreenDisplayHelper.class.getSimpleName();
    public static final double MIN_TABLET_SIZE = 7.0;
    public static final String ORIENTATION_TYPE_NAME = "ORIENTATION_TYPE_NAME";
    /**
     * 设置设备是显示横屏还是竖屏
     * OrientationType枚举中有两种选项
     * PORTRAIT代表竖屏
     * LANDSCAPE代表横屏
     */
    public enum OrientationType {
        PORTRAIT, LANDSCAPE, PORTRAIT_REVERSE, LANDSCAPE_REVERSE
    }

    /**
     * 请在这里设置决定要采用的屏幕方向
     * 默认都是竖屏
     * 若要自定义，请重写getFixedOrientation(Context)方法
     */
    public static OrientationType getFixedOrientation(Context context) {

        /**
         *  手机只有竖屏，平板根据摄像头位置可以设置横屏或者竖屏
         *  若要设置横屏可返回OrientationType.LANDSCAPE
         *  然后在具体的Activity的decideWhichLayout()方法中返回相应的layout值；
         */
        if (ifThisIsPhone(context)) {
            return OrientationType.PORTRAIT;
        } else {

            /**
             *  以下代码是为了配合设置功能，集成时可以删除
             */
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isDebug = sharedPrefs.getBoolean("pref_debug_mode", false);
            if (isDebug) {
                boolean padLandscape = sharedPrefs.getBoolean("pref_pad_landscape", false);
                if (padLandscape) {
                    return OrientationType.LANDSCAPE;
                } else {
                    return OrientationType.PORTRAIT;
                }
            }
            /**
             *  以上代码是为了配合设置功能，集成时可以删除
             */
            return OrientationType.PORTRAIT;
        }
    }


    /**
     * 判断本机是手机还是平板
     * 若是手机，返回true
     */
    public static boolean ifThisIsPhone(Context context) {
        if("phone".equals(context.getResources().getString(R.string.screen_type))) {
            return true;
        }
        else if ("tablet".equals(context.getResources().getString(R.string.screen_type))) {
            return false;
        }
        return false;
    }

    /**
     * 获取屏幕比例
     * @param context
     * @return
     */
    public static double getScreenScale(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        double scale = new Double(heightPixels) / new Double(widthPixels);
        return scale;
    }
}
