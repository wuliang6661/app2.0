package synway.module_publicaccount.public_message;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;

import synway.module_interface.db.SQLite;
import synway.module_interface.palastmsginterface.HandlerPaLastMsgInterface;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_LastMsg;

/**
 * Created by huangxi
 * DATE :2019/1/18
 * Description ：
 */

public class PublicMessage extends HandlerPaLastMsgInterface {
    /**
     * 插入或更新公众号
     */
    public static final String ACTION_UPDATE_PUBLIC_ACCOUNT = "action.updata.public.account";
    public static final String ACTION_DELETE_PUBLIC_ACCOUNT = "action.delete.public.account";
    public static final String ACTION_CLEAR_PUBLIC_ACCOUNT = "action.clear.public.account";
    public static final String ACTION_UPDATE_PUBLIC_ACCOUNT_UNREAD = "action.updata.public.account.unread";
    /** 公众号ID,  */
    public static final String PUBLIC_ACCOUNT_ID = "modulepushUtil.PublicMessage.ID";

    /** 公众号名称,  */
    public static final String PUBLIC_ACCOUNT_NAME = "modulepushUtil.PublicMessage.PaName";
    /**
     * 最近一次通讯的本机时间,仅用于排序
     */
    public static final String PUBLIC_ACCOUNT_LOCALE_TIME = "modulepushUtil.PublicMessage.PaMsgLocaleTime";

    /**
     * 最近一次通讯的服务器时间,作为标准时间
     */
    public static final String PUBLIC_ACCOUNT_SHOW_TIME = "modulepushUtil.PublicMessage.PaMsgShowTime";

    /**
     * 未读消息数量
     */
    public static final String PUBLIC_ACCOUNT_UN_READ_COUNT = "modulepushUtil.PublicMessage.UnReadCount";
    public static final String PUBLIC_ACCOUNT_BRIEF_MSG = "modulepushUtil.PublicMessage.BriefMsg";


    public static final String ACTION_UNREAD_COUNT_MINUS = "modulepushUtil.unread.count.minus";

    /**
     * 清除未读数
     */
    public static final void clearUnReadCount(Context context, SQLiteDatabase db, String paID) {
        ContentValues cv = new ContentValues();
        cv.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT, 0);// 消息
        SQLite.update(db, Table_PublicAccount_LastMsg._TABLE_NAME, cv, Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?", new String[]{paID});
        // 再发送广播
        Intent intent = new Intent(ACTION_CLEAR_PUBLIC_ACCOUNT);
        intent.putExtra(PUBLIC_ACCOUNT_ID, paID);
        context.sendBroadcast(intent);
    }

    /**
     * @param context
     * @param db
     * @param paID        目标ID
     * @param paName      目标名称
     * @param localTime   消息本地时间
     * @param serverTime  消息服务器时间
     * @param unReadCount 消息未读数
     */
    //插入或更新公众号消息
    public static final void insertOrUpdate(Context context, SQLiteDatabase db,
                                            String paID, String paName,
                                            long localTime,
                                            long serverTime, int unReadCount,String briefMsg) {

        ContentValues cv2 = new ContentValues();
        cv2.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID, paID);
        cv2.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_NAME, paName);
        cv2.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_LOCALE_TIME, localTime);
        cv2.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_SHOW_TIME, serverTime);
        cv2.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT, unReadCount);
        cv2.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_BRIEF_MSG, briefMsg);
        String cols[] = new String[]{Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID};

        String strs = SQLite.queryOneRecord(db, Table_PublicAccount_LastMsg._TABLE_NAME, cols,
                "|", Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?", new String[]{paID}, null);
        //已有记录，修改操作
        if (strs != null) {
            String selectionArgs[] = new String[]{paID};
            SQLite.update(db, Table_PublicAccount_LastMsg._TABLE_NAME, cv2,
                    Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?", selectionArgs);

        } else {
            // [修改/新增数据库里的公众号消息]
            SQLite.insert(db, Table_PublicAccount_LastMsg._TABLE_NAME, null, cv2);
        }

        // 发送广播到 应用消息查看
        Intent sendpaMsg = new Intent();
        sendpaMsg.setAction(ACTION_UPDATE_PUBLIC_ACCOUNT);
        sendpaMsg.putExtra(PUBLIC_ACCOUNT_ID, paID);
        sendpaMsg.putExtra(PUBLIC_ACCOUNT_NAME, paName);
        sendpaMsg.putExtra(PUBLIC_ACCOUNT_LOCALE_TIME, localTime);
        sendpaMsg.putExtra(PUBLIC_ACCOUNT_SHOW_TIME, serverTime);
        sendpaMsg.putExtra(PUBLIC_ACCOUNT_BRIEF_MSG, briefMsg);

        // 消息未读数
        // 设置公众号的未读数
        sendpaMsg.putExtra(PUBLIC_ACCOUNT_UN_READ_COUNT, unReadCount);
        context.sendBroadcast(sendpaMsg);
    }

    //删除消息表中公众号
    public static final void delete(final Context context,
                                    SQLiteDatabase db, final String paID) {

        db.delete(Table_PublicAccount_LastMsg._TABLE_NAME, Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?",
                new String[]{paID});

        // 告知界面删除对应项
        Intent intent = new Intent();
        intent.setAction(ACTION_DELETE_PUBLIC_ACCOUNT);
        intent.putExtra(PUBLIC_ACCOUNT_ID, paID);
        context.sendBroadcast(intent);

    }
//数据库中读取公众号消息表
    public static final void get(Context context, ArrayList<ObjPaMessage> paMessageArrayList) {
//读取置顶的公众号
        SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
        String sql = "select * from " + Table_PublicAccount_LastMsg._TABLE_NAME + " WHERE " + Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX + ">0 " + " order by " + Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX + " ASC , " + Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_LOCALE_TIME + " DESC ";
        Cursor cursor = sqliteHelp.getWritableDatabase().rawQuery(sql, null);

        while (cursor.moveToNext()) {
            ObjPaMessage objPaMessage = new ObjPaMessage();

            long time = Long.parseLong(cursor.getString(cursor.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_SHOW_TIME)));
            objPaMessage.showTime = time;
            objPaMessage.id = cursor.getString(cursor.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID));
            if (objPaMessage.id == null || objPaMessage.id.equals("")) {
                continue;
            }
            objPaMessage.name = cursor.getString(cursor.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_NAME));
            if (TextUtils.isEmpty(objPaMessage.name)) {
                continue;
            }
            objPaMessage.topIndex = cursor.getInt(cursor.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX));
            objPaMessage.unReadCount = cursor.getInt(cursor.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT));
            objPaMessage.briefMsg = cursor.getString(cursor.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_BRIEF_MSG));

            paMessageArrayList.add(objPaMessage);
        }
        //读取未置顶的公众号
        String sql2 = "select * from " + Table_PublicAccount_LastMsg._TABLE_NAME + " WHERE " + Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX + "=0 " + " order by " + Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX + " ASC , " + Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_LOCALE_TIME + " DESC ";
        Cursor cursor2 = sqliteHelp.getWritableDatabase().rawQuery(sql2, null);
        while (cursor2.moveToNext()) {
            ObjPaMessage objPaMessage = new ObjPaMessage();

            long time = Long.parseLong(cursor2.getString(cursor2.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_SHOW_TIME)));
            objPaMessage.showTime = time;
            objPaMessage.id = cursor2.getString(cursor2.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID));
            if (objPaMessage.id == null || objPaMessage.id.equals("")) {
                continue;
            }
            objPaMessage.name = cursor2.getString(cursor2.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_NAME));
            if (TextUtils.isEmpty(objPaMessage.name)) {
                continue;
            }
            objPaMessage.topIndex = cursor2.getInt(cursor2.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX));
            objPaMessage.unReadCount = cursor2.getInt(cursor2.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT));
            objPaMessage.briefMsg = cursor2.getString(cursor.getColumnIndex(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_BRIEF_MSG));
            paMessageArrayList.add(objPaMessage);
        }
        if (cursor2 != null) {
            cursor2.close();
        }
    }

    //获取对应公众号ID的消息未读数
    public static final int getUnReadCount(SQLiteDatabase db, String paId) {

        String selectnList[] = {
                Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT};

        String dbResult = SQLite.queryOneRecord(
                db, Table_PublicAccount_LastMsg._TABLE_NAME, selectnList, "|",
                Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?", new String[]{paId}, null);

        if (null == dbResult) {
            return 0;
        }

        try {
            int result = Integer.valueOf(dbResult);
            if (result <= 0) {
                result = 0;
            }
            return result;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
    //获取所有公众号未读数
    public static final int getAllUnReadCount(SQLiteDatabase db) {

        String selectnList[] = {
                Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT};
    String[]dbResult =SQLite.query(db,Table_PublicAccount_LastMsg._TABLE_NAME,selectnList,"|",null,null,null);

        if (null == dbResult) {
            return 0;
        }
        int result=0;
        try {
            for(int i=0;i<dbResult.length;i++){
                int a= Integer.valueOf(dbResult[i]);
                if (a <= 0) {
                    a = 0;
                }
                result=result+a;
            }
            return result;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //删除公众号消息列表中该公众号
    public static final int delete(SQLiteDatabase db, String paID) {
        return db.delete(Table_PublicAccount_LastMsg._TABLE_NAME, Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?",
                new String[]{paID});
    }

    @Override
    public int deletePaLastMsg(Context context,SQLiteDatabase db, long limitTime) {
        return deletePaMsg(context,db,limitTime);
    }

    /**
     * 删除公众号某个时间前的所有简略消息
     * @param db
     * @param limitTime
     * @return
     */
    public static final  int deletePaMsg(final Context context,SQLiteDatabase db,long limitTime ){
        int s=0;
        s=db.delete(Table_PublicAccount_LastMsg._TABLE_NAME,
                Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_LOCALE_TIME + "<=?",
                new String[]{"" + limitTime});
        Intent intent = new Intent(ACTION_CLEAR_PUBLIC_ACCOUNT);
        context.sendBroadcast(intent);
        return s;
    }


}
