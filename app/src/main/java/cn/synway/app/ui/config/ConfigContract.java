package cn.synway.app.ui.config;

import cn.synway.app.db.table.ConfigEntry;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ConfigContract {
    interface View extends BaseRequestView {

        void checkSoress(ConfigEntry configEntry);
    }

    interface Presenter extends BasePresenter<View> {

    }
}
