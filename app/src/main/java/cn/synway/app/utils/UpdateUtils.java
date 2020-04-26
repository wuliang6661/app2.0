package cn.synway.app.utils;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;

import cn.synway.app.R;
import cn.synway.app.api.DownloadResponseBody.DownloadListener;
import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.base.BaseDialog;
import cn.synway.app.bean.VersionBO;
import cn.synway.app.config.Config;
import cn.synway.app.config.FileConfig;
import cn.synway.app.ui.main.MainActivity;
import cn.synway.app.widget.AlertDialog;
import cn.synway.app.widget.HorizontalProgressBar;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2111:14
 * desc   : App检查更新的工具类
 * version: 1.0
 */
public class UpdateUtils {

    public static boolean isUpdate;

    public static void checkUpdate(onSourssListener listener) {
        HttpServerImpl.checkUpdate().subscribe(new HttpResultSubscriber<VersionBO>() {
            @Override
            public void onSuccess(VersionBO s) {
                if (listener != null) {
                    listener.onComplan();
                }
                String replace = AppUtils.getAppVersionName().replace(".", "");
                int version = 0;
                try {
                    version = Integer.parseInt(replace);
                }
                catch (Exception e) {
                    ToastUtils.showShort("版本解析有误");
                    return;
                }

                if (Integer.parseInt(s.getVersionCode()) > version) {
                    if (listener != null) {
                        listener.isHaveNewVersion();
                    }
                    if (Config.isUpdate) {
                        createCustomDialogTwo(s, listener);
                    }
                    else {
                        new AlertDialog(AppManager.getAppManager().curremtActivity()).builder().setGone().
                                setMsg("App有新版本，请前往应用商店更新！")
                                .setPositiveButton("确定", null).show();
                    }
                }
                else {
                    if (listener != null) {
                        listener.nowIsNew();
                    }
                }
            }

            @Override
            public void onFiled(String message) {
                if (listener != null) {
                    listener.onComplan();
                }
                // 首页不显示异常弹窗，只有检测更新时弹出
                if (StringUtils.isEmpty(message) || AppManager.getAppManager().curremtActivity()
                        instanceof MainActivity) {
                    return;
                }
                ToastUtils.showShort(message);
            }
        });
    }


    /**
     * 检测进度回调
     */
    public interface onSourssListener {

        void onComplan();

        void isHaveNewVersion();

        void nowIsNew();

        void update(File file);
    }


    private static void createCustomDialogTwo(VersionBO versionBO, onSourssListener listener) {
        BaseDialog baseDialog = new BaseDialog(AppManager.getAppManager().curremtActivity(), R.style.BaseDialog, R.layout.custom_dialog_two_layout);

        baseDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    if ("1".equals(versionBO.getAppIsUpdate())) { //需要强制更新
                        return true;
                    }
                    return false;
                }
                else {
                    return false;
                }
            }
        });
        TextView textView = baseDialog.findViewById(R.id.tv_msg);
        TextView title = baseDialog.findViewById(R.id.tv_title);
        TextView cancle = baseDialog.findViewById(R.id.cancle);
        View line = baseDialog.findViewById(R.id.line);
        title.setText(versionBO.getVersionName() + "版本更新");
        HorizontalProgressBar progress = baseDialog.findViewById(R.id.progress);

        ImageView close = baseDialog.findViewById(R.id.close);
        TextView upup = baseDialog.findViewById(R.id.commit);

        upup.setOnClickListener(v -> updateAPK(v, versionBO, baseDialog, progress, listener));

        textView.setText(versionBO.getUpdateText());
        if ("1".equals(versionBO.getAppIsUpdate())) {   //需要强制更新
            textView.setVisibility(View.VISIBLE);
            baseDialog.setCanceledOnTouchOutside(false);
            close.setVisibility(View.GONE);
            cancle.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        else {
            baseDialog.setCanceledOnTouchOutside(true);
            textView.setVisibility(View.GONE);
            close.setVisibility(View.VISIBLE);
        }
        baseDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (UpdateUtils.isUpdate) {
                    ToastUtils.showShort("后台下载中");
                }
            }
        });
        close.setOnClickListener(view -> {
            baseDialog.dismiss();
        });
        cancle.setOnClickListener(view -> {
            baseDialog.dismiss();
        });
        baseDialog.show();
    }

    private static void updateAPK(View v, VersionBO versionBO, BaseDialog baseDialog,
                                  HorizontalProgressBar progressView, onSourssListener listener) {
        LogUtils.e("updateAPK");
        v.setClickable(false);
        UpdateUtils.isUpdate = true;

        progressView.setVisibility(View.VISIBLE);
        String loadUrl = versionBO.getDownLoadUrl();
        File file = new File(FileConfig.getApkFile(), "update.apk");
        boolean existsFile = FileUtils.createOrExistsFile(file);

        if (!existsFile) {
            ToastUtils.showShort("IO异常");
            UpdateUtils.isUpdate = false;
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                LogUtils.e(Thread.currentThread().getName(), "2");
                progressView.setCurrentProgress((Float) msg.obj);
            }
        };

        DownloadListener downloadListener = new DownloadListener() {
            @Override
            public void onProgress(String progress) {
                Message message = Message.obtain();
                message.obj = Float.valueOf(progress);
                handler.sendMessage(message);
            }
        };
        HttpServerImpl.downLoad(loadUrl, downloadListener, file).subscribe(new Subscriber<ResponseBody>() {

            @Override
            public void onCompleted() {
                LogUtils.e(Thread.currentThread().getName(), "1");
                baseDialog.dismiss();
                progressView.setCurrentProgress(100F);
                listener.update(file);
                // AppUtils.installApp(file);
                //LogUtils.e("安装");
                UpdateUtils.isUpdate = false;

            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showShort(e.getMessage());
                baseDialog.dismiss();
                v.setClickable(true);
                UpdateUtils.isUpdate = false;
            }

            @Override
            public void onNext(ResponseBody integer) {


            }
        });
    }

}
