package cn.synway.app.bean;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/8/216:04
 * desc   :
 * version: 1.0
 */
public class MsgSelectFcBo {


    /**
     * androidPhone : 1
     * faceNeed : 0
     * id : 4e1d192b-31d4-eced-8894-2e766fbedd77a
     * isLiveAuth : 0
     * name : 任务通知
     * phoneUrl : www.baidu.com
     */

    private int androidPhone;
    private int faceNeed;
    private String id;
    private int isLiveAuth;
    private String name;
    private String phoneUrl;
    /**
     * androidIdentity : 1
     * androidLicense : 0
     * identityUrl : https://blog.csdn.net/sinat_38184748/article/details/88795699?targetId=%value%
     * licenseUrl :
     */

    private int androidIdentity;
    private int androidLicense;
    private String identityUrl;
    private String licenseUrl;


    public int getAndroidPhone() {
        return androidPhone;
    }

    public void setAndroidPhone(int androidPhone) {
        this.androidPhone = androidPhone;
    }

    public int getFaceNeed() {
        return faceNeed;
    }

    public void setFaceNeed(int faceNeed) {
        this.faceNeed = faceNeed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsLiveAuth() {
        return isLiveAuth;
    }

    public void setIsLiveAuth(int isLiveAuth) {
        this.isLiveAuth = isLiveAuth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneUrl() {
        return phoneUrl;
    }

    public void setPhoneUrl(String phoneUrl) {
        this.phoneUrl = phoneUrl;
    }

    public int getAndroidIdentity() {
        return androidIdentity;
    }

    public void setAndroidIdentity(int androidIdentity) {
        this.androidIdentity = androidIdentity;
    }

    public int getAndroidLicense() {
        return androidLicense;
    }

    public void setAndroidLicense(int androidLicense) {
        this.androidLicense = androidLicense;
    }

    public String getIdentityUrl() {
        return identityUrl;
    }

    public void setIdentityUrl(String identityUrl) {
        this.identityUrl = identityUrl;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }
}
