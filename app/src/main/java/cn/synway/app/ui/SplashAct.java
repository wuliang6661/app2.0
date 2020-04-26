package cn.synway.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.synway.app.R;
import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.base.SynApplication;
import cn.synway.app.base.SynBaseActivity;
import cn.synway.app.bean.AppConfigBO;
import cn.synway.app.bean.event.FaceLoginEvent;
import cn.synway.app.bean.request.LoginRequest;
import cn.synway.app.config.Config;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.ui.login.LoginActivity;
import cn.synway.app.ui.main.MainActivity;
import cn.synway.app.ui.recognize.YiTuLivenessActivity;
import cn.synway.app.utils.DeviceUtils;


/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/1410:44
 * desc   :  引导页
 * version: 1.0
 */
public class SplashAct extends SynBaseActivity {


    @Override
    protected int getLayout() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.act_splash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EventBus.getDefault().register(this);
        new Handler().postDelayed(this::appIsLogin, 2000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 判断App是否需要自动登录 425600
     */
    private void appIsLogin() {
        boolean isOpenLogin = SynApplication.spUtils.getBoolean("isOpenLogin", false);
        if (isOpenLogin) {   //可以自动登录
            String loginCode = SynApplication.spUtils.getString("loginCode");
            String password = SynApplication.spUtils.getString("password");

            LoginRequest request = new LoginRequest();
            request.setIMEI(DeviceUtils.getMEIDCode());
            request.setLoginCode(loginCode);
            request.setLoginPwd(password);
            login(request);
        }
        else {
            gotoActivity(LoginActivity.class, true);
        }
    }


    /**
     * 登录接口
     */
    public void login(LoginRequest request) {
        HttpServerImpl.login(request).subscribe(new HttpResultSubscriber<AppConfigBO>() {
            @Override
            public void onSuccess(AppConfigBO configBO) {
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
                if ("1".equals(configBO.getIsMTerminal())) {   //机身码未绑定
                    gotoActivity(LoginActivity.class, true);
                }
                else {
                    if (Config.isOpenLogin) {
                        if (Config.isOpenLive) {
                            stopProgress();
                            YiTuLivenessActivity.startForAuth(SplashAct.this);
                        }
                        else {
                            getUserInfo();
                        }
                    }
                    else {
                        gotoActivity(LoginActivity.class, true);
                    }
                }
            }

            @Override
            public void onFiled(String message) {
                gotoActivity(LoginActivity.class, true);
            }
        });
    }


    /**
     * 获取用户信息
     */
    public void getUserInfo() {
        HttpServerImpl.getUserInfo().subscribe(new HttpResultSubscriber<UserEntry>() {
            @Override
            public void onSuccess(UserEntry userBO) {
                UserIml.addData(userBO);

                SynApplication.spUtils.put("userId", userBO.getUserID());
                if (Config.isOpenLogin) {   //如果可以自动登录，则缓存数据
                    SynApplication.spUtils.put("isOpenLogin", Config.isOpenLogin);
                }
                else {
                    SynApplication.spUtils.remove("loginCode");
                    SynApplication.spUtils.remove("password");
                    SynApplication.spUtils.remove("isOpenLogin");
                }
                gotoActivity(MainActivity.class, true);
            }

            @Override
            public void onFiled(String message) {
                gotoActivity(LoginActivity.class, true);
            }
        });
    }


    /**
     * 活体登录成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void liveSuress(FaceLoginEvent event) {
        if (event.isSuress) {
            getUserInfo();
        }
    }


}
