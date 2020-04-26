package cn.synway.app.bean.request;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2816:39
 * desc   :
 * version: 1.0
 */
public class MessageRequest {


    /**
     * currentPage : 0
     * pageSize : 0
     * pcId : string
     * pushType : string
     * userId : string
     */

    private int currentPage;
    private int pageSize;
    private String pcId;
    private String pushType;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getPcId() {
        return pcId;
    }

    public void setPcId(String pcId) {
        this.pcId = pcId;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

}
