package cn.synway.synmonitor.utils;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.List;

import cn.synway.synmonitor.config.Config;
import cn.synway.synmonitor.logutil.LogUtil;


public class DeviceInfo {


    private DeviceInfo() {
    }

    public static DeviceInfo getInstance() {
        return DeviceInfoHolder.instance;
    }

    private static class DeviceInfoHolder {
        private static DeviceInfo instance = new DeviceInfo();
    }

    /**
     * 获取手机型号
     */
    public String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取操作系统信息
     */
    public String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取运营商
     */
    public String getProviderName(Context context) {
        if (null == context) {
            return "";
        }
        else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String ProvidersName = "";
            if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
                return "";
            }
            else {
                String IMSI = telephonyManager.getSubscriberId();
                if (!TextUtils.isEmpty(IMSI)) {
                    if (!IMSI.startsWith("46000") && !IMSI.startsWith("46002")) {
                        if (IMSI.startsWith("46001")) {
                            ProvidersName = "中国联通";
                        }
                        else if (IMSI.startsWith("46003")) {
                            ProvidersName = "中国电信";
                        }
                    }
                    else {
                        ProvidersName = "中国移动";
                    }
                }

                return ProvidersName;
            }
        }
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    public String getPhoneBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机Android API等级（22、23 ...）
     *
     * @return
     */
    public int getBuildLevel() {
        return android.os.Build.VERSION.SDK_INT;
    }


    /**
     * 获取手机Android 版本（4.4、5.0、5.1 ...）
     *
     * @return
     */
    public String getBuildVersion() {
        return android.os.Build.VERSION.RELEASE;
    }


    /**
     * 得到所在地域
     * https://blog.csdn.net/mingjiezuo/article/details/79755357
     */
    public double[] getLocale(Context contenx) {
        LocationManager locationManager = (LocationManager) contenx.getSystemService(Context.LOCATION_SERVICE);

        List<String> prodiverlist = locationManager.getProviders(true);
        String provider = null;
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;//网络定位
        }
        else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;//GPS定位
        }
        //有位置提供器的情况
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(contenx, permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(contenx, permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                LogUtil.w("Location", "缺少权限");
                return new double[]{0, 0};
            }
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    return new double[]{location.getLongitude(), location.getLatitude()};
                }
            }
            catch (Exception e) {
                LogUtil.w("Location", "地址获取异常");
                return new double[]{0, 0};
            }
        }
        LogUtil.w("Location", "没有可用的位置提供器");
        return new double[]{0, 0};
    }

    /**
     * 获取设备的唯一标识，deviceId
     * the IMEI for GSM and the MEID or ESN for CDMA phones
     *
     * @param context
     * @return
     */
    public String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //没有权限则返回""
            return "";
        }
        else {
            String deviceId = tm.getDeviceId();
            if (deviceId == null) {
                return "";
            }
            else {
                return deviceId;
            }
        }
    }

    public String getResolution() {
        String resolution = "";
        Context context = Config.context;
        if (context == null) {
            return resolution;
        }
        try {
            final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            final Display display = wm.getDefaultDisplay();
            final DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            resolution = metrics.widthPixels + "x" + metrics.heightPixels;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return resolution;
    }

    public String getDensity() {
        String densityStr = "";
        Context context = Config.context;
        if (context == null) {
            return densityStr;
        }
        final int density = context.getResources().getDisplayMetrics().densityDpi;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                densityStr = "LDPI";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                densityStr = "MDPI";
                break;
            case DisplayMetrics.DENSITY_TV:
                densityStr = "TVDPI";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                densityStr = "HDPI";
                break;
            //todo uncomment in android sdk 25
            //case DisplayMetrics.DENSITY_260:
            //    densityStr = "XHDPI";
            //    break;
            case DisplayMetrics.DENSITY_280:
                densityStr = "XHDPI";
                break;
            //todo uncomment in android sdk 25
            //case DisplayMetrics.DENSITY_300:
            //    densityStr = "XHDPI";
            //    break;
            case DisplayMetrics.DENSITY_XHIGH:
                densityStr = "XHDPI";
                break;
            //todo uncomment in android sdk 25
            //case DisplayMetrics.DENSITY_340:
            //    densityStr = "XXHDPI";
            //    break;
            case DisplayMetrics.DENSITY_360:
                densityStr = "XXHDPI";
                break;
            case DisplayMetrics.DENSITY_400:
                densityStr = "XXHDPI";
                break;
            case DisplayMetrics.DENSITY_420:
                densityStr = "XXHDPI";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                densityStr = "XXHDPI";
                break;
            case DisplayMetrics.DENSITY_560:
                densityStr = "XXXHDPI";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                densityStr = "XXXHDPI";
                break;
            default:
                densityStr = "other";
                break;
        }
        return densityStr;
    }

}
