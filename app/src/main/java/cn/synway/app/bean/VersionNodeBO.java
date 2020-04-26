package cn.synway.app.bean;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2114:59
 * desc   :
 * version: 1.0
 */
public class VersionNodeBO {


    /**
     * versionName : v1.0.4
     * releaseNote : 1212
     * level : 0
     * levelStr : 普通
     * appName : null
     * versionCode : 4
     * createTime : 1559720955
     */


    private String versionName;
    private String releaseNote;
    private int level;   //0:普通  1 高  2 严重
    private String levelStr;
    private String appName;
    private int versionCode;
    private String createTime;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getReleaseNote() {
        return releaseNote;
    }

    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelStr() {
        return levelStr;
    }

    public void setLevelStr(String levelStr) {
        this.levelStr = levelStr;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
