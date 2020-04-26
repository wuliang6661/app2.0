package synway.module_publicaccount.public_chat.WebviewFn.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;

import me.iwf.photopicker.PhotoPickerActivity;
import synway.cameraNew.BaseCamera;
import synway.common.newCamera.CameraState;
import synway.module_publicaccount.public_chat.file_upload_for_camera.PicUploadManager;

/**
 * Created by leo on 2018/9/28.
 */

public class PublicAccountCameraNew extends BaseCamera {

    //如果来自相册，那么拍完后返回，把相册关闭
    public static final String EXTRA_IS_FROM_PACKER = "EXTRA_IS_FROM_PACKER";
    //广播种子，防止多广播冲突
    public static final String EXTRA_SEED= "EXTRA_SEED";
    private boolean isClosePacker  = false;
    private int seed = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CameraState.instance().cameraStateHandler.getCameraState() == 1) {
            isOpenCamera = false;
            showDialog("实时视频后台拍摄正在运行，照片拍摄无法使用");
        } else if (CameraState.instance().cameraStateHandler.getCameraState() == 2) {
            isOpenCamera = false;
            showDialog("文件视频后台拍摄正在运行，照片拍摄无法使用");
        }
        //隐藏相册按钮
        iv_album.setVisibility(View.GONE);
        isClosePacker =getIntent().getBooleanExtra(EXTRA_IS_FROM_PACKER, false);
        seed=getIntent().getIntExtra(EXTRA_SEED, -1);
    }


    @Override protected void toSendPicture(String bigPath, String smallPath, boolean willClose, String description) {
        //发送广播
        String action = "send_picture";
        if(seed!=-1){
            action+=seed;
        }
        Intent intent = new Intent(action);

        intent.putExtra("send_picture_bigPath", bigPath);
//        intent.putExtra("send_picture_smallPath", smallPath);
        sendBroadcast(intent);
        //如果是从相册跳过来的，那么返回的时候需要把相册关闭
        if(isClosePacker){
            Intent intent1 = new Intent();
//            intent1.putExtra("result_CloseAlbum",true);
            setResult(PhotoPickerActivity.RESULT_CAMERA, intent1);
        }else {//不是来自相册，那么拍完后返回成功，用来给页面判断拍照是否成功
//            Intent intent2 = new Intent();
            setResult(RESULT_OK);
        }

    }


    @Override protected void toFinishActivity() {
        finish();
    }


    @Override protected void onAlbumClick() {

    }

    private void showDialog(String msg) {
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
