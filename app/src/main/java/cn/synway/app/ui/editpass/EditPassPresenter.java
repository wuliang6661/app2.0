package cn.synway.app.ui.editpass;

import android.content.Context;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class EditPassPresenter extends BasePresenterImpl<EditPassContract.View>
        implements EditPassContract.Presenter {

    public void editPass(String oldPass, String newPass) {
        HttpServerImpl.updatePass(oldPass, newPass).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                if (mView != null) {
                    mView.onSuress();
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
            }
        });
    }


}
