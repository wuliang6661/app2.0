package cn.synway.app.ui.main.publicappcenter;

import java.util.List;

import cn.synway.app.bean.NetAPPBO;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PublicAppCenterContract {
    interface View extends BaseView {

        void setNetAPPData(List<NetAPPBO> list);
        void onFaild();
    }

    interface Presenter extends BasePresenter<View> {
        void getData();
    }
}
