package cn.synway.synmonitor.bean;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/1714:40
 * desc   :  需要上报的用户信息
 * version: 1.0
 */
public class UserBO {

    private String userId;

    private String userName;

    private String orginName;
    private String orginId;
    private Object mDescribe;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrginName() {
        return orginName;
    }

    public void setOrginName(String orginName) {
        this.orginName = orginName;
    }

    public void setOrginId(String orginId) {
        this.orginId = orginId;
    }

    public String getOrginId() {
        return orginId;
    }

    public Object getDescribe() {
        return mDescribe;
    }

    public void setDescripe(Object describe) {
        this.mDescribe = describe;
    }
}
