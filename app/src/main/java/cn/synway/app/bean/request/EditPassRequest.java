package cn.synway.app.bean.request;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2416:50
 * desc   :
 * version: 1.0
 */
public class EditPassRequest {


    /**
     * newPWD : string
     * oldPWD : string
     * userID : string
     */

    private String newPWD;
    private String oldPWD;

    public String getNewPWD() {
        return newPWD;
    }

    public void setNewPWD(String newPWD) {
        this.newPWD = newPWD;
    }

    public String getOldPWD() {
        return oldPWD;
    }

    public void setOldPWD(String oldPWD) {
        this.oldPWD = oldPWD;
    }
}
