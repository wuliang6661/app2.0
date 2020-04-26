package cn.synway.app.ui.downmap;

import android.support.annotation.MainThread;

import java.util.List;

import cn.synway.app.bean.MyAreaDO;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseRequestView;
import cn.synway.app.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class DownMapContract {
    interface View extends BaseRequestView {

        void getData(List<MyAreaDO> list);
        @MainThread
        void updateItemState(MyAreaDO entity);
    }

    interface Presenter extends BasePresenter<View> {
        void getMapInfo();
        void downMap(MyAreaDO entity);
    }
}
