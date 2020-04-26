package synway.common.upload;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;
import qyc.net.push.javapush.common.GetExpMsg;
import synway.common.okhttp.OkHttpUtils;
import synway.common.okhttp.callback.Callback;
import synway.common.okhttp.request.RequestCall;

/**
 * 同异步POST上传文件方法类
 */
public class SynUpload {
    /**
     * @param url
     * @param filePath 文件地址
     * @param file_ext 文件后缀
     * @param type     文件类型(1=图片，2=视频，3=音频，4=其他)
     * @param msgID
     * @param callback 回调callback,需要进度复写callback的inProgress
     * @return [0]=失败信息 或null(成功) [1]=失败详细信息 或服务端应答
     * 异步上传文件,默认3分钟超时时间
     */
    public static RequestCall uploadFile(String url, String filePath, String file_ext, int type, String msgID, Callback callback) {
        return uploadFile(url, filePath, file_ext, type, 180, msgID, callback);
    }

    /**
     * @param url
     * @param filePath 文件地址
     * @param file_ext 文件后缀
     * @param type     文件类型(1=图片，2=视频，3=音频，4=其他)
     * @param outTime  超时时间(单位秒)
     * @param msgID    回调callback,需要进度复写callback的inProgress
     * @return [0]=失败信息 或null(成功) [1]=失败详细信息 或服务端应答
     * 同步上传文件
     */
    public static RequestCall uploadFile(String url, String filePath, String file_ext, int type, long outTime, String msgID, Callback callback) {

        return null;
    }

    /**
     * @param url
     * @param filePath 文件地址
     * @param file_ext 文件后缀
     * @param type     文件类型(1=图片，2=视频，3=音频，4=其他)
     * @param msgID
     * @return [0]=失败信息 或null(成功) [1]=失败详细信息 或服务端应答
     * 同步上传文件,默认3分钟超时时间
     */
    public static String[] uploadFile(String url, String filePath, String file_ext, int type, String msgID) {
        return uploadFile(url, filePath, file_ext, type, 10, msgID);
    }

    /**
     * @param url
     * @param filePath 文件地址
     * @param file_ext 文件后缀
     * @param type     文件类型(1=图片，2=视频，3=音频，4=其他)
     * @param outTime  超时时间(单位秒)
     * @param msgID
     * @return [0]=失败信息 或null(成功) [1]=失败详细信息 或服务端应答
     * 同步上传文件
     */
    public static String[] uploadFile(String url, String filePath, String file_ext, int type, long outTime, String msgID) {
        final String[] returnStr = new String[2];
        RequestCall call = null;
        File file = new File(filePath);
        if (file.exists()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("FILE_EXT", file_ext);
                jsonObject.put("TYPE", type);
            } catch (JSONException e) {
            }
            call = UploadFileQueue.instance().getUploadFileRequestCall(msgID);
            if (call == null) {
                call = OkHttpUtils
                        .post()
                        .addHeader("Connection", "close")//增加关闭连接，不让它保持连接
                        .addFile("FILE_INFO", jsonObject.toString(), file)
                        .url(url)
                        .build()
                        .connTimeOut(5000)
                        .readTimeOut(outTime * 1000)
                        .writeTimeOut(outTime * 1000);
                UploadFileQueue.instance().setUploadFileRequestCall(msgID, call);
            }
        } else {
            returnStr[0] = "要发送的文件不存在";
            returnStr[1] = filePath;
            return returnStr;
        }
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                returnStr[0] = null;
                returnStr[1] = response.body().string();
                UploadFileQueue.instance().removeHandle(msgID);
            } else {
                returnStr[0] = "文件上传失败";
                returnStr[1] = response.message();
                UploadFileQueue.instance().removeHandle(msgID);
            }
        } catch (IOException e) {
            //当取消请求时,会抛出异常到这里,在这里判断call的状态
            if (call.getCall().isCanceled()) {
                returnStr[0] = null;
                returnStr[1] = null;
                UploadFileQueue.instance().removeHandle(msgID);
            } else {
                returnStr[0] = "文件上传失败";
                returnStr[1] = GetExpMsg.detail(e);
                UploadFileQueue.instance().removeHandle(msgID);
            }
        }
        return returnStr;
    }

    /**
     * 上传文件给网页端
     *
     * @param url
     * @param filePath 文件地址
     * @param file_ext 文件后缀
     * @param type     文件类型(1=图片，2=视频，3=音频，4=其他)
     * @param outTime  超时时间(单位秒)
     * @return [0]=失败信息 或null(成功) [1]=失败详细信息 或服务端应答
     */
    public static String[] uploadFileToWeb(String url, String filePath, String file_ext, int type, long outTime) {
        final String[] returnStr = new String[2];
        RequestCall call = null;
        File file = new File(filePath);
        if (file.exists()) {
            String json = "{FILE_EXT:" + file_ext + ",TYPE:" + type + "}";
            call = OkHttpUtils
                    .post()
                    .addFile("FILE_INFO", json, file)
                    .url(url)
                    .build()
                    .connTimeOut(180000)
                    .readTimeOut(outTime * 1000);
        } else {
            returnStr[0] = "文件不存在";
            returnStr[1] = "文件不存在";
            return returnStr;
        }
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                returnStr[0] = null;
                returnStr[1] = response.body().string();
            } else {
                returnStr[0] = "文件上传失败";
                returnStr[1] = response.message();
            }
        } catch (IOException e) {
            returnStr[0] = "文件上传失败";
            returnStr[1] = e.toString();
        }
        return returnStr;
    }
}
