package synway.module_publicaccount.public_chat.WebviewFn;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.file_upload_for_camera.VideoUploadManager;

/**  给公众网页提供拍照接口
 * Created by admin on 2016/11/30.
 */
public class VideoUploadFn {
    private Activity activity;
    private String photoname="";
    String userid="";
    String  targetid="";
    String path = "";
    private PAWebViewAct paWebViewAct;

    private VideoUploadManager mVideoUploadManager = null;
    public VideoUploadFn(Activity activity, PAWebViewAct paWebViewAct, VideoUploadManager videoUploadManager, String userId){
        this.activity=activity;
        this.paWebViewAct=paWebViewAct;
        this.mVideoUploadManager = videoUploadManager;
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("camerasend");
//        activity.registerReceiver(cameraReceive, filter);
        userid= userId;
        targetid="public_account";
    }

    //本地视频
    @JavascriptInterface
    public String localVideoFn() {
        String result=null;
        if(mVideoUploadManager!=null){
            result = mVideoUploadManager.start(VideoUploadManager.BY_LOCALVIDEO,VideoUploadManager.FOR_H5);
        }
        return result;
    }

    //拍摄视频
    @JavascriptInterface
    public String videoRecordFn() {
        String result=null;
        if(mVideoUploadManager!=null){
            result = mVideoUploadManager.start(VideoUploadManager.BY_RECORD,VideoUploadManager.FOR_H5);
        }
        return result;
    }

    //异步-本地视频
    @JavascriptInterface
    public void localVideosAsyFn() {
        if(mVideoUploadManager!=null){
            mVideoUploadManager.startAsy(VideoUploadManager.BY_LOCALVIDEO,VideoUploadManager.FOR_H5);
        }
    }

    //异步-拍摄视频
    @JavascriptInterface
    public void videoRecordAsyFn() {
        if(mVideoUploadManager!=null){
             mVideoUploadManager.startAsy(VideoUploadManager.BY_RECORD,VideoUploadManager.FOR_H5);
        }
    }

}
