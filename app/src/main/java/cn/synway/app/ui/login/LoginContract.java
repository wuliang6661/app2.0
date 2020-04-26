package cn.synway.app.ui.login;

import cn.synway.app.bean.AppConfigBO;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LoginContract {
    interface View extends BaseRequestView {

        void getConfigBo(AppConfigBO configBO);

        void bindSourss();

        void getUserInfo(UserEntry userBO);
    }

    interface Presenter extends BasePresenter<View> {

    }
}
