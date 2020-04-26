package cn.synway.app.ui.main.personlist.personinfo;

import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PersonInfoContract {
    interface View extends BaseView {
        void setUser(UserEntry user);
    }

    interface Presenter extends BasePresenter<View> {
        void getPersonInfo(String id);

    }
}
