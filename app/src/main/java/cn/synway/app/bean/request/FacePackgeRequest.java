package cn.synway.app.bean.request;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/10/3010:30
 * desc   :
 * version: 1.0
 */
public class FacePackgeRequest {

    private String queryImagePackage;

    private String signature;
    private boolean queryImagePackageReturnImageLIst;
    private String userId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQueryImagePackage() {
        return queryImagePackage;
    }

    public void setQueryImagePackage(String queryImagePackage) {
        this.queryImagePackage = queryImagePackage;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isQueryImagePackageReturnImageLIst() {
        return queryImagePackageReturnImageLIst;
    }

    public void setQueryImagePackageReturnImageLIst(boolean queryImagePackageReturnImageLIst) {
        this.queryImagePackageReturnImageLIst = queryImagePackageReturnImageLIst;
    }
}
