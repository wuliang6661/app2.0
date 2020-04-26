package cn.synway.app.mvp;

/**
 * 作者 by wuliang 时间 16/10/31.
 * 所有数据回调公共的父类接口
 */

public interface BaseRequestView extends BaseView {
    void onRequestError(String msg);

    void onRequestEnd();
}
