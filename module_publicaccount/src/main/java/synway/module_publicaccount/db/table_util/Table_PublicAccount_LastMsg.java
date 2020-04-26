package synway.module_publicaccount.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

/**
 * Created by huangxi
 * DATE :2019/1/17
 * Description ：公众号消息表
 */

public class Table_PublicAccount_LastMsg implements TableUpdate {
     //表名
    public static final String _TABLE_NAME = "PublicAccount_LastMsg";
    /** 公众号ID,  */
    public static final String PUBLIC_ACCOUNT_ID = "ID";

    /** 公众号名称,  */
    public static final String PUBLIC_ACCOUNT_NAME = "PaName";
    /**
     * 最近一次通讯的本机时间,仅用于排序
     */
    public static final String PUBLIC_ACCOUNT_LOCALE_TIME = "PaMsgLocaleTime";

    /**
     * 最近一次通讯的服务器时间,作为标准时间
     */
    public static final String PUBLIC_ACCOUNT_SHOW_TIME = "PaMsgShowTime";

    /**
     * 未读消息数量
     */
    public static final String PUBLIC_ACCOUNT_UN_READ_COUNT = "UnReadCount";
    /**
     * 0表示不置顶 >0表示置顶,其中越大越靠顶
     */
    public static final String PUBLIC_ACCOUNT_TOP_INDEX = "TopIndex";
    public static final String PUBLIC_ACCOUNT_BRIEF_MSG = "BriefMsg";
    public static final String version52 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PUBLIC_ACCOUNT_BRIEF_MSG + " varchar(40)" ;

    public static final String getCreatTableSql() {
        return "CREATE TABLE IF NOT EXISTS " + _TABLE_NAME + " ( " + PUBLIC_ACCOUNT_ID
                + " varchar(40) PRIMARY KEY ,"
                +  PUBLIC_ACCOUNT_NAME + "  varchar(40) ,"
                +PUBLIC_ACCOUNT_LOCALE_TIME+" dateTime NOT NULL ,"
                +PUBLIC_ACCOUNT_SHOW_TIME+" dateTime NOT NULL ,"
                +PUBLIC_ACCOUNT_UN_READ_COUNT+" integer default 0 ,"
                +PUBLIC_ACCOUNT_TOP_INDEX+" integer default 0"
                + " ) ";
    }

    @Override
    public String[] update(int verison, SQLiteDatabase db) {
        if(verison==50){
            return new String[]{getCreatTableSql()};
        }else if(verison ==52){
            return new String[]{version52};
        }
        return null;
    }

    @Override
    public void delete(SQLiteDatabase db) {
        String deleteSql = "drop table " + _TABLE_NAME;
        db.execSQL(deleteSql);
    }
}
