package synway.module_interface.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

/**
 * 规范用语表
 * Created by 13itch on 2016/7/13.
 */
public class Table_RTGISPoints implements TableUpdate {
    /** 表名 */
    public static final String table_name = "RTGISPoint";

    public static final String public_guid = "PublicGUID";
    public static final String time = "Time";
    public static final String msg_info = "MsgInfo";
//    public static final String target_id = "TargetID";
//    public static final String target_name = "Name";
//    public static final String x = "X";
//    public static final String y = "Y";
//    public static final String tip = "Tip";



    public static String Version15() {
        String sql = "CREATE TABLE RTGISPoint (_id INTEGER PRIMARY KEY AUTOINCREMENT, PublicGUID VARCHAR, MsgInfo VARCHAR,Time INTEGER )";
        return sql;
    }

    @Override
    public String[] update(int verson, SQLiteDatabase db) {
        if (verson == 15) {
            return new String[] { Version15()};
        }
        return null;
    }
    @Override
    public void delete(SQLiteDatabase db) {
        String deleteSql = "drop table " + table_name;
        db.execSQL(deleteSql);
    }
}
