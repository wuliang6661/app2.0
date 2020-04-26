package cn.synway.app.ui.messagedetails;

import java.util.List;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.FcTypeBo;
import cn.synway.app.bean.MessageListBo;
import cn.synway.app.bean.request.MessageRequest;
import cn.synway.app.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MessageDetailsPresenter extends BasePresenterImpl<MessageDetailsContract.View>
        implements MessageDetailsContract.Presenter {


    public void getMsgTypeList(String fcId) {
        HttpServerImpl.getFcType(fcId).subscribe(new HttpResultSubscriber<List<FcTypeBo>>() {
            @Override
            public void onSuccess(List<FcTypeBo> s) {
                if (mView != null) {
                    mView.getFcTypeList(s);
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

    private int page = 1;

    public void getMessageList(String fcId, String typeId, boolean isAddPage) {
        MessageRequest request = new MessageRequest();
        if (isAddPage) {
            page++;
            request.setCurrentPage(page);
        } else {
            page = 1;
            request.setCurrentPage(page);
        }
        request.setPageSize(30);
        request.setPushType(fcId);
        request.setPcId(typeId);
        HttpServerImpl.getMessageList(request).subscribe(new HttpResultSubscriber<MessageListBo>() {
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
