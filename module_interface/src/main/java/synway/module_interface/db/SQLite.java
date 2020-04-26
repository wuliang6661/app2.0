package synway.module_interface.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <p/>
 * 模块编号:1.0
 * <p/>
 * 模块名称:sqlite操作
 * <p/>
 * 功能:对数据库的一系列增删改查
 * <p/>
 * 注意:
 * <p/>
 * 相关模块:DBHelp
 *
 * @author 钱园超
 * @version 创建时间: 2011-7-1
 */
public class SQLite {

    /**
     * 查询一条数据
     */
    public static String queryOneRecord(SQLiteDatabase db, String tableName, String[] columns, String splitStr,
                                        String selection, String[] selectionArgs, String orderBy) {
        Cursor cs = db.query(tableName, columns, selection, selectionArgs, null, null, orderBy);

        String returnstr = null;
        int columnsLength = 0;
        if (columns != null) {
            columnsLength = columns.length;
        }
        if (cs.moveToNext()) {
            returnstr = "";
            String columnsVale = null;
            for (int j = 0; j < columnsLength; j++) {
                columnsVale = cs.getString(cs.getColumnIndex(columns[j]));
                if (columnsVale != null) {
                    returnstr += columnsVale;
                }
                if (j != columnsLength - 1) {
                    returnstr += splitStr;
                }
            }
        }
        cs.close();

        return returnstr;
    }

    /**
     * 查询一条数据
     */
    public static String[] queryOneRecord2(SQLiteDatabase db, String tableName, String[] columns,
                                           String selection, String[] selectionArgs, String orderBy) {
        Cursor cs = db.query(tableName, columns, selection, selectionArgs, null, null, orderBy);

        String[] result = new String[columns.length];
        int columnsLength = 0;
        if (columns != null) {
            columnsLength = columns.length;
        }
        if (cs.moveToNext()) {
            String columnsVale;
            for (int j = 0; j < columnsLength; j++) {
                columnsVale = cs.getString(cs.getColumnIndex(columns[j]));
                result[j] = columnsVale;
            }
        }
        cs.close();
        return result;
    }

    /**
     * <p/>
     * 功能:查询表
     * <p/>
     * 异常:列名错误会导致异常
     *
     * @param tableName     表名
     * @param columns       列名
     * @param splitStr      分割符
     * @param selection
     * @param selectionArgs
     * @param orderBy       排序
     * @param limit         取的条数
     * @return String[] 数组的每一个项都代表一行查询结果,数组的长度就是查询结果的条数
     * 每一行查询结果中,值与值之间以分隔符分隔,顺序和查询的列的顺序匹配.
     * 当某列的值为NULL时,分隔符之间的字符串的值以"null"字符串表示,而不是空字符串
     * 每一项以splitStr解开后,数组长度一定和columns的数组长度相等,顺序一定匹配.
     */
    public static String[] query(SQLiteDatabase db, String tableName, String[] columns, String splitStr,
                                 String selection, String[] selectionArgs, String orderBy, String limit) {
        Cursor cs = db.query(tableName, columns, selection, selectionArgs, null, null, orderBy, limit);

        String[] returnStrs = new String[cs.getCount()];
        int i = 0;
        String returnstr;
        int columnsLength = 0;
        if (columns != null) {
            columnsLength = columns.length;
        }
        while (cs.moveToNext()) {
            returnstr = "";
            String columnsVale = null;
            for (int j = 0; j < columnsLength; j++) {
                columnsVale = cs.getString(cs.getColumnIndex(columns[j]));
                if (columnsVale != null) {
                    returnstr += columnsVale;
                }
                if (j != columnsLength - 1) {
                    returnstr += splitStr;
                }
            }
            returnStrs[i] = returnstr;
            i++;
        }
        cs.close();
        return returnStrs;
    }

    /**
     * <p/>
     * 功能:查询表
     * <p/>
     * 异常:列名错误会导致异常
     *
     * @param tableName     表名
     * @param columns       列名
     * @param splitStr      分割符
     * @param selection
     * @param selectionArgs
     * @param orderBy       排序
     * @return String[] 数组的每一个项都代表一行查询结果,数组的长度就是查询结果的条数
     * 每一行查询结果中,值与值之间以分隔符分隔,顺序和查询的列的顺序匹配.
     * 当某列的值为NULL时,分隔符之间的字符串的值以"null"字符串表示,而不是空字符串
     * 每一项以splitStr解开后,数组长度一定和columns的数组长度相等,顺序一定匹配.
     */
    public static String[] query(SQLiteDatabase db, String tableName, String[] columns, String splitStr,
                                 String selection, String[] selectionArgs, String orderBy) {
        Cursor cs = db.query(tableName, columns, selection, selectionArgs, null, null, orderBy);

        // Cursor cs = db.query(tableName, null, selection, selectionArgs,
        // null, null, null);
        String[] returnStrs = new String[cs.getCount()];
        int i = 0;
        String returnstr;
        int columnsLength = 0;
        if (columns != null) {
            columnsLength = columns.length;
        }
        while (cs.moveToNext()) {
            returnstr = "";
            String columnsVale = null;
            for (int j = 0; j < columnsLength; j++) {
                columnsVale = cs.getString(cs.getColumnIndex(columns[j]));
                if (columnsVale != null) {
                    returnstr += columnsVale;
                }
                if (j != columnsLength - 1) {
                    returnstr += splitStr;
                }
            }
            returnStrs[i] = returnstr;
            i++;
        }
        cs.close();
        return returnStrs;

    }

    /**
     * <p/>
     * 功能:连续多次查询表
     * <p/>
     * 异常:列名错误会导致异常
     *
     * @param tableName     表名
     * @param columns       列名
     * @param splitStr      分割符
     * @param selections    多个条件
     * @param selectionArgs 多个条件
     * @param orderBy       排序
     * @return LinkedList<String[]> 多个查询结果,长度一定和columns的长度一样
     */
    public static ArrayList<String[]> querys(SQLiteDatabase db, String tableName, String[] columns, String splitStr,
                                             String[] selections, ArrayList<String[]> selectionArgs, String orderBy) {
        ArrayList<String[]> LinkedReturnStrs = new ArrayList<String[]>();
        for (int a = 0; a < selections.length; a++) {
            Cursor cs = db.query(tableName, columns, selections[a], selectionArgs.get(a), null, null, orderBy);
            String[] returnStrs = new String[cs.getCount()];
            int i = 0;
            String returnstr;
            int columnsLength = 0;
            if (columns != null) {
                columnsLength = columns.length;
            }
            while (cs.moveToNext()) {
                returnstr = "";
                for (int j = 0; j < columnsLength; j++) {
                    returnstr += cs.getString(cs.getColumnIndex(columns[j]));
                    if (j != columnsLength - 1) {
                        returnstr += splitStr;
                    }
                }
                returnStrs[i] = returnstr;
                i++;
            }
            LinkedReturnStrs.add(returnStrs);
            cs.close();
        }

        return LinkedReturnStrs;
    }

    /**
     * <p/>
     * 功能:;连接查询
     * <p/>
     * 异常:列名错误会导致异常
     *
     * @param tableName     表名
     * @param columns       列名
     * @param splitStr      分割符
     * @param selection
     * @param selectionArgs
     * @param orderBy       排序
     * @return String[]
     * 每一项都代表一行结果,列之间以splitStr分隔,顺序和传入的列顺序匹配.当某列的值为NULL时,字符串的值以"null"
     * 字符串表示,而不是空字符串. 每一项以splitStr解开后,数组长度一定和columns的数组长度相等,顺序一定匹配.
     */
    public static String[] queryLink(SQLiteDatabase db, String tableName, String[] leftJoinTable, String linkStr,
                                     String[] columns, String splitStr, String selection, String[] selectionArgs, String orderBy) {
        if (leftJoinTable.length == 1) {
            tableName += " left join ";
            tableName += leftJoinTable[0];
            tableName += " on ";
            tableName += linkStr;
        } else {
            for (int i = 0; i < leftJoinTable.length; i++) {
                if (i == 0) {
                    tableName += " left join ";
                    tableName += leftJoinTable[i];
                } else if (i == leftJoinTable.length - 1) {
                    tableName += ",";
                    tableName += leftJoinTable[i];
                    tableName += " on ";
                    tableName += linkStr;
                } else {
                    tableName += ",";
                    tableName += leftJoinTable[i];
                }

            }
        }

        Cursor cs = db.query(tableName, columns, selection, selectionArgs, null, null, orderBy);

        String[] returnStrs = new String[cs.getCount()];
        int i = 0;
        String returnstr;
        int columnsLength = 0;
        if (columns != null) {
            columnsLength = columns.length;
        }
        while (cs.moveToNext()) {
            returnstr = "";
            for (int j = 0; j < columnsLength; j++) {
                returnstr += cs.getString(cs.getColumnIndex(columns[j]));
                if (j != columnsLength - 1) {
                    returnstr += splitStr;
                }
            }
            returnStrs[i] = returnstr;
            i++;
        }
        cs.close();

        return returnStrs;
    }

    /**
     * <p/>
     * 功能:插入一条Sql信息
     * <p/>
     * 异常:无异常
     *
     * @param tableName     表名
     * @param noColumnValue 对没有提及的列使用的值
     * @param contentValues 键值对
     * @return boolean
     */
    public static long insert(SQLiteDatabase db, String tableName, String noColumnValue, ContentValues contentValues) {
        long result = db.insert(tableName, noColumnValue, contentValues);


        return result;
    }

    /**
     * 重置表内容
     *
     * @param db
     * @param tableName
     * @param noColumnValue
     * @param contentValues
     */
    public static void reset(SQLiteDatabase db, String tableName, String noColumnValue,
                             ArrayList<ContentValues> contentValues) {
        db.delete(tableName, null, null);
        for (int i = 0; i < contentValues.size(); i++) {
            db.insert(tableName, noColumnValue, contentValues.get(i));
        }
    }

    /**
     * 插入多条Sql信息
     *
     * @param tableName     表名
     * @param contentValues 键值对集合
     */
    public static void inserts(SQLiteDatabase db, String tableName, ArrayList<ContentValues> contentValues) {
        int count = contentValues.size();
        db.beginTransaction();
        for (int i = 0; i < count; i++) {
            db.insert(tableName, null, contentValues.get(i));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * <p/>
     * 功能:插入多条Sql信息
     * <p/>
     * 异常:
     *
     * @param insertTableAndValue key=表名,value=键值对 void
     */
    public static void inserts(SQLiteDatabase db, Hashtable<String, ContentValues> insertTableAndValue) {
        db.beginTransaction();
        for (Enumeration<String> e = insertTableAndValue.keys(); e.hasMoreElements(); ) {
            db.insert(e.toString(), null, insertTableAndValue.get(e.toString()));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * <p/>
     * 功能:更新表中的一条信息
     * <p/>
     * 注意:
     *
     * @param tableName     表名
     * @param cv            新的值对
     * @param selection     查询字段名,如id=? and name=?
     * @param selectionArgs 查询字段值,如new String[]{"1","Castiel"}
     */
    public static boolean update(SQLiteDatabase db, String tableName, ContentValues cv, String selection,
                                 String[] selectionArgs) {
        long result = 0;
        result = db.update(tableName, cv, selection, selectionArgs);

        return result > 0;
    }

    /**
     * <p/>
     * 功能:更新多条表中的信息
     * <p/>
     * 异常:无异常
     *
     * @param insertTableAndValue key=表名,value=键值对
     * @param selection           查询字段名,如id=? and name=?
     * @param selectionArgs       查询字段值,如new String[]{"1","Castiel"} void
     */
    public static void updates(SQLiteDatabase db, Hashtable<String, ContentValues> insertTableAndValue,
                               String selection, String[] selectionArgs) {
        db.beginTransaction();
        for (Enumeration<String> e = insertTableAndValue.keys(); e.hasMoreElements(); ) {
            db.update(e.toString(), insertTableAndValue.get(e.toString()), selection, selectionArgs);

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * <p/>
     * 功能:删除
     * <p/>
     * 注意:
     *
     * @param tableName     表名
     * @param selection     查询字段名,如id=? and name=?
     * @param selectionArgs 查询字段值,如new String[]{"1","Castiel"}
     */
    public static void del(SQLiteDatabase db, String tableName, String selection, String[] selectionArgs) {
        db.delete(tableName, selection, selectionArgs);

    }

}
