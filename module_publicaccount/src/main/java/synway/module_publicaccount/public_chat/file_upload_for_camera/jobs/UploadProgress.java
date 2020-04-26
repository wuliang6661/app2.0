package synway.module_publicaccount.public_chat.file_upload_for_camera.jobs;

/**
 * Created by 朱铁超 on 2018/12/12.
 */

public interface UploadProgress {
    void progress(int maxItem, int currentItem, long max, float curProgress);
}
