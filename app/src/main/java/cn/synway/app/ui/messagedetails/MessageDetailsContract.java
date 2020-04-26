package cn.synway.app.ui.messagedetails;

import java.util.List;

import cn.synway.app.bean.FcTypeBo;
import cn.synway.app.bean.MessageListBo;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MessageDetailsContract {
    interface View extends BaseRequestView {

        void getFcTypeList(List<FcTypeBo> fcTypeBos);

        void getMessageList(boolean isAddPage, MessageListBo messageListBo);

        void readSourss();
    }

    interface Presenter extends BasePresenter<View> {

    }
}
