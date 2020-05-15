package cn.synway.app.ui.serachmessage;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.MessageListBo;
import cn.synway.app.bean.request.MessageKeyRequest;
import cn.synway.app.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SerachMessagePresenter extends BasePresenterImpl<SerachMessageContract.View>
        implements SerachMessageContract.Presenter {

    private int page = 1;

    public void getMessageListByKey(String keyword, boolean isAddPage) {
        if (isAddPage) {
            page++;
        } else {
            page = 1;
        }
        MessageKeyRequest request = new MessageKeyRequest();
        request.currentPage = page;
        request.pageSize = 20000;
        request.keyword = keyword;
        HttpServerImpl.getMessageByKey(request).subscribe(new HttpResultSubscriber<MessageListBo>() {
            @Override
            public void onSuccess(MessageListBo s) {
                if (mView != null) {
                    mView.getMessageList(isAddPage, s);
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


    /**
     * 更新消息为已读
     */
    public void updateRead(String pushId) {
        HttpServerImpl.updateRead(pushId).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                if (mView != null) {
                    mView.readSourss();
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
