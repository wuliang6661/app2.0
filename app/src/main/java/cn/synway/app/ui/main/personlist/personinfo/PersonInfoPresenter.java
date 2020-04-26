package cn.synway.app.ui.main.personlist.personinfo;

import com.blankj.utilcode.util.ToastUtils;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PersonInfoPresenter extends BasePresenterImpl<PersonInfoContract.View> implements PersonInfoContract.Presenter {

    @Override
    public void getPersonInfo(String id) {

        HttpServerImpl.getUserInfoById(id).subscribe(new HttpResultSubscriber<UserEntry>() {
            @Override
            public void onSuccess(UserEntry user) {
                if (mView != null) {
                    mView.setUser(user);
                }
            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShort(message);
            }
        });
    }

}
