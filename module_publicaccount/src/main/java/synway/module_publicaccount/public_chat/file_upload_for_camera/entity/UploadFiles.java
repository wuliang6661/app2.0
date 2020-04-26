package synway.module_publicaccount.public_chat.file_upload_for_camera.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 朱铁超 on 2018/12/3.
 * 文件上传对象
 */

public class UploadFiles implements Serializable {

    //需要上传的对象
    private List<String> localFiles;

    //存储上传完成的本地路径与对应上传路径
    private List<MediaFile> netFiles;

    public List<String> getLocalFiles() {
        return localFiles;
    }

    public void setLocalFiles(List<String> localFiles) {
        this.localFiles = localFiles;
    }

    public List<MediaFile> getNetFiles() {
        return netFiles;
    }

    public void setNetFiles(List<MediaFile> netFiles) {
        this.netFiles = netFiles;
    }
}
