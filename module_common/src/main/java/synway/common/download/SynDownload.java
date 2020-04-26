package synway.common.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Response;
import synway.common.okhttp.OkHttpUtils;
import synway.common.okhttp.callback.Callback;
import synway.common.okhttp.request.RequestCall;

/**
 * Created by admin on 2016/11/18.
 */
public class SynDownload {
    public static RequestCall httpDownload(String urlStr, String filePath, String fileName, Callback callback) {
        RequestCall requestCall = OkHttpUtils.get().url(urlStr).tag(fileName).build();
        return requestCall;
    }

    public static RequestCall httpDownload(String urlStr, Object object) {
        return OkHttpUtils.get().url(urlStr).tag(object).build();
    }

    //取消请求
    public static void cancelRequest(Object object) {
        OkHttpUtils.getInstance().cancelTag(object);
    }

    public static String httpDownload(String urlStr) {
        return httpDownload(urlStr, 5000, 5000);
    }

    public static String httpDownload(String urlStr, int readTimeOut, int connectTimeOut) {
        RequestCall call = OkHttpUtils.get().url(urlStr).build();
        call.readTimeOut(readTimeOut);
        call.connTimeOut(connectTimeOut);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return response.body().toString();
            } else {
                return "";
            }
        } catch (IOException e) {
            return e.toString();
        }

    }

    public static boolean httpDownload(File file, String urlStr) {
        return httpDownload(file, urlStr, 5000, 5000, null);
    }

    public static boolean httpDownload(File file, String urlStr, Object tag) {
        return httpDownload(file, urlStr, 5000, 5000, tag);
    }
    public static String[] httpDownloadNew(File file, String urlStr) {
        return httpDownloadNew(file, urlStr, 5000, 5000, null);
    }
    public static boolean httpDownload(File file, String urlStr, int readTimeOut, int connectTimeOut, Object tag) {
        RequestCall call = OkHttpUtils.get().url(urlStr).tag(tag).build();
        call.readTimeOut(readTimeOut);
        call.connTimeOut(connectTimeOut);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                if (file.exists()) {
                    file.delete();
                }
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                try {
                    file.createNewFile();
                    if (response.body().contentLength() <= 0) return false;
                    inputStream = response.body().byteStream();
                    outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    // 开始写文件
                    int flag = -1;
                    while ((flag = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, flag);
                    }


                } catch (IOException e1) {
                    return false;
                } finally {
                    if (inputStream != null && outputStream != null) {
                        inputStream.close();
                        outputStream.close();
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

    }

    public static String[] httpDownloadNew(File file, String urlStr, int readTimeOut, int connectTimeOut, Object tag) {
        String[] result=new String[2];
        RequestCall call = OkHttpUtils.get().url(urlStr).tag(tag).build();
        call.readTimeOut(readTimeOut);
        call.connTimeOut(connectTimeOut);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                if (file.exists()) {
                    file.delete();
                }
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                try {
                    file.createNewFile();
                    if (response.body().contentLength() <= 0){
                        result[0]="500";
                        result[1]="服务器错误";
                        return result;}
                    inputStream = response.body().byteStream();
                    outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    // 开始写文件
                    int flag = -1;
                    while ((flag = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, flag);
                    }


                } catch (IOException e1) {
                    result[0]="500";
                    result[1]=e1.toString();
                    return result;
                } finally {
                    if (inputStream != null && outputStream != null) {
                        inputStream.close();
                        outputStream.close();
                    }
                }
                result[0]="200";
                return result;
            } else {
                result[0]="500";
                result[1]="服务器无响应";
                return result;
            }
        } catch (IOException e) {
            result[0]="500";
            result[1]=e.toString();
            return result;
        }
    }

}
