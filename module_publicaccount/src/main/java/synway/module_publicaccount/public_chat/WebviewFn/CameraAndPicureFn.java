package synway.module_publicaccount.public_chat.WebviewFn;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import java.io.File;

import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.file_upload_for_camera.PicUploadManager;

/**
 * 给公众网页提供拍照接口
 * Created by admin on 2016/11/30.
 */
public class CameraAndPicureFn {
    private Activity activity;
    private String photoname = "";
    String userid = "";
    String targetid = "";
    String path = "";
    private PAWebViewAct paWebViewAct;


    private PicUploadManager mTakePhoneAndUpdateManager = null;

    public CameraAndPicureFn(Activity activity, PAWebViewAct paWebViewAct,
                             PicUploadManager takePhoneAndUpdateManager, String userId) {
        this.activity = activity;
        this.paWebViewAct = paWebViewAct;
        this.mTakePhoneAndUpdateManager = takePhoneAndUpdateManager;
        userid = userId;
        targetid = "public_account";
    }


    //相机+相册
    @JavascriptInterface
    public String picureAndAlbumFn() {
        String result = null;
        File filessmall = new File(BaseUtil.getFolderPath() + "/" + userid + "/msgfile/" + targetid + "/pic/small");
        if (!filessmall.exists()) {
            filessmall.mkdirs();
        }
        if (mTakePhoneAndUpdateManager != null) {
            result = mTakePhoneAndUpdateManager.start(PicUploadManager.BY_ALBUM, PicUploadManager.FOR_H5);
        }
        return result;
    }

    //纯相册
    @JavascriptInterface
    public String useAlbumFn() {
        String result = null;
        File filessmall = new File(BaseUtil.getFolderPath() + "/" + userid + "/msgfile/" + targetid + "/pic/small");
        if (!filessmall.exists()) {
            filessmall.mkdirs();
        }
        if (mTakePhoneAndUpdateManager != null) {
            result = mTakePhoneAndUpdateManager.start(PicUploadManager.BY_ONLY_ALBUM, PicUploadManager.FOR_H5);
        }
        return result;
    }

    //纯相机
    @JavascriptInterface
    public String picureByOnlyCameraFn() {
        String result = null;
        File filessmall = new File(BaseUtil.getFolderPath() + "/" + userid + "/msgfile/" + targetid + "/pic/small");
        if (!filessmall.exists()) {
            filessmall.mkdirs();
        }
        if (mTakePhoneAndUpdateManager != null) {
            result = mTakePhoneAndUpdateManager.start(PicUploadManager.BY_CAMERA, PicUploadManager.FOR_H5);
        }
        return result;
    }

    //异步-纯相机
    @JavascriptInterface
    public void picureByOnlyCamerasAsyFn() {
        File filessmall = new File(BaseUtil.getFolderPath() + "/" + userid + "/msgfile/" + targetid + "/pic/small");
        if (!filessmall.exists()) {
            filessmall.mkdirs();
        }
        if (mTakePhoneAndUpdateManager != null) {
            mTakePhoneAndUpdateManager.startAsy(PicUploadManager.BY_CAMERA, PicUploadManager.FOR_H5);
        }
    }

    //异步-纯相册
    @JavascriptInterface
    public void useAlbumsAsyFn() {
        File filessmall = new File(BaseUtil.getFolderPath() + "/" + userid + "/msgfile/" + targetid + "/pic/small");
        if (!filessmall.exists()) {
            filessmall.mkdirs();
        }
        if (mTakePhoneAndUpdateManager != null) {
            mTakePhoneAndUpdateManager.startAsy(PicUploadManager.BY_ONLY_ALBUM, PicUploadManager.FOR_H5);
        }
    }

    //异步-相机+相册
    @JavascriptInterface
    public void picureAndAlbumsAsyFn() {
        File filessmall = new File(BaseUtil.getFolderPath() + "/" + userid + "/msgfile/" + targetid + "/pic/small");
        if (!filessmall.exists()) {
            filessmall.mkdirs();
        }
        if (mTakePhoneAndUpdateManager != null) {
            mTakePhoneAndUpdateManager.startAsy(PicUploadManager.BY_ALBUM, PicUploadManager.FOR_H5);
        }
    }
}
