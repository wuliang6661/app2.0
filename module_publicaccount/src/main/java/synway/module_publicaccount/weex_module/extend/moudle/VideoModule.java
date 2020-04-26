package synway.module_publicaccount.weex_module.extend.moudle;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import synway.module_publicaccount.public_chat.file_upload_for_camera.VideoUploadManager;


/**
 * Created by dell on 2018/10/11.
 * 说明：
 */

public class VideoModule extends WXModule {

    /**
     * 上传拍摄视频
     */
    @JSMethod(uiThread = false)
    public void localVideoFn(JSCallback callback) {
        callback.invoke(((VideoUploadManager.UpdateInterface)(mWXSDKInstance.getContext())).startVideoUpload(VideoUploadManager.BY_RECORD));
    }

    /**
     * 上传本地视频
     */
    @JSMethod(uiThread = false)
    public void videoRecordFn(JSCallback callback) {
        callback.invoke(((VideoUploadManager.UpdateInterface)(mWXSDKInstance.getContext())).startVideoUpload(VideoUploadManager.BY_LOCALVIDEO));
    }

    /**
     * 异步-上传拍摄视频
     */
    @JSMethod(uiThread = false)
    public void localVideoAsyFn() {
        ((VideoUploadManager.UpdateInterface)(mWXSDKInstance.getContext())).startVideoUploadAsy(VideoUploadManager.BY_RECORD);
    }

    /**
     * 异步-上传本地视频
     */
    @JSMethod(uiThread = false)
    public void videoRecordAsyFn() {
        ((VideoUploadManager.UpdateInterface)(mWXSDKInstance.getContext())).startVideoUploadAsy(VideoUploadManager.BY_LOCALVIDEO);
    }
}
