package cn.synway.app.ui.login;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.base.SynApplication;
import cn.synway.app.bean.AppConfigBO;
import cn.synway.app.bean.event.FaceLoginEvent;
import cn.synway.app.bean.request.LoginRequest;
import cn.synway.app.config.Config;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.ui.config.ConfigActivity;
import cn.synway.app.ui.main.MainActivity;
import cn.synway.app.ui.recognize.YiTuLivenessActivity;
import cn.synway.app.utils.AppManager;
import cn.synway.app.utils.DeviceUtils;
import cn.synway.app.utils.NetUtils;
import cn.synway.app.widget.AlertDialog;


/**
 * MVPPlugin
 * 登录页面
 */

public class LoginActivity extends MVPBaseActivity<LoginContract.View, LoginPresenter>
        implements LoginContract.View {

    @BindView(R.id.edit_login_user)
    EditText editLoginUser;
    @BindView(R.id.edit_login_password)
    EditText editLoginPassword;
    @BindView(R.id.scoll_view)
    ScrollView scollView;

    private String strUserCode;
    private String strUserPassword;

    @Override
    protected int getLayout() {
        return R.layout.act_login;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        requestPermission();
        editLoginUser.setText(SynApplication.spUtils.getString("loginCode"));
        editLoginPassword.setText(SynApplication.spUtils.getString("password"));
//        if (BuildConfig.DEBUG) {
//            editLoginUser.setText("yth2");
//            // 15726818192
//            editLoginPassword.setText("1");
//        }
//        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.bt_login)
    public void login() {
        strUserCode = editLoginUser.getText().toString().trim();
        strUserPassword = editLoginPassword.getText().toString().trim();
        if (StringUtils.isEmpty(strUserCode)) {
            showToast("请输入账号！");
            return;
        }
        if (StringUtils.isEmpty(strUserPassword)) {
            showToast("请输入密码！");
            return;
        }
        LoginRequest request = new LoginRequest();
        request.setIMEI(DeviceUtils.getMEIDCode());
        request.setLoginCode(strUserCode);
        request.setLoginPwd(strUserPassword);
        showProgress(null);
        SynApplication.spUtils.put("liveUser", strUserCode);
        mPresenter.login(request);
//        gotoActivity(YiTuLivenessActivity.class, false);
    }


    @OnClick(R.id.config)
    public void configClick() {
        gotoActivity(ConfigActivity.class, false);
    }


    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.RECORD_AUDIO
                    }, 1);
        }
    }


    @Override
    public void onRequestError(String msg) {
        stopProgress();
        showToast(msg);
    }

    @Override
    public void onRequestEnd() {

    }

    @Override
    public void getConfigBo(AppConfigBO configBO) {
        SynApplication.token = configBO.getToken();
        Config.isOpenLive = "1".equals(configBO.getIsLiveAuth());
        Config.isOpenLogin = "1".equals(configBO.getAppLogin());
        Config.isOpenWaterMaker = "1".equals(configBO.getAppWatermark());
        Config.isUpdate = "1".equals(configBO.getAppIsUpdate());
        Config.publicIP = configBO.getAppFunctionIp();
        Config.publicPort = configBO.getAppFunctionPort();
        Config.pushIp = configBO.getAppPushIp();
        Config.pushPort = configBO.getAppPushPort();
        Config.pushUpdatePort = configBO.getAppUploadPort();
        Config.AppCenterName = configBO.getModelName();
        Config.gartherUrl = configBO.getAppStatisticsUrl();

        if (!StringUtils.isTrimEmpty(configBO.getMsgInfo())) {
            new AlertDialog(AppManager.getAppManager().curremtActivity()).builder().setGone().
                    setMsg(configBO.getMsgInfo())
                    .setPositiveButton("确定", null)
                    .setDismissListener((dialogInterface) -> {
                        checkIMEI(configBO);
                    }).show();
        } else {
            checkIMEI(configBO);
        }

    }

    private void checkIMEI(AppConfigBO configBO) {
        if ("1".equals(configBO.getIsMTerminal())) {   //机身码未绑定
            new AlertDialog(this).builder().setGone().setMsg("绑定手机\n账号与本机绑定后，将在别的手机不可登陆！")
                    .setNegativeButton("取消", view -> stopProgress())
                    .setPositiveButton("确定", v -> {
                        mPresenter.bindIMEI(DeviceUtils.getMEIDCode());
                    }).show();
        } else {
            if (Config.isOpenLive) {
                stopProgress();
                YiTuLivenessActivity.startForAuth(this);
            } else {
                mPresenter.getUserInfo();
            }
        }
    }


    @Override
    public void bindSourss() {
        if (Config.isOpenLive) {   //活体验证打开
            stopProgress();
            YiTuLivenessActivity.startForAuth(this);
        } else {
            mPresenter.getUserInfo();
        }
    }

    @Override
    public void getUserInfo(UserEntry userBO) {
        stopProgress();
        UserIml.addData(userBO);

        SynApplication.spUtils.put("userId", userBO.getUserID());

        if (Config.isOpenLogin) {   //如果可以自动登录，则缓存数据
            SynApplication.spUtils.put("loginCode", userBO.getCode());
            SynApplication.spUtils.put("password", strUserPassword);
            SynApplication.spUtils.put("isOpenLogin", Config.isOpenLogin);
        } else {
            SynApplication.spUtils.remove("loginCode");
            SynApplication.spUtils.remove("password");
            SynApplication.spUtils.remove("isOpenLogin");
        }
        gotoActivity(MainActivity.class, true);
    }


    /**
     * 活体登录成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void liveSuress(FaceLoginEvent event) {
        if (event.isSuress) {
            mPresenter.getUserInfo();
        }
    }


    /**
     * 让ScrollView滚动到底部
     */
    private void scrollToBottom(final View decorView, final View scroll) {
        Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || decorView == null) {
                    return;
                }
                int offset = decorView.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }
                scroll.scrollTo(0, offset);
            }
        });
    }
}
