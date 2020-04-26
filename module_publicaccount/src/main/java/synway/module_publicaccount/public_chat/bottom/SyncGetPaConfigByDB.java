package synway.module_publicaccount.public_chat.bottom;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import java.util.ArrayList;

import synway.module_interface.db.SQLite;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.db.table_util.Table_PublicConfigMsg;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_chat.Obj_PaConfigMsg;

import static synway.module_publicaccount.until.ConfigUtil.PUB_CJPT_GUID;

/**
 * Created by huangxi
 * DATE :2018/12/3
 * Description ：
 */

public class SyncGetPaConfigByDB {

    private Handler handler = null;
    public SyncGetPaConfigByDB.IOnGetPaConfigByDB lsn = null;
    private Obj_PaConfigMsg objPaConfigMsg = null;
    private Context context;
    public SyncGetPaConfigByDB(Context context) {
        this.context=context;
        handler = new Handler();
    }

    public void start(String ID) {
        new Thread(new SyncGetPaConfigByDB.mRunnbale(ID)).start();
    }

    public void stop() {
        this.lsn = null;
    }

    private class mRunnbale implements Runnable {

        private String ID = null;

        private mRunnbale(String ID) {
            this.ID = ID;
        }

        @Override
        public void run() {
                String tableName = Table_PublicConfigMsg._TABLE_NAME;
            String columns[] = new String[] { Table_PublicConfigMsg.PAC_ID,
                    Table_PublicConfigMsg.PAC_SourceUrl};

            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();

            String strs[];
            try {
                strs = SQLite.query(sqliteHelp.getWritableDatabase(),
                        tableName, columns, "|", null, null,
                        Table_PublicConfigMsg.PAC_ID + " desc ");
            } catch (Exception e) {
                // MLog.Log("qsjh", "!");
                String s=e.toString();
                return;
            } finally {
                // db.close();
            }

            for (int i = 0; i < strs.length; i++) {
                String sList[] = strs[i].split("\\|", -1);
                Obj_PaConfigMsg obj_paConfigMsg = new Obj_PaConfigMsg();
                obj_paConfigMsg.ID = sList[0];
                obj_paConfigMsg.sourceUrl = sList[1];
                if(obj_paConfigMsg.ID.equals(ID)){
                    objPaConfigMsg=obj_paConfigMsg;
                }
            }

            if (objPaConfigMsg ==null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != lsn) {
                            lsn.onFail("", "^u", " resultList.size() == 0 ");
                        }
                    }
                });
                return;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != lsn) {
                        lsn.onResult(objPaConfigMsg);
                    }
                }
            });

        }

    }

    public void setLsn(SyncGetPaConfigByDB.IOnGetPaConfigByDB lsn) {
        this.lsn = lsn;
    }

    public interface IOnGetPaConfigByDB {
        void onResult(Obj_PaConfigMsg objPaConfigMsg);

        void onFail(String title, String reason, String detail);
    }
}

