package synway.module_publicaccount.public_chat.file_upload;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import okhttp3.Call;
import okhttp3.Response;
import qyc.net.push.javapush.common.GetExpMsg;
import synway.common.okhttp.OkHttpUtils;
import synway.common.okhttp.callback.Callback;
import synway.common.okhttp.callback.StringCallback;
import synway.common.okhttp.request.RequestCall;
import synway.common.upload.UploadFileQueue;
import synway.module_publicaccount.public_chat.file_upload_for_camera.jobs.UploadManger;
import synway.module_publicaccount.public_chat.file_upload_for_camera.jobs.UploadProgress;
import synway.module_publicaccount.until.StringUtil;

/**
 * 从common模块复制过来的文件上传
 */

public class CamreaUpload {
    /**
     * @param url
     * @param filePath 文件地址
     * @param file_ext 文件默认后缀
     * @param type     文件类型(1=图片，2=视频，3=音频，4=其他)
     * @param msgID
     * @return [0]=失败信息 或null(成功) [1]=失败详细信息 或服务端应答
     * 同步上传文件,默认3分钟超时时间
     */
    public static String[] uploadFile(String url, String filePath, String file_ext, int type, String msgID) {
        return uploadFile(url, filePath, file_ext, type, 10, msgID);
    }

    public static String[] uploadFileSyn(String url, String filePath, String file_ext, int type, String msgID, UploadProgress uploadProgress) {
        return uploadFileSyn(url, filePath, file_ext, type, 10, msgID, uploadProgress);
    }

    /**
     * @param url
     * @param filePath 文件地址
     * @param file_ext 文件默认后缀
     * @param type     文件类型(1=图片，2=视频，3=音频，4=其他)
     * @param outTime  超时时间(单位秒)
     * @param msgID
     * @return [0]=失败信息 或null(成功) [1]=失败详细信息 或服务端应答
     * 同步上传文件
     */
    public static String[] uploadFile(String url, String filePath, String file_ext, int type, long outTime, String msgID) {
        String file_ext_temp = "";
        final String[] returnStr = new String[2];
        RequestCall call = null;
        File file = new File(filePath);
        if (file.exists()) {
            file_ext_temp = getFileSuffix(filePath);
            if (StringUtil.isEmpty(file_ext_temp)) {
                file_ext_temp = file_ext;
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("FILE_EXT", file_ext_temp);
                jsonObject.put("TYPE", type);
                jsonObject.put("FILE_NAME", getFilename(filePath));
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
     * @param url
     * @param filePath 文件地址
     * @param file_ext 文件默认后缀
     * @param type     文件类型(1=图片，2=视频，3=音频，4=其他)
     * @param outTime  超时时间(单位秒)
     * @param msgID
     * @return [0]=失败信息 或null(成功) [1]=失败详细信息 或服务端应答
     * 异步上传文件
     */
    public static String[] uploadFileSyn(String url, String filePath, String file_ext, int type, long outTime, String msgID, final UploadProgress uploadProgress) {
        String file_ext_temp = "";
        final String[] returnStr = new String[2];
        RequestCall call = null;
        File file = new File(filePath);
        if (file.exists()) {
            file_ext_temp = getFileSuffix(filePath);
            if (StringUtil.isEmpty(file_ext_temp)) {
                file_ext_temp = file_ext;
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("FILE_EXT", file_ext_temp);
                jsonObject.put("TYPE", type);
                jsonObject.put("FILE_NAME", getFilename(filePath));
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
        //阻塞队列
        final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        try {
            call.execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        if (queue != null && queue.size() == 0) {
                            queue.put(e.getMessage());
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        if (queue != null && queue.size() == 0) {
                            queue.put(response);
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

                @Override
                public void inProgress(float progress, long total, int id) {
                    super.inProgress(progress, total, id);
                    if (uploadProgress != null) {
                        uploadProgress.progress(-1, -1, total, progress);
                    }
                }
            });

            String response = null;
            queue.clear();
            response = queue.take();
            if (!StringUtil.isEmpty(response)) {
                JSONObject resultJson = new JSONObject(response);
                int resultCode = resultJson.optInt("RESPONSE_STATE", 500);
                if (resultCode == 200) {
                    returnStr[0] = null;
                    returnStr[1] = response;
                    UploadFileQueue.instance().removeHandle(msgID);
                } else {
                    returnStr[0] = "文件上传失败";
                    returnStr[1] = response;
                    UploadFileQueue.instance().removeHandle(msgID);
                }
            } else {
                returnStr[0] = "文件上传失败";
                returnStr[1] = "没有数据返回";
                UploadFileQueue.instance().removeHandle(msgID);
            }
        } catch (Exception e) {
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
     * 获取文件名,去后缀
     */
    private static String getFilename(String filepath) {
        String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
        if (filename.indexOf(".") >= 0) {
            filename = filename.substring(0, filename.indexOf("."));
        }
        return filename;
    }

    /**
     * 获取文件后缀
     */
    private static String getFileSuffix(String filepath) {
        if (filepath.contains(".")) {
            return filepath.substring(filepath.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

}
