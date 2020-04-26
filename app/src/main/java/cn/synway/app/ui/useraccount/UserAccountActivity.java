package cn.synway.app.ui.useraccount;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.base.SynApplication;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.push.PushManager;
import cn.synway.app.ui.editpass.EditPassActivity;
import cn.synway.app.ui.login.LoginActivity;
import cn.synway.app.utils.AppManager;
import cn.synway.app.utils.SynCountlyFactory;
import cn.synway.app.widget.AlertDialog;


/**
 * MVPPlugin
 * 账号与安全页面
 */

public class UserAccountActivity extends MVPBaseActivity<UserAccountContract.View, UserAccountPresenter>
        implements UserAccountContract.View {

    @BindView(R.id.edit_password)
    LinearLayout editPassword;
    @BindView(R.id.logout)
    LinearLayout logout;

    @Override
    protected int getLayout() {
        return R.layout.act_user_account;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitleText("账号与安全");
    }


    @OnClick(R.id.edit_password)
    public void editPassword() {
        if (UserIml.getUser().getType() == 0) {   //自己平台添加的用户
            gotoActivity(EditPassActivity.class, false);
        }
        else {
            showToast("该用户暂不支持在手机上修改密码！");
        }
    }


    @OnClick(R.id.logout)
    public void loginOut() {
        new AlertDialog(this).builder().setGone().setMsg("是否确认退出登录？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", v -> {
                    SynApplication.spUtils.put("isOpenLogin", false);
                    PushManager.getInstance().destory();
                    showProgress("");
                    new Handler().postDelayed(() -> {   //3秒的延迟，以便长连接池能够完全退出
                        SynCountlyFactory.clearSynCountlyUserData();
                        SynCountlyFactory.destorySynCountly();
                        stopProgress();
                        AppManager.getAppManager().finishAllActivity();
                        gotoActivity(LoginActivity.class, true);
                    }, 3000);
                }).show();
    }

}
