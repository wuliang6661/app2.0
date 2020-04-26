package cn.synway.app.ui.main;

import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainContract {
    interface View extends BaseRequestView {

        void upFileSourss(String url);

        void updatePicSoress();

        void getUserInfo(UserEntry userBO);

        void addWaterMaker(String water);
    }

    interface Presenter extends BasePresenter<View> {

    }
}
