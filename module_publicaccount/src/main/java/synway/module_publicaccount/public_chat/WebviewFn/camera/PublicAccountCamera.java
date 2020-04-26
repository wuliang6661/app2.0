package synway.module_publicaccount.public_chat.WebviewFn.camera;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import synway.cameraNew.BaseCamera;
import synway.common.newCamera.CameraState;

/**
 * Created by leo on 2018/9/28.
 */

public class PublicAccountCamera extends BaseCamera {

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
    }


    @Override protected void toSendPicture(String bigPath, String smallPath, boolean willClose, String description) {
        if (!TextUtils.isEmpty(bigPath)) {
            String picName = bigPath.substring(bigPath.lastIndexOf("/") + 1, bigPath.length());
            Intent intent = new Intent();
            intent.putExtra("bigPath", bigPath);
            intent.putExtra("result_PicName",picName);
            setResult(RESULT_OK, intent);
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
