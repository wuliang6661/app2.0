package synway.module_publicaccount.public_chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import synway.module_interface.config.ThrowExp;
import synway.module_interface.db.SQLite;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.analytical.fac.AnalyticalPath;
import synway.module_publicaccount.analytical.fac.IAnalytical_Base;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;

/**
 * 从PublicAccountRecord数据库解析数据
 */
public class SyncGetRecord {

    public static final void creatTable(String publicGUID) {
        // SQLiteDatabase db = null;
        SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
        // try {
        // db = SQLiteDatabase.openDatabase(BaseUtil.getDBPath(context), null,
        // SQLiteDatabase.OPEN_READWRITE);
        // } catch (Exception e) {
        // return;
        // }

        sqliteHelp.getWritableDatabase().execSQL(
                Table_PublicAccountRecord.getCreatTableSql(publicGUID));

        // db.beginTransaction();
        // db.setTransactionSuccessful();
        // db.endTransaction();

        // db.close();
    }

    private int COUNT = 5;
    private Handler handler = null;

    private SparseArray<IAnalytical_Base> analys = null;

    public SyncGetRecord(Context context) {
        this.handler = new Handler();
        this.analys = new SparseArray<IAnalytical_Base>();
        IAnalytical_Base analytical = null;
        for (int i = 0; i < AnalyticalPath.CLASS_PATH.length; i++) {
            try {
                analytical = (IAnalytical_Base) Class.forName(
                        AnalyticalPath.CLASS_PATH[i]).newInstance();
            } catch (Exception e) {
                ThrowExp.throwRxp("SyncGetRecord 公众帐号解析工厂类的包路径错误");
            }

            int msgType = analytical.msgType();
            if (msgType <= 0) {
                ThrowExp.throwRxp("SyncGetRecord 公众帐号解析工厂类没有注册需要接收的msgType");
            }

            if (analys.get(msgType) != null) {
//				ThrowExp.throwRxp("SyncGetRecord 公众帐号解析工厂注册的msgType重合了",
//						"重合的类为：", analys.get(msgType).getClass().getName(),
//						PushClassPath.CLASS_PATH[i]);
            }

            analys.put(msgType, analytical);
            analytical.onInit(context);
        }
    }

    public void start(String publicGUID, int startIndxe) {
        new Thread(new mRunnable(publicGUID, startIndxe)).start();
    }

    public void stop() {
        this.lsn = null;
    }

    private class mRunnable implements Runnable {

        private String publicGUID = null;

        private int startIndex;

        private mRunnable(String publicGUID, int startIndxe) {
            this.publicGUID = publicGUID;
            this.startIndex = startIndxe;
        }

        @Override
        public void run() {
            // 查询数据库中数据
            // 打开数据库
            // SQLiteDatabase db = null;
            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
            // try {
            // db = SQLiteDatabase.openDatabase(BaseUtil.getDBPath(context),
            // null, SQLiteDatabase.OPEN_READWRITE);
            // } catch (Exception e) {
            // throw new RuntimeException("SyncGetRecord 打开数据库出错");
            // }

            final ArrayList<Obj_PublicMsgBase> arrayList;
            try {
                arrayList = get(sqliteHelp.getWritableDatabase(), publicGUID,
                        startIndex);
            } catch (Exception e) {
                return;
            } finally {
                // db.close();
            }

            if (null == arrayList || arrayList.size() == 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != lsn) {
                            lsn.onFail();
                        }
                    }
                });
                return;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != lsn) {
                        lsn.onResult(arrayList);
                    }
                }
            });
        }

        private ArrayList<Obj_PublicMsgBase> get(SQLiteDatabase db,
                                                 String publicGUID, int startIndex) {
            String tableName = Table_PublicAccountRecord
                    .getTableName(publicGUID);

            String cols[] = new String[]{
                    Table_PublicAccountRecord.publicRecord_col_ID,
                    Table_PublicAccountRecord.publicRecord_col_GUID,
                    Table_PublicAccountRecord.publicRecord_col_localeTime,
                    Table_PublicAccountRecord.publicRecord_col_msg,
                    Table_PublicAccountRecord.publicRecord_col_serverTime,
                    Table_PublicAccountRecord.publicRecord_col_msgType,
                    Table_PublicAccountRecord.publicRecord_col_data};

            String queryList[] = SQLite.query(db, tableName, cols, "|", null,
                    null, Table_PublicAccountRecord.publicRecord_col_localeTime
                            + " desc", startIndex + "," + COUNT);

            if (null == queryList || queryList.length == 0) {
                return null;
            }

            ArrayList<Obj_PublicMsgBase> arrayList = new ArrayList<Obj_PublicMsgBase>();
            for (int i = queryList.length - 1; i >= 0; i--) {
                String str[] = queryList[i].split("\\|", -1);
                if (str.length != cols.length) {
                    continue;
                }
                Obj_PublicMsgBase base = null;
                try {
                    String guid = str[1];
                    String localTime = str[2];
                    String msg = str[3];
                    String showTime = str[4];
                    int msgType = Integer.valueOf(str[5]);
                    String urlData = str[6];
                    JSONObject jsonObject = new JSONObject(msg);
                    JSONObject msgInfo = jsonObject.getJSONObject("MSG_INFO");
                    if (urlData.length() > 0) {
                       JSONArray jsonMsg = new JSONArray(urlData);
                       //将URL域以URLMSG字段加入MSG_INFO字段中，这样不用更改onDeal接口，方便统一解析
                        msgInfo.put("URL_MSG",jsonMsg);
                    }
                    base = analys.get(msgType).onDeal(msgInfo);
                    base.MsgType = msgType;
                    base.publicGUID = guid;
                    base.showTime = showTime;
                    base.localTime = Long.parseLong(localTime);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue;
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }

                arrayList.add(base);
            }

            return arrayList;
        }

    }

    private IOnGetRecordLsn lsn = null;

    public void setLsn(IOnGetRecordLsn lsn) {
        this.lsn = lsn;
    }

    interface IOnGetRecordLsn {

        void onResult(ArrayList<Obj_PublicMsgBase> arrayList);

        void onFail();

    }

}
