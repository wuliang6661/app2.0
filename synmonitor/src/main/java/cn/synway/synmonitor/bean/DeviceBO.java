package cn.synway.synmonitor.bean;

import android.content.Context;

import cn.synway.synmonitor.utils.DeviceInfo;

public class DeviceBO {

    private int apiLevel;//Android api 版本
    private String androidVersion; //android版本
    private String deviceId;//设备ID
    private String phoneBrand;//品牌
    private String phoneModel;//手机型号
    private String providerName;//运营商
    private String systemVersion;//操作系统信息
    private String latitude;
    private String longitude;

    private DeviceBO() {

    }

    public DeviceBO(Context context) {
        DeviceInfo instance = DeviceInfo.getInstance();
        apiLevel = instance.getBuildLevel();
        androidVersion = instance.getBuildVersion();
        phoneBrand = instance.getPhoneBrand();
        phoneModel = instance.getPhoneModel();
        systemVersion = instance.getSystemVersion();
        if (context != null) {
            deviceId = instance.getDeviceId(context);
            providerName = instance.getProviderName(context);
            double[] locale = instance.getLocale(context);
            latitude = locale[0] + "";
            longitude = locale[1] + "";
        }
    }

    public int getApiLevel() {
        return apiLevel;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
