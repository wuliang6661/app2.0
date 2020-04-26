package cn.synway.app.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.base.SynBaseActivity;
import cn.synway.app.config.Config;
import cn.synway.app.utils.UpdateUtils;
import cn.synway.app.widget.AlertDialog;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/1813:16
 * desc   :
 * version: 1.0
 */
public class AboutActivity extends SynBaseActivity {


    @BindView(R.id.version_name)
    TextView versionName;
    @BindView(R.id.about_layout)
    LinearLayout aboutLayout;
    @BindView(R.id.today_point)
    TextView todayPoint;
    private final int INSTALL_PACKAGES_REQUESTCODE = 1111; //安装apk 权限code
    private File updateAPKFile;//apk地址

    @Override
    protected int getLayout() {
        return R.layout.act_about;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initToorBar();
        View back = findViewById(R.id.back);
        back.setOnClickListener(view -> backPage());

        setTitleText("关于");

        versionName.setText(AppUtils.getAppName() + AppUtils.getAppVersionName());
        if (Config.isHaveNewVersion) {
            todayPoint.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 界面退出事件
     */
    private void backPage() {
        if (UpdateUtils.isUpdate) {
            new AlertDialog(this).builder().setGone().setMsg("当前应用正在下载，退出将取消安装!")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("继续退出", v -> {
                        finish();
                    }).show();
        }
        else {
            finish();
        }
    }


    @OnClick(R.id.about_layout)
    public void about() {
        showProgress("");
        UpdateUtils.checkUpdate(new UpdateUtils.onSourssListener() {
            @Override
            public void onComplan() {
                stopProgress();
            }

            @Override
            public void isHaveNewVersion() {

            }

            @Override
            public void nowIsNew() {
                showToast("当前已是最新版本！");
            }

            @Override
            public void update(File file) {
                checkPermissionAndupdate(file);
            }
        });
    }

    @OnClick(R.id.update_layout)
    public void updateList() {
        gotoActivity(VersionListActivity.class, false);
    }

    private void checkPermissionAndupdate(File file) {

        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (b) {
                AppUtils.installApp(file);
            }
            else {
                this.updateAPKFile = file;
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                        INSTALL_PACKAGES_REQUESTCODE);
            }
        }
        else {
            AppUtils.installApp(file);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case INSTALL_PACKAGES_REQUESTCODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (updateAPKFile != null) {
                        AppUtils.installApp(updateAPKFile);
                    }
                }
                else {
                    //Toast.makeText(this, "请前往设置界面打开权限", Toast.LENGTH_SHORT).show();
                    new AlertDialog(this).builder().setGone().setMsg("无安装权限,点击确认前往设置界面")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", v -> {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                startActivityForResult(intent, INSTALL_PACKAGES_REQUESTCODE);
                            }).show();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INSTALL_PACKAGES_REQUESTCODE) {
            if (updateAPKFile != null) {
                AppUtils.installApp(updateAPKFile);
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                backPage();
                return true;

        }
        return super.onKeyUp(keyCode, event);
    }
}
