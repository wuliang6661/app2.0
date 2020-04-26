package synway.common.newCamera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;

import synway.cameraNew.BaseCamera;

/**
 * <p>
 *
 * @author 孙量 ${date}${time}
 * @LIO Life:<br>Input:<br>Output:
 */

public class NewCamera extends BaseCamera {
    public final static String EXTRA_TARGET_ID = "TARGET_ID";
    public final static String EXTRA_SET_RESULT = "SET_RESULT_2_SEND";
    private String targetID = null;
    private boolean isSetResult = false;

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
        targetID = getIntent().getStringExtra("TARGET_ID");
        if (targetID != null) {
            IntentFilter groupFilter = new IntentFilter();
            groupFilter.addAction("group.push.quitGroup_" + targetID);
            groupFilter.addAction("group.push.dismissGroup_" + targetID);
            registerReceiver(groupReceiver, groupFilter);
        }
        //隐藏相册按钮
        iv_album.setVisibility(View.GONE);
        isSetResult = getIntent().getBooleanExtra(EXTRA_SET_RESULT, false);
    }

    @Override
    protected void onDestroy() {
        if (targetID != null) {
            unregisterReceiver(groupReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void toSendPicture(String bigPath, String smallPath, boolean willClose, String description) {
        String picName = bigPath.substring(bigPath.lastIndexOf("/") + 1, bigPath.length());
//        Toast.makeText(this, picName, Toast.LENGTH_SHORT).show();
        if(isSetResult){
            Intent intent = new Intent();
            intent.putExtra(EXTRA_PHOTO_NAME, picName);
            intent.putExtra(EXTRA_DESCRIPTION, description);
            setResult(Activity.RESULT_OK, intent);
        }else{
            Intent intent = new Intent("send_picture");
            intent.putExtra("send_picture_address", picName);
            intent.putExtra(EXTRA_DESCRIPTION, description);
            sendBroadcast(intent);
        }
    }

    @Override
    protected void toFinishActivity() {
        finish();
    }

    @Override
    protected void onAlbumClick() {

    }

    private BroadcastReceiver groupReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("group.push.dismissGroup_" + targetID)) {
                finish();
            } else if (action.equals("group.push.quitGroup_" + targetID)) {
                finish();
            }

        }
    };

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
