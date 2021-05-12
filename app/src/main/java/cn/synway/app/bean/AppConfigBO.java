package cn.synway.app.bean;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2015:54
 * desc   :
 * version: 1.0
 */
public class AppConfigBO {


    /**
     * token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJBUFAiLCJ1c2VyX2lkIjoieXRoMSIsImlzcyI6IlNlcnZpY2UiLCJleHAiOjE1NjE4ODE3MTcsImlhdCI6MTU2MTAxNzcxN30.v0hDv7jJUYU9bKsA3je9ZuV_Lx_P_SQAFiswlbnguA8
     * isLiveAuth : 0
     * isMTerminal : 0
     * appLogin : 1
     * appPushIp : 172.18.100.37
     * appPushPort : 7501
     * appFunctionIp : 172.18.100.37
     * appFunctionPort : 82
     */

    private String token;
    private String isLiveAuth;
    private String isMTerminal;
    private String appLogin;
    private String appPushIp;
    private String appPushPort;
    private String appFunctionIp;
    private String appFunctionPort;
    private String appWatermark;
    private String appIsUpdate;
    private String appUploadPort;
    private String modelName;
    private String appStatisticsUrl;
    private String msgInfo;
    private String showMessageMenu;
    private String showAddressListMenu;

    public String getShowMessageMenu() {
        return showMessageMenu;
    }

    public void setShowMessageMenu(String showMessageMenu) {
        this.showMessageMenu = showMessageMenu;
    }

    public String getShowAddressListMenu() {
        return showAddressListMenu;
    }

    public void setShowAddressListMenu(String showAddressListMenu) {
        this.showAddressListMenu = showAddressListMenu;
    }

    public void setMsgInfo(String msgInfo) {
        this.msgInfo = msgInfo;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public String getAppStatisticsUrl() {
        return appStatisticsUrl;
    }

    public void setAppStatisticsUrl(String appStatisticsUrl) {
        this.appStatisticsUrl = appStatisticsUrl;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getAppUploadPort() {
        return appUploadPort;
    }

    public void setAppUploadPort(String appUploadPort) {
        this.appUploadPort = appUploadPort;
    }

    public String getAppIsUpdate() {
        return appIsUpdate;
    }

    public void setAppIsUpdate(String appIsUpdate) {
        this.appIsUpdate = appIsUpdate;
    }

    public String getAppWatermark() {
        return appWatermark;
    }

    public void setAppWatermark(String appWatermark) {
        this.appWatermark = appWatermark;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsLiveAuth() {
        return isLiveAuth;
    }

    public void setIsLiveAuth(String isLiveAuth) {
        this.isLiveAuth = isLiveAuth;
    }

    public String getIsMTerminal() {
        return isMTerminal;
    }

    public void setIsMTerminal(String isMTerminal) {
        this.isMTerminal = isMTerminal;
    }

    public String getAppLogin() {
        return appLogin;
    }

    public void setAppLogin(String appLogin) {
        this.appLogin = appLogin;
    }

    public String getAppPushIp() {
        return appPushIp;
    }

    public void setAppPushIp(String appPushIp) {
        this.appPushIp = appPushIp;
    }

    public String getAppPushPort() {
        return appPushPort;
    }

    public void setAppPushPort(String appPushPort) {
        this.appPushPort = appPushPort;
    }

    public String getAppFunctionIp() {
        return appFunctionIp;
    }

    public void setAppFunctionIp(String appFunctionIp) {
        this.appFunctionIp = appFunctionIp;
    }

    public String getAppFunctionPort() {
        return appFunctionPort;
    }

    public void setAppFunctionPort(String appFunctionPort) {
        this.appFunctionPort = appFunctionPort;
    }
}
