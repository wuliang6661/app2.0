package cn.synway.app.ui.editpass;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.ui.login.LoginActivity;
import cn.synway.app.utils.AppManager;
import cn.synway.app.utils.MD5Util;


/**
 * MVPPlugin
 * 修改密码
 */

public class EditPassActivity extends MVPBaseActivity<EditPassContract.View, EditPassPresenter>
        implements EditPassContract.View {

    @BindView(R.id.edit_old_pass)
    EditText editOldPass;
    @BindView(R.id.edit_new_pass)
    EditText editNewPass;
    @BindView(R.id.commit)
    Button commit;

    @Override
    protected int getLayout() {
        return R.layout.act_edit_password;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitleText("修改密码");
    }


    @OnClick(R.id.commit)
    public void commit() {
        String newPass = editNewPass.getText().toString().trim();
        String oldPass = editOldPass.getText().toString().trim();
        if (StringUtils.isEmpty(oldPass)) {
            showToast("请输入旧密码！");
            return;
        }
        if (StringUtils.isEmpty(newPass)) {
            showToast("请输入新密码！");
            return;
        }
        mPresenter.editPass(oldPass, newPass);
    }

    @Override
    public void onSuress() {
        showToast("修改成功！请重新登录！");
        AppManager.getAppManager().finishAllActivity();
        gotoActivity(LoginActivity.class, true);
    }

    @Override
    public void onRequestError(String msg) {
        showToast(msg);
    }
}
