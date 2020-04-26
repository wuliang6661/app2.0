package cn.synway.synmonitor.event.eventmodel;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.synway.synmonitor.bean.DeviceBO;
import cn.synway.synmonitor.bean.UserBO;

public class BaseEvent {

    private String happenTime;//发生时间
    private String happenDate;//发生日期
    private String sdkVersion; //ssdk的版本
    private String description;//额外描述
    private String type;//页面，点击事件
    private String name;
    private long duration;

    //private UserBO userBO;//用户信息（可为空）

    private String userId;

    private String userName;

    private String organName;
    private String organId;

    //private DeviceBO deviceBO;//设备信息（包含经纬度）
    private int apiLevel;//Android api 版本
    private String androidVersion; //android版本
    private String deviceId;//设备ID
    private String phoneBrand;//品牌
    private String phoneModel;//手机型号
    private String providerName;//运营商
    private String systemVersion;//操作系统信息
    private String latitude;
    private String longitude;
    private Object userDescribe;
    private String functionId;
    private int storageType;
    private static SimpleDateFormat mFormat;
    private static  SimpleDateFormat mDateFormat;


    public void setHappenTime(long happenTime) {
        if (mFormat == null)
            mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = mFormat.format(new Date(happenTime));
        this.happenTime = time;
        this.setHappenDate(happenTime);
    }

    private void setHappenDate(long happenTime) {
        if (mDateFormat == null)
            mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = mDateFormat.format(new Date(happenTime));
        this.happenDate = date;
    }

    public void setDeviceBO(DeviceBO deviceBO) {
        //this.deviceBO = deviceBO;
        this.apiLevel = deviceBO.getApiLevel();
        this.androidVersion = deviceBO.getAndroidVersion();
        this.deviceId = deviceBO.getDeviceId();
        this.phoneBrand = deviceBO.getPhoneBrand();
        this.phoneModel = deviceBO.getPhoneModel();
        this.providerName = deviceBO.getProviderName();
        this.systemVersion = deviceBO.getSystemVersion();
        this.latitude = deviceBO.getLatitude();
        this.longitude = deviceBO.getLongitude();

    }

    public void setUserBO(@Nullable UserBO userBO) {
        if (userBO != null) {
            this.userId = userBO.getUserId();
            this.userName = userBO.getUserName();
            this.organName = userBO.getOrginName();
            this.organId = userBO.getOrginId();
            this.userDescribe = userBO.getDescribe();
        }
    }

    public String getHappenDate() {
        return happenDate;
    }

    public Object getUserDescribe() {
        return userDescribe;
    }

    public void setDescrible(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getHappenTime() {
        return happenTime;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getOrganName() {
        return organName;
    }

    public String getOrganId() {
        return organId;
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

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public void setId(String id) {
        this.functionId = id;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setStorageType(int storageType) {
        this.storageType = storageType;
    }

    public int getStorageType() {
        return storageType;
    }
}
