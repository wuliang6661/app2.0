package synway.module_publicaccount.public_chat.file_upload_for_camera.record;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;

import java.io.File;

import synway.common.newCamera.CameraState;
import synway.module_interface.config.BaseUtil;
import synway.vcamera.BaseRecordActivity;


/**
 * 公众号视频录制 改自抓捕视频录制
 *
 * @author 朱铁超
 */
public class VideoRecord extends BaseRecordActivity {


    public static final String ACTION_SEND_VIDEO = "arrest_send_video";
    public static final String EXTRA_VIDEO_PATH = "arrest_video_path";

    @Override
    public boolean getReadyBindService() {
        if (CameraState.instance().cameraStateHandler.getCameraState() != 0) {
            isOpenCamera = false;
            showErrorDialog("后台拍摄正在运行，视频录制无法使用");
            return false;
        } else {
            isOpenCamera = true;
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initVariable();
        super.onCreate(savedInstanceState);
        hideRecord.setVisibility(View.GONE);//隐藏暗拍按钮
        autoUploadFlag = false;
        setMaxMillSecond(120000);//拍摄时长为2分钟
        setUploadText("上传");//到达最大拍摄时长的提示语
//        isNoSuffix = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void loadConfigWithUrl() {
        videoQuality = 2;//视频质量为540P
        autoUploadFlag = false;
    }

    @Override
    public void videoPathCallback(String path) {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_SEND_VIDEO);
//        intent.putExtra(EXTRA_VIDEO_PATH, path);
//        sendBroadcast(intent);
//        Intent intent = new Intent();
//        intent.putExtra(EXTRA_VIDEO_PATH,path);
//        setResult(RESULT_OK, intent);
//        finish();
    }

    @Override
    public void configCallback(int quality, boolean autoUpload) {

    }

    @Override
    public void toFinishActivity(String path) {
        if (path != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_VIDEO_PATH, path);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }

    @Override
    public Intent getHideRecordServiceIntent() {
        return null;
    }

    private void initVariable() {
        targetID = getIntent().getStringExtra("TARGET_ID");
        userID = getIntent().getStringExtra("USER_ID");
        fileFolder = BaseUtil.ChatFileUtil.getChatVideoPath(userID, targetID);
        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void showErrorDialog(String msg) {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog//
                .btnNum(1)
                .content(msg)//
                .btnText("确定")//
                .showAnim(new BounceTopEnter())//
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                finish();
            }
        });
    }


}
