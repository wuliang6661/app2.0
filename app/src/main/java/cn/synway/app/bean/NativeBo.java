package cn.synway.app.bean;

public class NativeBo {

    /**
     * app_version : 1.0
     * app_packangename : com.article.oa_article
     * app_start : com.article.oa_article.LoginActivity
     * app_name : app名称
     * app_newinformation : 新版本更新内容
     * app_download_url : app下载地址
     */
    private String app_version;
    private String app_packangename;
    private String app_start;
    private String app_name;
    private String app_newinformation;
    private String app_download_url;
    private int type;

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
