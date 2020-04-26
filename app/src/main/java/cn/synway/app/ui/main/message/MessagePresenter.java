package cn.synway.app.ui.main.message;

import java.util.List;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.MessageFcBo;
import cn.synway.app.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MessagePresenter extends BasePresenterImpl<MessageContract.View> implements MessageContract.Presenter {


    public void getFcList() {
        HttpServerImpl.getFcList().subscribe(new HttpResultSubscriber<List<MessageFcBo>>() {
            @Override
            public void onSuccess(List<MessageFcBo> s) {
                if (mView != null) {
                    mView.getFcMenuList(s);
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
            }
        });
    }

}
