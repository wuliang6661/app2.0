package cn.synway.app.db.table;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NameInDb;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/258:54
 * desc   :  登录的各个账号用户表
 * version: 1.0
 */
@Entity
public class UserEntry implements Serializable {

    @Id
    public long id;

    @NameInDb("user_id")
    private String userID;

    /**
     * 用户名称
     */
    @NameInDb("user_name")
    private String userName;

    /**
     * 电话号码
     */
    @NameInDb("mobile_phone")
    private String mobilePhoneNo;

    /**
     * 性别
     */
    @NameInDb("sex")
    private int sex;

    /**
     * 机构编码
     */
    @NameInDb("organ_id")
    private String organ;

    /**
     * 登录账号
     */
    @NameInDb("login_code")
    private String code;

    /**
     * 省
     */
    @NameInDb("province")
    private String province;

    /**
     * 地区
     */
    @NameInDb("area")
    private String area;

    /**
     * 机构名称
     */
    @NameInDb("organ_name")
    private String organName;

    /**
     * 更新时间
     */
    @NameInDb("update_time")
    private long updateTime;

    /**
     * 该账号是否注销
     */
    @NameInDb("isdelete")
    private int isdelete;

    /**
     * 用户来源
     * <p>
     * 0： 自己平台添加的用户  1： 外部导入
     */
    @NameInDb("type")
    private int type;

    /**
     * 用户头像
     */
    @NameInDb("user_pic")
    private String userPic;

    /**
     * 用户角色
     */
    @NameInDb("user_roles")
    private String roles;

    /**
     * 当前账号登录时间
     */
    @NameInDb("login_time")
    private long loginTime;

    public UserEntry(String userID, String userName, String mobilePhoneNo, int sex, String organ, String code, String province, String area,
                     String organName, long updateTime, int isdelete, int type, String userPic, String roles, long loginTime) {
        this.userID = userID;
        this.userName = userName;
        this.mobilePhoneNo = mobilePhoneNo;
        this.sex = sex;
        this.organ = organ;
        this.code = code;
        this.province = province;
        this.area = area;
        this.organName = organName;
        this.updateTime = updateTime;
        this.isdelete = isdelete;
        this.type = type;
        this.userPic = userPic;
        this.roles = roles;
        this.loginTime = loginTime;
    }

    public UserEntry() {

    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobilePhoneNo() {
        return mobilePhoneNo;
    }

    public void setMobilePhoneNo(String mobilePhoneNo) {
        this.mobilePhoneNo = mobilePhoneNo;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
