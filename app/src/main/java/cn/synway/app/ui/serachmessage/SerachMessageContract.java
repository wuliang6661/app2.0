package cn.synway.app.ui.serachmessage;

import cn.synway.app.bean.MessageListBo;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SerachMessageContract {
    interface View extends BaseRequestView {

        void getMessageList(boolean isPageAdd, MessageListBo messageListBo);

        void readSourss();
    }

    interface Presenter extends BasePresenter<View> {

    }
}
