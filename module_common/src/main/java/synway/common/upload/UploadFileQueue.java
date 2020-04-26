package synway.common.upload;

import java.util.HashMap;

import synway.common.okhttp.request.RequestCall;

public class UploadFileQueue {
    private HashMap<String, RequestCall> uploadList = null;
    private volatile static UploadFileQueue instance;

    public static UploadFileQueue instance() {
        if (instance == null) {
            synchronized (UploadFileQueue.class) {
                if (instance == null) {
                    instance = new UploadFileQueue();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        uploadList = new HashMap<>();
    }

    /***
     * 获取发送文件的RequestCall，如果没有则返回null
     *
     * @param MsgID
     * @return
     */
    public RequestCall getUploadFileRequestCall(String MsgID) {
        if (uploadList == null) {
            return null;
        }
        return uploadList.get(MsgID);
    }


    /**
     * 保存上传队列
     *
     * @param MsgID
     * @param requestCall
     */
    public void setUploadFileRequestCall(String MsgID, RequestCall requestCall) {
        uploadList.put(MsgID, requestCall);
    }

    /**
     * 移除上传队列
     *
     * @param MsgID
     */
    public void removeHandle(String MsgID) {
        uploadList.remove(MsgID);
    }

    /**
     * 停止上传
     *
     * @param MsgID
     */
    public boolean stopCall(String MsgID) {
        RequestCall requestCall = uploadList.remove(MsgID);
        if (requestCall != null) {
            requestCall.cancel();
            return true;
        } else {
            return false;
        }
    }
}
