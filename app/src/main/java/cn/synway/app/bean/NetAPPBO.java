package cn.synway.app.bean;

import im.utils.StringUtil;

public class NetAPPBO {

    /**
     * id : 0561c3c1-bc79-8a7a-a75a-b090f95d5d9b5
     * name : 本地app测试
     * status : 0
     * mobilePic : http://172.18.100.37:82/publicFuncImages/Public_ad5eb15b-2adf-4d3a-8168-283f32ac7f91
     * businessUrl :
     * <p>
     * businessType : 2
     * sourceUrl : {"type":10000,"app_version":"1.0","app_packangename":"com.article.oa_article","app_start":"com.article.oa_article.LoginActivity","app_name":"app名称","app_newinformation":"新版本更新内容","app_download_url":"app下载地址"}
     * jumpType : click
     * isDisplayBanner : 1
     * pushMsgType : null
     * belongToGroup :
     * belongGroupName : null
     * faceNeed : 0
     * funcCode : app_name123456
     * isLiveAuth : 0
     */
    private boolean isHead;
    private String headName;
    private String id;
    private String name;
    private int status;
    private String mobilePic;
    private String businessUrl;


    private String businessType; //公众号的类型 1: week ; //  html:0  本地: 2

    private String sourceUrl;

    private String jumpType; //empty , click , buton

    private String pushMsgType;
    private String belongToGroup;
    private String belongGroupName;
    private int faceNeed;
    private String funcCode;
    private int isLiveAuth;
    private String isDisplayBanner;
    private int noReadCount;

    public NetAPPBO(String headName, boolean b) {
        this.headName = headName;
        this.isHead = b;
    }

    public NetAPPBO() {

    }

    public String getId() {
        return id;
    }

    public void setNoReadCount(int noReadCount) {
        this.noReadCount = noReadCount;
    }

    public int getNoReadCount() {
        return noReadCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMobilePic() {
        return mobilePic;
    }

    public void setMobilePic(String mobilePic) {
        this.mobilePic = mobilePic;
    }

    public String getBusinessUrl() {
        return businessUrl;
    }

    public void setBusinessUrl(String businessUrl) {
        this.businessUrl = businessUrl;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getIsDisplayBanner() {
        return StringUtil.isEmpty(isDisplayBanner) ? "1" : isDisplayBanner;
    }

    public void setIsDisplayBanner(String isDisplayBanner) {
        this.isDisplayBanner = isDisplayBanner;
    }

    public String getPushMsgType() {
        return pushMsgType;
    }

    public void setPushMsgType(String pushMsgType) {
        this.pushMsgType = pushMsgType;
    }

    public String getBelongToGroup() {
        return belongToGroup;
    }

    public void setBelongToGroup(String belongToGroup) {
        this.belongToGroup = belongToGroup;
    }

    public String getBelongGroupName() {
        return belongGroupName;
    }

    public void setBelongGroupName(String belongGroupName) {
        this.belongGroupName = belongGroupName;
    }

    public int getFaceNeed() {
        return faceNeed;
    }

    public void setFaceNeed(int faceNeed) {
        this.faceNeed = faceNeed;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public int getIsLiveAuth() {
        return isLiveAuth;
    }

    public void setIsLiveAuth(int isLiveAuth) {
        this.isLiveAuth = isLiveAuth;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHeadName(String headName) {
        this.headName = headName;
    }

    public String getHeadName() {
        return headName;
    }


    public class SourceUrl {

        /**
         * type : 10000
         * app_version : 1.0
         * app_packangename : com.article.oa_article
         * app_start : com.article.oa_article.LoginActivity
         * app_name : app名称
         * app_newinformation : 新版本更新内容
         * app_download_url : app下载地址
         */

        private int type;
        private String app_version;
        private String app_packangename;
        private String app_start;
        private String app_name;
        private String app_newinformation;
        private String app_download_url;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getApp_version() {
            return app_version;
        }

        public void setApp_version(String app_version) {
            this.app_version = app_version;
        }

        public String getApp_packangename() {
            return app_packangename;
        }

        public void setApp_packangename(String app_packangename) {
            this.app_packangename = app_packangename;
        }

        public String getApp_start() {
            return app_start;
        }

        public void setApp_start(String app_start) {
            this.app_start = app_start;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public String getApp_newinformation() {
            return app_newinformation;
        }

        public void setApp_newinformation(String app_newinformation) {
            this.app_newinformation = app_newinformation;
        }

        public String getApp_download_url() {
            return app_download_url;
        }

        public void setApp_download_url(String app_download_url) {
            this.app_download_url = app_download_url;
        }
    }
}
