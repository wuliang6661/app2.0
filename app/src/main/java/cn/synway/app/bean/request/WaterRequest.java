package cn.synway.app.bean.request;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/1610:07
 * desc   :  增加水印信息
 * version: 1.0
 */
public class WaterRequest {


    /**
     * dizhouCode : string
     * funcVersion : string
     * id : string
     * loginCode : string
     * loginLatitude : string
     * loginLongitude : string
     * loginTime : string
     * phoneCode : string
     * randomCode : string
     * serverIp : string
     */

    public String dizhouCode;
    public String funcVersion;
    public String loginCode;
    public String loginLatitude;
    public String loginLongitude;
    public String phoneCode;
    public String serverIp;

    public String getDizhouCode() {
        return dizhouCode;
    }

    public void setDizhouCode(String dizhouCode) {
        this.dizhouCode = dizhouCode;
    }

    public String getFuncVersion() {
        return funcVersion;
    }

    public void setFuncVersion(String funcVersion) {
        this.funcVersion = funcVersion;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public String getLoginLatitude() {
        return loginLatitude;
    }

    public void setLoginLatitude(String loginLatitude) {
        this.loginLatitude = loginLatitude;
    }

    public String getLoginLongitude() {
        return loginLongitude;
    }

    public void setLoginLongitude(String loginLongitude) {
        this.loginLongitude = loginLongitude;
    }


    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
}
