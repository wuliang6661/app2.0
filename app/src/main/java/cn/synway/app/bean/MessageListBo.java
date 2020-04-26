package cn.synway.app.bean;

import java.util.List;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2816:52
 * desc   :
 * version: 1.0
 */
public class MessageListBo {


    /**
     * total : 2
     * list : [{"id":null,"pushId":"4","pushContent":"消息4","pushType":0,"pushLevel":0,"createDate":null,"pushTitle":null,"pcId":null},{"id":null,"pushId":"2","pushContent":"124131","pushType":0,"pushLevel":0,"createDate":null,"pushTitle":null,"pcId":null}]
     */

    private int total;
    private List<ListBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : null
         * pushId : 4
         * pushContent : 消息4
         * pushType : 0     0： 未读   1 已读
         * pushLevel : 0
         * createDate : null
         * pushTitle : null
         * pcId : null
         */

        private String id;
        private String pushId;
        private String pushContent;
        private int pushType;
        private int pushLevel;
        private String createDate;
        private String pushTitle;
        private String pcId;
        private int businessType;
        private String pushUrl;
        private int isRead;    // 1是已读，0是未读

        public int getIsRead() {
            return isRead;
        }

        public void setIsRead(int isRead) {
            this.isRead = isRead;
        }

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPushId() {
            return pushId;
        }

        public void setPushId(String pushId) {
            this.pushId = pushId;
        }

        public String getPushContent() {
            return pushContent;
        }

        public void setPushContent(String pushContent) {
            this.pushContent = pushContent;
        }

        public int getPushType() {
            return pushType;
        }

        public void setPushType(int pushType) {
            this.pushType = pushType;
        }

        public int getPushLevel() {
            return pushLevel;
        }

        public void setPushLevel(int pushLevel) {
            this.pushLevel = pushLevel;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getPushTitle() {
            return pushTitle;
        }

        public void setPushTitle(String pushTitle) {
            this.pushTitle = pushTitle;
        }

        public String getPcId() {
            return pcId;
        }

        public void setPcId(String pcId) {
            this.pcId = pcId;
        }
    }
}
