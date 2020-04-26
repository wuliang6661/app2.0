package synway.module_stack.api;

import android.app.Dialog;

import java.net.SocketTimeoutException;

import rx.Subscriber;
import synway.module_stack.api.exception.NetException;

/**
 * Created by wuliang on 2018/11/13.
 * <p>
 * 自定义的Subscriber订阅者
 */

public abstract class HttpResultSubscriber<T> extends Subscriber<T> {

    /**
     * 滚动的菊花
     */
    private Dialog progressDialog = null;


    public HttpResultSubscriber() {

    }


    @Override
    public void onNext(T t) {
        onSuccess(t);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        //在这里做全局的错误处理
        if (e instanceof NetException) {
            //网络错误
            NetException exception = (NetException) e;
            onFiled(exception.code, exception.message);
        } else if (e instanceof SocketTimeoutException) {
            onFiled("请求失败！", "连接服务器错误！");
        } else {
            onFiled("请求失败！", e.getMessage());
        }

    }


    @Override
    public void onCompleted() {

    }

    public abstract void onSuccess(T t);

    public abstract void onFiled(String code, String message);
}
