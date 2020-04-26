package synway.module_publicaccount.public_list_new;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import synway.module_interface.db.SQLite;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicConfigMsg;
import synway.module_publicaccount.public_list_new.adapter.PublicGridItem;
import synway.module_publicaccount.public_list_new.adapter.UrlObj;
import synway.module_publicaccount.publiclist.bean.App_InformationBean;
import synway.module_publicaccount.until.StringUtil;

/**
 * Created by leo on 2018/6/19.
 */

public class SynGetPublicAccountDB {
    private Handler handler;

    public SynGetPublicAccountDB() {
        this.handler = new Handler();
    }

    public void start() {
        new Thread(new MRunnable()).start();
    }

    private class MRunnable implements Runnable {

        @Override
        public void run() {
            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
            SQLiteDatabase db = sqliteHelp.getReadableDatabase();

            String selectSql = "select * from " + Table_PublicAccount._TABLE_NAME;
            Cursor cursor = db.rawQuery(selectSql, null);
            final ArrayList<PublicGridItem> resultList = new ArrayList<>();
            while (cursor.moveToNext()) {
                PublicGridItem item = new PublicGridItem();
                item.id = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_ID));
                item.name = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_NAME));
                if (cursor.isNull(cursor.getColumnIndex(Table_PublicAccount.FC_TYPE))) {
                    //兼容之前的数据,若是升级后此列没有值,则默认为0
                    item.type = 0;
                } else {
                    String typeStr = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_TYPE));
                    item.type = Integer.parseInt(typeStr);
                    if (item.type == 1) {
                        //当tpye为1的时候,给appInformation变量赋值,其他情况均为null
                        String appInformationJson = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.APP_INFORMATION));
                        item.app_information = getAppInformation(appInformationJson);
                    } else if (item.type == 2) {
                        //
                        String strs[];
                        String columns[] = new String[]{Table_PublicConfigMsg.PAC_ID, Table_PublicConfigMsg.PAM_PublicUrlType, Table_PublicConfigMsg.PAM_PublicUrl, Table_PublicConfigMsg.PAC_SourceUrl,Table_PublicConfigMsg.PAM_ISSHowTitle};
                        try {
                            strs = SQLite.query(sqliteHelp.getReadableDatabase(),
                                    Table_PublicConfigMsg._TABLE_NAME, columns, "|", null, null,
                                    null);
                        } catch (Exception e) {
                            String s = e.toString();
                            return;
                        } finally {
                        }
                        for (int i = 0; i < strs.length; i++) {
                            String sList[] = strs[i].split("\\|", -1);
                            if(sList[0].equals(item.id)){
                                UrlObj urlObj = new UrlObj();
                                urlObj.urlType = Integer.parseInt(sList[1]);
                                urlObj.publicUrl = sList[2];
                                urlObj.urlParam = sList[3];
                                urlObj.isShowTitle = Integer.parseInt(sList[4]);
                                item.urlObj = urlObj;
                                break;
                            }
                        }
                    }
                }
                item.mobilePic = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_MOBILEPIC));
                item.fatherGroupID = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.PAM_FatherGroupID));
                item.fatherGroupName = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.PAM_FatherGroupName));
                resultList.add(item);

            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onResult(resultList);
                    }
                }
            });


        }
    }


    public interface OnGetPublicAccountDBListener {
        void onResult(ArrayList<PublicGridItem> resultList);
    }

    private OnGetPublicAccountDBListener listener;

    public void setListener(OnGetPublicAccountDBListener listener) {
        this.listener = listener;
    }


    private App_InformationBean getAppInformation(String appInformationJson) {

        App_InformationBean app_informationBean = new App_InformationBean();
        if (StringUtil.isEmpty(appInformationJson)) {
            return app_informationBean;
        }
        try {
            JSONObject jsonObject = new JSONObject(appInformationJson);
            app_informationBean.app_version = jsonObject.optString("APP_VERSION");
            app_informationBean.app_packangename = jsonObject.optString("APP_PACKAGENAME");
            app_informationBean.app_newinformation = jsonObject.optString("APP_NEWINFORMATION");
            app_informationBean.app_download_url = jsonObject.optString("APP_DOWNLOAD_URL");
            app_informationBean.app_name = jsonObject.optString("APP_NAME");
        } catch (JSONException e) {
            e.printStackTrace();
            return app_informationBean;
        }
        return app_informationBean;
    }

    public void stop() {
        if (listener != null) {
            listener = null;
        }
    }
}
