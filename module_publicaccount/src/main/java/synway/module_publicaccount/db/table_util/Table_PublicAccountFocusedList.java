package synway.module_publicaccount.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

/**
 * Created by QSJH on 2016/5/12 0012.
 */
public class Table_PublicAccountFocusedList implements TableUpdate {

    /**
     * 表名
     */
    public static final String TABLE_NAME = "PublicAccountFocusedList";

    /**
     * 主键，公众号ID
     */
    public static final String COL_GUID = "GUID";

    /**
     * 创建公众号聊天记录表， 若不存在会创建
     */
    public static final String getCreatTableSql() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + " ( "
                + COL_GUID + " varchar(50) PRIMARY KEY"
                + " ) ";
    }


    @Override
    public String[] update(int verson, SQLiteDatabase db) {
        if (verson == 14) {
            return new String[]{getCreatTableSql()};
        }
        return null;
    }
    @Override
    public void delete(SQLiteDatabase db) {
        String deleteSql = "drop table " + TABLE_NAME;
        db.execSQL(deleteSql);
    }
}
