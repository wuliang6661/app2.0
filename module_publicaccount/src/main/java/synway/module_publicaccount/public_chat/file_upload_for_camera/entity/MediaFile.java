package synway.module_publicaccount.public_chat.file_upload_for_camera.entity;

import java.io.Serializable;

/**
 * Created by 朱铁超 on 2018/12/3.
 */

public class MediaFile implements Serializable {

    /**
     * 本地路径
     */
    private String localPath;
    /**
     * 网络路径
     */
    private String netPath;

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getNetPath() {
        return netPath;
    }

    public void setNetPath(String netPath) {
        this.netPath = netPath;
    }
}
