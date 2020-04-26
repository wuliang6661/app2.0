package synway.module_publicaccount.public_favorite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.Main;

/**
 * Created by quintet on 2016/10/15.
 *
 * 获取全部公众号的数据,目的是判断是否有公众号。
 */
public class SynGetPublicInfoByDB {

    private Handler handler = null;

    public SynGetPublicInfoByDB() {
        handler = new Handler();
    }


    public void start() {
        new Thread(new MRunnable()).start();
    }


    public void stop() {
        if (listener != null) {
            listener = null;
        }
    }


    private class MRunnable implements Runnable {

        @Override public void run() {
            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
            SQLiteDatabase db = sqliteHelp.getWritableDatabase();

            try {
                db.beginTransaction();
                String tableName = Table_PublicAccount._TABLE_NAME;
                String sql = "select * from "+tableName+";";
                Cursor cursor = db.rawQuery(sql, null);
                int count = cursor.getCount();

                if (count == 0) {
                    handler.post(new Runnable() {
                        @Override public void run() {
                            if (listener != null) {
                                listener.onResult(false);
                            }

                        }
                    });

                } else {
                    handler.post(new Runnable() {
                        @Override public void run() {
                            if (listener != null) {
                                listener.onResult(true);
                            }

                        }
                    });

                }
                cursor.close();
                db.setTransactionSuccessful();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                db.endTransaction();
            }
        }
    }




    public interface OnSynGetPublicInfoByDBListener {
        void onResult(boolean isRecordExists);
    }

    private OnSynGetPublicInfoByDBListener listener = null;


    public void setOnSynGetPublicInfoByDBListener(OnSynGetPublicInfoByDBListener listener) {
        this.listener = listener;
    }

}
