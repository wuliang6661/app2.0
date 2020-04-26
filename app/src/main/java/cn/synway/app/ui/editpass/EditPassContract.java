package cn.synway.app.ui.editpass;

import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class EditPassContract {
    interface View extends BaseRequestView {

        void onSuress();
    }

    interface Presenter extends BasePresenter<View> {

    }
}
