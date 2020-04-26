package cn.synway.app.bean.request;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2010:52
 * desc   :
 * version: 1.0
 */
public class LoginRequest {


    /**
     * IMEI : string
     * loginCode : string
     * loginPwd : string
     */

    private String IMEI;
    private String loginCode;
    private String loginPwd;

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }
}
