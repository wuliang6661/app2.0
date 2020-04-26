package cn.synway.app.ui.login;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.AppConfigBO;
import cn.synway.app.bean.request.LoginRequest;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LoginPresenter extends BasePresenterImpl<LoginContract.View> implements LoginContract.Presenter {

    public void login(LoginRequest request) {
        HttpServerImpl.login(request).subscribe(new HttpResultSubscriber<AppConfigBO>() {
            @Override
            public void onSuccess(AppConfigBO s) {
                if (mView != null) {
                    mView.getConfigBo(s);
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


    public void bindIMEI(String imei) {
        HttpServerImpl.bindImei(imei).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                if (mView != null) {
                    mView.bindSourss();
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


    public void getUserInfo() {
        HttpServerImpl.getUserInfo().subscribe(new HttpResultSubscriber<UserEntry>() {
            @Override
            public void onSuccess(UserEntry s) {
                if (mView != null) {
                    mView.getUserInfo(s);
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
