package cn.synway.app.api;

import rx.Subscriber;

/**
 * Created by wuliang on 2018/11/13.
 * <p>
 * 自定义的Subscriber订阅者
 */

public abstract class HttpResultSubscriber<T> extends Subscriber<T> {



    public HttpResultSubscriber() {

    }



    @Override
    public void onNext(T t) {
        onSuccess(t);
    }


    @Override
    public void onError(Throwable e) {
        e.printStackTrace();

        onFiled(e.getMessage());
    }


    @Override
    public void onCompleted() {

    }


    public abstract void onSuccess(T t);

    public abstract void onFiled(String message);
}
