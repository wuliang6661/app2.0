package cn.synway.app.bean;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2815:13
 * desc   :
 * version: 1.0
 */
public class MessageFcBo {


    /**
     * pushTitle : 标题4
     * pushType : 4e1d192b-31d4-eced-8894-2e766fbedd77a
     * typeName : 任务通知
     * createDate : 2019-06-27 15:56:42.0
     * mobilePic : null
     */

    private String pushTitle;
    private String pushType;
    private String typeName;
    private long createDate;
    private String mobilePic;
    private int noReadTotal;

    /**
     * 以下是即时通信的消息
     * <p>
     * 0:为推送应用消息  1 为即时通信消息
     */
    private int type;
    private String id;    //会话id
    private String msgType;   //消息推送类型

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNoReadTotal() {
        return noReadTotal;
    }

    public void setNoReadTotal(int noReadTotal) {
        this.noReadTotal = noReadTotal;
    }

    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getMobilePic() {
        return mobilePic;
    }

    public void setMobilePic(String mobilePic) {
        this.mobilePic = mobilePic;
    }
}
