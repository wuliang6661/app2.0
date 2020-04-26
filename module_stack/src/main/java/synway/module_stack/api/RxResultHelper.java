package synway.module_stack.api;

import android.util.Log;

import rx.Observable;
import rx.schedulers.Schedulers;
import synway.module_stack.hm_leave.net.BaseResult;
import rx.android.schedulers.AndroidSchedulers;
/**
 * 作者 by wuliang 时间 16/11/24.
 */

public class RxResultHelper {
    private static final String TAG = "RxResultHelper";

    public static <T> Observable.Transformer<BaseResult<T>, T> httpRusult() {

        return apiResponseObservable -> apiResponseObservable.flatMap(
                mDYResponse -> {
                    Log.d(TAG, "call() called with: mDYResponse = [" + mDYResponse + "]");
                    if (mDYResponse.suress()) {
                        return createData(mDYResponse.getData());
                    } else {
                        return Observable.error(new RuntimeException(mDYResponse.getErr()));
                    }
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    private static <T> Observable<T> createData(final T t) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(t);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
