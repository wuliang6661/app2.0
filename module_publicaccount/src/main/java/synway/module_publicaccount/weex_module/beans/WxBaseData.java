package synway.module_publicaccount.weex_module.beans;

/**
 * Created by huangxi
 * DATE :2018/11/29
 * Description ：公众号跳转页面数据基类
 */

public class WxBaseData {
    private String userId;//用户名Id
    private String paId;//公众号Id
    private String serviceUrl;//与中间服务通信Url
    private String resourceUrl;//资源存放Url

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

}
