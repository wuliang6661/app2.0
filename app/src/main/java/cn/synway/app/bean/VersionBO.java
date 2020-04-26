package cn.synway.app.bean;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2114:18
 * desc   :
 * version: 1.0
 */
public class VersionBO {


    /**
     * downLoadUrl : D:/SynServer/tomcat7.0.57/SynWay/OSCService/APK开启活体的包.apk
     * appName : 开启活体的包.apk
     * updateText : 我是描述
     * versionName : v2.0.1
     * versionCode : 20
     */

    private String downLoadUrl;
    private String appName;
    private String updateText;
    private String versionName;
    private String versionCode;
    private String appIsUpdate;

    public String getAppIsUpdate() {
        return appIsUpdate;
    }

    public void setAppIsUpdate(String appIsUpdate) {
        this.appIsUpdate = appIsUpdate;
    }

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUpdateText() {
        return updateText;
    }

    public void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
