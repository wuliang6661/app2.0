package synway.module_publicaccount.public_favorite;

import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import qyc.library.tool.exp.ThrowExp;
import qyc.library.tool.http.HttpHead;
import qyc.library.tool.http.HttpPost;

/**
 * 当从本地没有获取到本地数据库全部公众号的信息，进一步发送网络请求获取关于全部公众号的数据，
 * 目的是判断该用户是否有公众号
 */


class SyncGetPublicInfoByHttp {

    private boolean isStop = false;

    private Handler handler = null;


    public SyncGetPublicInfoByHttp() {
        handler = new Handler();
    }


    public void start(String ip, int port, String userID) {
        if (isStop) {
            ThrowExp.throwRxp("【异模】  在stop之后不允许start");
            return;
        }

        String url = HttpHead.urlHead(ip, port)
            + "PFService/PublicFunction/GetFunctionList.osc";
        new Thread(new mRunnable(url, userID)).start();
    }


    public void stop() {
        isStop = true;
        this.onGetPublicInfoByHttpListener = null;
    }


    private class mRunnable implements Runnable {

        private String url = null;
        private String userID = null;


        private mRunnable(String url, String userID) {
            this.url = url;
            this.userID = userID;

        }


        @Override
        public void run() {
            JSONObject requestJson = getJson();

            //			MLog.Log("liujie", url);
            //			MLog.Log("liujie", requestJson.toString());

            final JSONObject resultJson = HttpPost.postJsonObj(url, requestJson);
            final String reuslt[] = HttpPost.checkResult(resultJson);
            if (null != reuslt) {
                if (null == onGetPublicInfoByHttpListener) {
                    return;
                }

                final String deatil = resultJson.optString("Detail");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onGetPublicInfoByHttpListener != null) {
                            onGetPublicInfoByHttpListener.onFail(reuslt[0], reuslt[0] + ",请重试",
                                reuslt[1] + "\n" + deatil);
                        }

                    }
                });
                return;
            }

            JSONArray jsonArray = resultJson.optJSONArray("FC_BASIC_INFO");
            int length = jsonArray.length();
            if (length == 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != onGetPublicInfoByHttpListener) {
                            onGetPublicInfoByHttpListener.onEmptyResult();
                        }
                    }
                });
                return;
            }


            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != onGetPublicInfoByHttpListener) {
                        onGetPublicInfoByHttpListener.onExistResult();
                    }
                }
            });

        }


        private JSONObject getJson() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("USER_ID", userID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

    }


    public interface OnGetPublicInfoByHttpListener {

        void onExistResult();
        void onFail(String title, String reason, String detail);
        void onEmptyResult();
    }


    private OnGetPublicInfoByHttpListener onGetPublicInfoByHttpListener = null;


    public void setOnGetPublicInfoByHttpListener(OnGetPublicInfoByHttpListener onGetPublicInfoByHttpListener) {

        this.onGetPublicInfoByHttpListener = onGetPublicInfoByHttpListener;
    }

}