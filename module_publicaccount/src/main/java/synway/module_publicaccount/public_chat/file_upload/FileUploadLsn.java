package synway.module_publicaccount.public_chat.file_upload;

/**
 * onError(),onFinish(),onProgress()为必须自己实现<p>
 * onStart（）和onCancle（）可以选择是否自己去重写，避免在不需要其回调时的冗余代码<p>
 * Created by dell on 2016/8/22.
 */
public abstract class FileUploadLsn {
    abstract void onError(Object o, int errorCode, String errorMsg);

    abstract void onFinish(Object o,String resultJson);

    abstract void onProgress(Object o, int progress, long uploaded);

    void onStart(Object o) {
    }

    void onCancle(Object o) {
    }

}
