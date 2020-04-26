package synway.common.http;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;
import synway.common.okhttp.OkHttpUtils;
import synway.common.okhttp.callback.Callback;
import synway.common.okhttp.request.RequestCall;

/**
 * HTTP请求的POST,用来发送消息
 */
public class SynHttp {

    /**
     * @param url
     * @param jsonStr
     * @return 同步POST, 默认读取时间5秒, 连接超时时间5秒
     */
    public static String httpSend(String url, String jsonStr) {
        return httpSend(url, jsonStr, 5000, 5000);
    }

    /**
     * @param url
     * @param jsonStr
     * @param readTimeOut
     * @param connectTimeOut
     * @return 同步POST
     */
    public static String httpSend(String url, String jsonStr, int readTimeOut, int connectTimeOut) {
        RequestCall call = OkHttpUtils.postString().url(url).content(jsonStr).mediaType(MediaType.parse("application/json; charset=utf-8")).build();
        call.readTimeOut(readTimeOut);
        call.connTimeOut(connectTimeOut);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                return null;
            }
        } catch (IOException e) {
            return e.toString();
        }
    }

    /**
     * @param url
     * @param jsonStr
     * @param callback
     * @return 异步POST方法, 传入回调callback, 取消请求通过RequestCall.cancel()
     * 默认读取时间5秒,连接超时时间5秒
     */
    public static RequestCall httpSend(String url, String jsonStr, Callback callback) {
        RequestCall call = OkHttpUtils.postString().url(url).content(jsonStr).mediaType(MediaType.parse("application/json; charset=utf-8")).build();
        call.execute(callback);
        return call;
    }

    /**
     * @param url
     * @param jsonObject
     * @return 同步POST, 默认读取时间5秒, 连接超时时间5秒
     */
    public static JSONObject httpSend(String url, JSONObject jsonObject) {
        return httpSend(url, jsonObject, 5000, 5000);
    }

    /**
     * @param url
     * @param jsonObject
     * @param readTimeOut
     * @param connectTimeOut
     * @return 同步POST
     */
    public static JSONObject httpSend(String url, JSONObject jsonObject, int readTimeOut, int connectTimeOut) {
        RequestCall call = OkHttpUtils.postString()
                .addHeader("Connection", "close")//增加关闭连接，不让它保持连接
                .url(url)
                .content(jsonObject.toString()
                ).mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().readTimeOut(readTimeOut)
                .connTimeOut(connectTimeOut);
        JSONObject resultJson = null;
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                resultJson = new JSONObject(response.body().string());
            }
        } catch (JSONException e) {
            resultJson = getExpJSON(-2, e.toString());
        } catch (IOException e) {
            resultJson = getExpJSON(-1, e.toString());
        }
        return resultJson;
    }

    /**
     * @param url
     * @param jsonObject
     * @param callback
     * @return 异步POST方法, 传入回调callback, 取消请求通过RequestCall.cancel()
     * 默认读取时间5秒,连接超时时间5秒
     */
    public static RequestCall httpSend(String url, JSONObject jsonObject, Callback callback) {
        return httpSend(url, jsonObject, 5000, 5000, callback);
    }

    public static RequestCall httpSend(String url, JSONObject jsonObject, int readTimeOut, int connectTimeOut, Callback callback) {
        RequestCall call = OkHttpUtils.postString()
                .url(url)
                .content(jsonObject.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8")
                ).build().readTimeOut(readTimeOut)
                .connTimeOut(connectTimeOut);
        call.execute(callback);
        return call;
    }


    private static JSONObject getExpJSON(int result, String wrongMsg) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", result);
            jsonObject.put("REASON", wrongMsg);
        } catch (JSONException var4) {
            var4.printStackTrace();
        }
        return jsonObject;
    }
}
