package synway.module_publicaccount.weex_module.extend.moudle;

import android.app.Activity;
import android.content.Intent;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import synway.module_publicaccount.public_chat.file_upload_for_camera.PicUploadManager;
import synway.module_publicaccount.weex_module.utlis.Constants;


/**
 * Created by dell on 2018/10/11.
 * 说明：
 */

public class CameraModule extends WXModule {

    @JSMethod(uiThread = true)
    public void openCamera() {
        Intent intent = new Intent(Constants.BROADCAST_CAMERA);
        intent.putExtra("msg", "开启摄像头");
        Activity activity = ((Activity) mWXSDKInstance.getContext());
        mWXSDKInstance.getContext().sendBroadcast(intent);
    }

    //纯相机
    @JSMethod(uiThread = false)
    public void picureByOnlyCameraFn(JSCallback callback) {
        callback.invoke(((PicUploadManager.UpdateInterface) (mWXSDKInstance.getContext())).startPic(PicUploadManager.BY_CAMERA));
    }

    //纯相册
    @JSMethod(uiThread = false)
    public void useAlbumFn(JSCallback callback) {
        callback.invoke(((PicUploadManager.UpdateInterface) (mWXSDKInstance.getContext())).startPic(PicUploadManager.BY_ONLY_ALBUM));
    }

    //相机+相册
    @JSMethod(uiThread = false)
    public void picureAndAlbumFn(JSCallback callback) {
        callback.invoke(((PicUploadManager.UpdateInterface) (mWXSDKInstance.getContext())).startPic(PicUploadManager.BY_ALBUM));
    }

    //异步纯相机
    @JSMethod(uiThread = false)
    public void picureByOnlyCameraAsyFn() {
        ((PicUploadManager.UpdateInterface) (mWXSDKInstance.getContext())).startAsyPic(PicUploadManager.BY_CAMERA);
    }

    //异步纯相册
    @JSMethod(uiThread = false)
    public void useAlbumAsyFn() {
        ((PicUploadManager.UpdateInterface) (mWXSDKInstance.getContext())).startAsyPic(PicUploadManager.BY_ONLY_ALBUM);
    }

    //异步相机+相册
    @JSMethod(uiThread = false)
    public void picureAndAlbumAsyFn() {
        ((PicUploadManager.UpdateInterface) (mWXSDKInstance.getContext())).startAsyPic(PicUploadManager.BY_ALBUM);
    }
}
