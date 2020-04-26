package cn.synway.app.ui.main.message;

import java.util.List;

import cn.synway.app.bean.MessageFcBo;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MessageContract {
    interface View extends BaseRequestView {

        void getFcMenuList(List<MessageFcBo> messageFcBos);
    }

    interface Presenter extends BasePresenter<View> {

    }
}
