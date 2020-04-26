package synway.module_publicaccount.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import synway.module_interface.db.SQLite;
import synway.module_publicaccount.db.table_util.Table_PublicAccountFocusedList;

/**
 * Created by QSJH on 2016/5/13 0013.
 */
public class PublicAccountFocusedList {

    /**
     * 已存在返回true,不存在返回false
     * @param db
     * @param publicAccountID
     * @return
     */
    public static boolean exist(SQLiteDatabase db,String publicAccountID){
        String[] idArray = getFocusedIDList(db);
        for(String s:idArray){
            if(s.equals(publicAccountID)){
                return true;
            }
        }
        return false;
    }


    /**
     * 向数据库中添加一个受关注的公众号
     */
    public static void addToDB(SQLiteDatabase db, String publicAccountID) {
        ContentValues cv = new ContentValues();
        cv.put(Table_PublicAccountFocusedList.COL_GUID, publicAccountID);
        SQLite.insert(db, Table_PublicAccountFocusedList.TABLE_NAME, null, cv);
    }

    /**
     * 从数据库中删除一个被关注的公众号
     */
    public static void removeFromDB(SQLiteDatabase db, String publicAccountID) {
        String sql = "delete from "+Table_PublicAccountFocusedList.TABLE_NAME + " where "+ Table_PublicAccountFocusedList.COL_GUID + " = '"+publicAccountID+"'";
        db.execSQL(sql);
//        SQLite.del(db, Table_PublicAccountFocusedList.TABLE_NAME, Table_PublicAccountFocusedList.COL_GUID + "=?", new String[]{publicAccountID});
    }

    /**
     * 读取所有已被关注的公众号ID，如果没有则返回长度为0的数据而不是NULL
     */
    public static String[] getFocusedIDList(SQLiteDatabase db) {
        String[] idList;
        idList = SQLite.query(db, Table_PublicAccountFocusedList.TABLE_NAME, new String[]{Table_PublicAccountFocusedList.COL_GUID}, "|", null, null, null);
        return idList;
    }

    public static ArrayList<String> getFocusedIDArrayList(SQLiteDatabase db){
        String[] list = getFocusedIDList(db);
        ArrayList<String> listlist = new ArrayList<>();
        for(String s:list)
        {
            listlist.add(s);
        }
        return listlist;
    }
}
