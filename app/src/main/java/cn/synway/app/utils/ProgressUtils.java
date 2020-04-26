package cn.synway.app.utils;

import android.app.Dialog;

import qyc.library.control.dialog_progress.DialogProgress;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/3011:07
 * desc   :  加载弹窗工具类
 * version: 1.0
 */
public class ProgressUtils {

    private static ProgressUtils utils;
    private static Dialog svProgressHUD = null;


    public static ProgressUtils getInstance() {
        if (utils == null) {
            utils = new ProgressUtils();
        }
        return utils;
    }


    /**
     * 显示加载进度弹窗
     */
    public void showProgress() {
        svProgressHUD = DialogProgress.get(AppManager.getAppManager().curremtActivity(), "加载中....",
                "message");
        svProgressHUD.setCanceledOnTouchOutside(true);
        svProgressHUD.show();
    }

    /**
     * 停止弹窗
     */
    public void stopProgress() {
        if (svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }


}
