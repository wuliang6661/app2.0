package cn.synway.app.push.data;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/315:31
 * desc   :
 * version: 1.0
 */
public class PushBo {


    /**
     * pushTitle : 731511新闻
     * pushContent : 特哒好消息11
     * noReadCount : 8
     * pushType : 4e1d192b-31d4-eced-8894-2e766fbedd77a
     * createDate : 1562138993580
     */

    private String pushTitle;
    private String pushContent;
    private int noReadCount;
    private String pushType;
    private long createDate;
    private String pushId;
    private int businessType;
    private String pushUrl;

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getPushContent() {
        return pushContent;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }

    public int getNoReadCount() {
        return noReadCount;
    }

    public void setNoReadCount(int noReadCount) {
        this.noReadCount = noReadCount;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
