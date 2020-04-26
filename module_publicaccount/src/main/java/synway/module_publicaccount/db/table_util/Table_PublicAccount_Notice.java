package synway.module_publicaccount.db.table_util;

import android.database.sqlite.SQLiteDatabase;
import synway.module_interface.db.TableUpdate;

/**
 * Created by leo on 2018/10/22.
 */

public class Table_PublicAccount_Notice implements TableUpdate {

    //表名
    public static final String _TABLE_NAME = "PublicAccount_Notice";

    /** ID, integer,PK,自增 */
    public static final String PUBLIC_NOTICE_COL_ID = "ID";

    /** Notice_ID 每条通知的唯一ID 由手机端自己生成，方便于查询*/
    public static final String PUBLIC_NOTICE_COL_MSG_ID = "Notice_ID";

    /** 公众功能ID */
    public static final String PUBLIC_NOTICE_COL_PUBLIC_GUID = "Public_GUID";

    /** 消息类型，固定为 7 通知类型 */
    public static final String PUBLIC_NOTICE_COL_MSGTYPE = "MsgType";

    public static final String PUBLIC_NOTICE_COL_TOUSER = "ToUser";

    /** json 消息内容,指的是MSG_INFO 所有包含的内容  */
    public static final String PUBLIC_NOTICE_COL_MSG = "Msg";

    /** 消息时间 (服务器返回), 用于显示*/
    public static final String PUBLIC_NOTICE_COL_SERVERTIME = "ServiceTime";

    /** 消息时间(本地时间) */
    public static final String PUBLIC_NOTICE_COL_LOCALETIME = "LocalTime";

    /** 通知类型的区分，其中比较特殊的是待办类消息 默认为0，待办类为1 后续可能增加 */
    public static final String PUBLIC_NOTICE_COL_PAGE_TYPE = "PageType";


    /**
     * 创建公众号通知表， 若不存在会创建
     */
    public static final String getCreatTableSql() {
        return "CREATE TABLE IF NOT EXISTS " + _TABLE_NAME
            + " ( "
            + PUBLIC_NOTICE_COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + PUBLIC_NOTICE_COL_PUBLIC_GUID + " varchar(50),"
            + PUBLIC_NOTICE_COL_MSG_ID + " varchar(100),"
            + PUBLIC_NOTICE_COL_PAGE_TYPE + " int DEFAULT 0,"
            + PUBLIC_NOTICE_COL_MSG + " text,"
            + PUBLIC_NOTICE_COL_TOUSER + " varchar(100),"
            + PUBLIC_NOTICE_COL_SERVERTIME + " varchar(100),"
            + PUBLIC_NOTICE_COL_LOCALETIME + " datetime,"
            + PUBLIC_NOTICE_COL_MSGTYPE + " int DEFAULT 7"
            + " ) ";
    }

    @Override public String[] update(int verson, SQLiteDatabase db) {
        if(verson == 48){
            return new String[]{getCreatTableSql()};
        }

        return null;
    }


    @Override public void delete(SQLiteDatabase db) {
        String deleteSql = "drop table " + _TABLE_NAME;
        db.execSQL(deleteSql);
    }
}
