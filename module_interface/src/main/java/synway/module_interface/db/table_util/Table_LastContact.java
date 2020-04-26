package synway.module_interface.db.table_util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.SQLite;
import synway.module_interface.db.TableUpdate;

/**
 * 最近联系人的表结构及建表语句
 *
 * @author 钱园超 [2015年7月28日 下午2:54:04]
 * @author 钱园超 2015年9月29日下午5:00:23 加入自动更新接口
 */
public class Table_LastContact implements TableUpdate {

    public static final String _TABLE_NAME = "LastContact";

    /**
     * ID, integer,PK,自增
     */
    public static final String ID = "ID";

    /**
     * 最近联系人类型，tinyint，0=单聊，1=群聊，2=公众
     */
    public static final String TARGET_TYPE = "TargetType";

    /**
     * 目标Id，varchar(40)
     */
    public static final String TARGET_ID = "TargetId";

    /**
     * 目标Id，varchar(40)
     */
    public static final String TARGET_NAME = "TargetName";

    /**
     * 最近一次通讯的本机时间,仅用于排序
     */
    public static final String LAST_MSG_LOCALE_TIME = "LastMsgLocaleTime";

    /**
     * 最近一次通讯的服务器时间,作为标准时间
     */
    public static final String LAST_MSG_SHOW_TIME = "LastMsgShowTime";

    /**
     * 最近一次通讯的内容， varchar
     */
    public static final String LAST_MSG_CONTENT = "LastMsgContent";

    /**
     * 未读消息数量
     */
    public static final String UN_READ_COUNT = "UnReadCount";

    /**
     * 发送状态 默认为0 -1表示发送失败 0表示正在发送 1发送成功 默认是1 接收的消息全部为1
     */
    public static final String SEND_STATE = "SendState";

    /**
     * 0表示不置顶 >0表示置顶,其中越大越靠顶
     */
    public static final String TOP_INDEX = "TopIndex";

    /**
     * 消息发送者的ID
     */
    public static final String FROM_USER_ID = "FromUserID";

    /**
     * 消息发送者的名字
     */
    public static final String FROM_USER_NAME = "FromUserName";

    public static final String getCreatTableSql() {
        return "CREATE TABLE " + Table_LastContact._TABLE_NAME + " ( " + Table_LastContact.ID
                + " integer PRIMARY KEY AUTOINCREMENT," + Table_LastContact.TARGET_ID + " varchar(40) UNIQUE,"
                + Table_LastContact.TARGET_NAME + " varchar(40)," + Table_LastContact.TARGET_TYPE + " tinyint,"
                + Table_LastContact.UN_READ_COUNT + " integer default 0," + Table_LastContact.LAST_MSG_LOCALE_TIME
                + " dateTime NOT NULL," + Table_LastContact.LAST_MSG_SHOW_TIME + " dateTime NOT NULL,"
                + Table_LastContact.LAST_MSG_CONTENT + " varchar," + Table_LastContact.SEND_STATE
                + " integer default 1," + Table_LastContact.TOP_INDEX + " integer default 0" + " ) ";
    }

    @Override
    public String[] update(int verson, SQLiteDatabase db) {
        if (verson == 1) {
            return new String[]{getCreatTableSql()};
        } else if (verson == 2) {
            return new String[]{v2_1, v2_2};
        } else if (verson == 3) {
            // ....
        } else if (verson == 24) {
            verson24(db);
        }
        return null;
    }

    public static final String v2_1 = "ALTER  TABLE  LastContact  ADD COLUMN  " + FROM_USER_ID + " varchar";
    public static final String v2_2 = "ALTER  TABLE  LastContact  ADD COLUMN  " + FROM_USER_NAME + " varchar ";

    //新版最近联系人最后一条数据更新
    private void verson24(SQLiteDatabase db) {

        String sql = "select * from " + Table_LastContact._TABLE_NAME + " order by " + Table_LastContact.TOP_INDEX + " DESC , " + Table_LastContact.LAST_MSG_LOCALE_TIME + " DESC ";
        Cursor cursor = db.rawQuery(sql, null);

        // 我的ID，用来判断最后一条消息是否需要人名的头;因为取不到，所以不管自己发的还是别人发的消息，统一
//		String myUserID = Sps_RWLoginUser.readUserID(context);
        while (cursor.moveToNext()) {

            String targetID = cursor.getString(cursor.getColumnIndex(Table_LastContact.TARGET_ID));
            int type = cursor.getInt(cursor.getColumnIndex(Table_LastContact.TARGET_TYPE));
            String content;
            if (type == 2) {
                content = ParseMsg.parseMsg2(cursor.getString(cursor.getColumnIndex(Table_LastContact.LAST_MSG_CONTENT)));
            } else {
                content = ParseMsg.parseMsg(targetID, cursor.getString(cursor.getColumnIndex(Table_LastContact.FROM_USER_ID)), cursor.getString(cursor.getColumnIndex(Table_LastContact.FROM_USER_NAME)), type,
                        cursor.getString(cursor.getColumnIndex(Table_LastContact.LAST_MSG_CONTENT)));
            }
            //替换原来的
            ContentValues cv2 = new ContentValues();
            cv2.put(Table_LastContact.LAST_MSG_CONTENT, content);

            String cols[] = new String[]{Table_LastContact.ID,
                    Table_LastContact.TARGET_ID, Table_LastContact.TARGET_NAME};

            String strs = SQLite.queryOneRecord(db, Table_LastContact._TABLE_NAME, cols,
                    "|", Table_LastContact.TARGET_ID + "=?", new String[]{targetID}, null);
            //已有记录，修改操作
            if (strs != null) {
                String selectionArgs[] = new String[]{targetID};
                SQLite.update(db, Table_LastContact._TABLE_NAME, cv2,
                        Table_LastContact.TARGET_ID + "=?", selectionArgs);

            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }
    @Override
    public void delete(SQLiteDatabase db) {
        String deleteSql = "drop table " + _TABLE_NAME;
        db.execSQL(deleteSql);
    }
}
