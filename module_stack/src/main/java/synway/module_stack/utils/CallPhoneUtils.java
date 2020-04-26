package synway.module_stack.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import qyc.library.control.dialog_confirm.DialogConfirm;
import qyc.library.control.dialog_confirm.OnDialogConfirmClick;

public class CallPhoneUtils {


    /**
     * 显示弹窗
     */
    public static void showCallPhone(Activity activity, String phoneNum) {
        DialogConfirm.show(activity, "是否拨打电话?", phoneNum, new OnDialogConfirmClick() {
            @Override
            public void onDialogConfirmClick() {
                callPhone(activity, phoneNum);
            }
        });
    }


    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhone(Activity activity, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CALL_PHONE
                    }, 1);
            return;
        }
        activity.startActivity(intent);
    }


}
