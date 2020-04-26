package synway.module_publicaccount.public_list_new;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import qyc.library.tool.exp.ThrowExp;
import qyc.library.tool.http.HttpPost;
import synway.module_interface.db.SQLite;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicConfigMsg;
import synway.module_publicaccount.public_list_new.adapter.PublicGridItem;
import synway.module_publicaccount.publiclist.GetHttpData.GetPublicList2;
import synway.module_publicaccount.publiclist.GetHttpData.PublicPost;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;

/**
 * Created by leo on 2018/6/21.
 */

public class SyncGetPublicByHttp3 {
    private boolean isStop = false;

    private Handler handler = null;
    public volatile boolean isDownloading = false;

    public SyncGetPublicByHttp3() {
        handler = new Handler();
    }

    public void start(String ip, int port, String userID) {
        if (isStop) {
            ThrowExp.throwRxp("【异模】  在stop之后不允许start");
            return;
        }

        String url = GetPublicList2.geturl(ip, port, userID);
        new Thread(new mRunnable(url, userID)).start();
    }

    public void stop() {
        isStop = true;
        this.lsn = null;
    }

    private class mRunnable implements Runnable {

        private String url = null;
        private String userID = null;

        private ArrayList<Obj_PublicAccount> AllList = null;
        private ArrayList<Obj_PublicAccount> resultList = null;

        private HashMap<String, Integer> selePos = null;

        private ArrayList<String> idList = null;
        private ArrayList<Obj_PublicAccount> appList = null;

        private mRunnable(String url, String userID) {
            this.url = url;
            this.userID = userID;
        }

        @Override
        public void run() {
            isDownloading = true;
            JSONObject requestJson = GetPublicList2.getJson(userID);

            final JSONObject resultJson = HttpPost.postJsonObj(url, requestJson);

            Log.e("wuliang", resultJson.toString());
            isDownloading = false;

            final ArrayList<PublicGridItem> resultList = new ArrayList<>();
            final String reuslt[] = PublicPost.checkResult(resultJson);
            if (null != reuslt) {
                if (null == lsn) {
                    return;
                }

                final String result = resultJson.optString("result");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lsn != null) {
                            lsn.onFail(reuslt[0], reuslt[0] + ",请重试", reuslt[1] + "\n" + result);
                        }
                    }
                });
                return;
            }
            final String proxy = resultJson.optString("global_proxy_url");
            JSONArray jsonArray = resultJson.optJSONArray("result");
            int length = jsonArray.length();
            if (length == 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != lsn) {
                            lsn.onResult(resultList, proxy);
                        }
                    }
                });
                SQLite.del(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(), Table_PublicAccount._TABLE_NAME, null, null);
                SQLite.del(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(), Table_PublicConfigMsg._TABLE_NAME, null, null);
                return;
            }


            final ArrayList<PublicGridItem> publicList = GetPublicList2.getPublicList2(jsonArray,
                    Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != lsn) {
                        lsn.onResult(publicList, proxy);
                    }
                }
            });

        }
    }

    public void setLsn(IOnGetAccountByHttp lsn) {
        this.lsn = lsn;
    }

    private IOnGetAccountByHttp lsn = null;

    public interface IOnGetAccountByHttp {

        void onResult(ArrayList<PublicGridItem> resultList, String proxyUrl);

        void onFail(String title, String reason, String detail);

    }
}
