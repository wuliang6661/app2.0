package synway.module_publicaccount.public_chat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import java.util.ArrayList;
import qyc.library.tool.main_thread.MainThread;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;

/**
 * Created by leo on 2019/1/19.
 */

class SyncGetPublicChatRecord {
    private boolean isStop = false;


    public void start(String publicGUID, String msgID, SQLiteDatabase db, String pushMsgPageCode,String pushMsgPageName){

        if (isStop) {
            return;
        }
        new Thread(new MRunnable(publicGUID,msgID,pushMsgPageCode,pushMsgPageName,db)).start();
    }


    public void stop() {
        isStop = true;
        onGetRecordResult = null;
    }

    private class MRunnable implements Runnable{

        private String publicGUID;
        private String msgID;
        private String pushMsgPageName;
        private String pushMsgPageCode;
        private  SQLiteDatabase db;
        private ArrayList<Obj_PublicMsgBase> resultList;

        public MRunnable(String publicGUID, String msgID,String pushMsgPageCode,String pushMsgPageName, SQLiteDatabase db) {
            this.publicGUID = publicGUID;
            this.msgID = msgID;
            this.db = db;
            this.pushMsgPageCode = pushMsgPageCode;
            this.pushMsgPageName = pushMsgPageName;

        }
        @Override public void run() {
            String sql = "select * from "+ Table_PublicAccountRecord.getTableName(publicGUID)+" where "+
                Table_PublicAccountRecord.publicRecord_col_GUID+" = '"+msgID+"' ";

            Cursor cursor = db.rawQuery(sql, null);
            long msgTime = 0;
            if (cursor.moveToNext()) {
                msgTime = cursor.getLong(
                    cursor.getColumnIndex(Table_PublicAccountRecord.publicRecord_col_localeTime));
            }

            if (msgTime == 0) {
                //说明查不到这条记录出异常了,直接return
                return;
            }
            if (TextUtils.isEmpty(pushMsgPageCode)) {
                //无分类模式
                sql = "select * from " + Table_PublicAccountRecord.getTableName(publicGUID) +
                    " where " + Table_PublicAccountRecord.publicRecord_col_localeTime + " < " + msgTime +
                    " order by " + Table_PublicAccountRecord.publicRecord_col_localeTime + " desc limit 0,10";
            } else {
                //分类模式
                sql = "select * from " + Table_PublicAccountRecord.getTableName(publicGUID) +
                    " where ( " +Table_PublicAccountRecord.publicRecord_col_pushMsgPageCodes+" like '%"+pushMsgPageCode+"%' or " +
                    Table_PublicAccountRecord.publicRecord_col_pushMsgPageNames+" like '%"+pushMsgPageName+"%' )"+
                    " and " + Table_PublicAccountRecord.publicRecord_col_localeTime + " < " + msgTime +
                    " order by " + Table_PublicAccountRecord.publicRecord_col_localeTime + " desc limit 0,10";
            }

            cursor = db.rawQuery(sql, null);
            resultList = new ArrayList<>();

            while (cursor.moveToNext()) {
                Obj_PublicMsgBase objPublicMsgBase = PublicMsgRecord.read(cursor);
                if (objPublicMsgBase != null) {
                    resultList.add(objPublicMsgBase);
                }
            }

            if (cursor != null) {
                cursor.close();
            }

            MainThread.joinMainThread(new MainThread.Runnable_MainThread() {
                @Override
                public void run() {
                    if (onGetRecordResult != null) {
                        onGetRecordResult.onFinish(resultList);
                    }
                }
            });

        }
    }

    public interface OnGetRecordResult{
        void onFinish(ArrayList<Obj_PublicMsgBase> list);
    }
    private OnGetRecordResult onGetRecordResult;

    public void setOnGetRecordResult(OnGetRecordResult onGetRecordResult){
        this.onGetRecordResult = onGetRecordResult;
    }
}
