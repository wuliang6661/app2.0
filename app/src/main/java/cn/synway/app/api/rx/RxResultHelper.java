package cn.synway.app.api.rx;

import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import cn.synway.app.base.SynApplication;
import cn.synway.app.bean.BaseResult;
import cn.synway.app.db.DataManager;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 作者 by wuliang 时间 16/11/24.
 */

public class RxResultHelper {
    private static final String TAG = "RxResultHelper";

    /**
     * 所有请求的共有数据过滤机制
     */
    public static <T> Observable.Transformer<BaseResult<T>, T> httpRusult() {
        return apiResponseObservable -> apiResponseObservable.flatMap(
                (Func1<BaseResult<T>, Observable<T>>) mDYResponse -> {
                    Log.d(TAG, "call() called with: mDYResponse = [" + mDYResponse + "]");
                    if (mDYResponse.surcess()) {
                        return createData(mDYResponse.getData());
                    } else if (mDYResponse.getCode() == -2) {   //清除本机所有缓存数据
                        LogUtils.e("本机已禁用！清除本机所有数据！");
                        SynApplication.spUtils.clear();
                        DataManager.getInstance().clearAll();
                    }
                    return Observable.error(new RuntimeException(mDYResponse.getMsg()));
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 下载文件的过滤机制
     */
    public static Observable.Transformer<ResponseBody, ResponseBody> downRequest(File file) {
        return apiResponseObservable -> apiResponseObservable
                .observeOn(Schedulers.io())
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        return write2File(responseBody, file);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    private static <T> Observable<T> createData(final T t) {
        return Observable.create(subscriber -> {
            try {
//                subscriber.setPageInfo(pageBO);
                subscriber.onNext(t);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    private static Observable<ResponseBody> write2File(ResponseBody body, File file) {
        return Observable.create(subscriber -> {
            try {
                FileUtils.createOrExistsFile(file);
                InputStream inputStream = body.byteStream();
                //long totalSize = body.contentLength();
                // long currentLength = 0;

                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                byte[] buffer = new byte[1024];
                int len;

                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                    //currentLength += len;
                    //LogUtils.e("write2File", currentLength + "");
                    //subscriber.onNext((int) (100 * currentLength / totalSize));
                }
                fos.close();
                bis.close();
                inputStream.close();
                subscriber.onNext(body);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

}
