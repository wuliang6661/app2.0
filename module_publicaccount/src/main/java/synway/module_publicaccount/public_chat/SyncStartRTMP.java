package synway.module_publicaccount.public_chat;

import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qyc.library.tool.http.HttpPost;

/**
 * Created by QSJH on 2016/6/12 0012.
 */
public class SyncStartRTMP {
    private String rtmpUrl;
    private String urlName;
    private Handler handler = new Handler();

    public void start(String postUrl, String rtmpUrl, String urlName) {
        this.rtmpUrl = rtmpUrl;
        this.urlName = urlName;
        new Thread(new MRunnable(postUrl)).start();
    }

    public void stop() {
        this.onStartRTMPResult = null;
    }

    private class MRunnable implements Runnable {
        private String url;

        public MRunnable(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            // 这个HTTP请求用来激活流媒体服务器
            // RESTFUL风格API,不需要请求参数，所以直接new一个JSON
            // 应答字符串不是一个JSON，格式类似于syn_resp({"code": 0 ,"desc":"ok"})
            // 所以这里要二次处理一下应答结果
            JSONObject resJSON = HttpPost.postJsonObj(url, new JSONObject());
            int result = resJSON.optInt("RESULT", 999);
            Log.i("testy","语音请求结果"+ result);
            if (result != 999) {
                if(result==-1) {
                    String err = resJSON.optString("REASON");
                    onNotifyFail("激活语音", "激活语音失败", err);
                    Log.i("testy","语音请求结果2222222"+ err);
                    return;
                }
            }

            Log.i("testy", "激活语音HTTP结果:" + resJSON);
            try {
                String regex = ".*\\((.*)\\).*";// 过滤出小括号内的字符串
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(resJSON.getString("REASON"));
//                Log.i("testy", "匹配结果:" + matcher.group(1));

                if (matcher.find()) {
                    resJSON = new JSONObject(matcher.group(1));
                }
            } catch (Exception e) {
                Log.i("testy", "激活失败了哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈:" +e);
                onNotifyFail("激活语音", "激活语音失败", e.toString());
                return;
            }

            if (resJSON.optInt("code", -1) == 0) {
                onNotifySuccess(rtmpUrl);
            } else {
                onNotifyFail("激活语音", "激活语音失败", resJSON.optString("desc"));
            }


        }
    }

    private void onNotifySuccess(final String rtmpUrl) {
        if (onStartRTMPResult != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onStartRTMPResult.onSuccess(rtmpUrl, urlName);
                }
            });

        }
    }

    private void onNotifyFail(final String title, final String reason, final String detail) {
        if (onStartRTMPResult != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onStartRTMPResult.onFail(title, reason, detail);
                }
            });

        }
    }

    private OnStartRTMPResult onStartRTMPResult;

    public void setOnStartRTMPResult(OnStartRTMPResult onStartRTMPResult) {
        this.onStartRTMPResult = onStartRTMPResult;
    }

    public interface OnStartRTMPResult {

        void onSuccess(String rtmpURL, String urlName);

        void onFail(String title, String reason, String detail);
    }
}
