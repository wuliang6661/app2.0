package synway.module_publicaccount.public_list_new;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by leo on 2018/6/22.
 */

public class OrderConfigRW {

    /**
     * 将数据写入ConfigTable表
     * @param value
     * @param db
     */
    public static void write(String value, SQLiteDatabase db){
        //先删除再插入,这样的做法就可以避免先查询一次,记录是否存在,然后选择插入或者更新操作
        String sql = "delete from ConfigTable  where ConfigCode = 'PUBLICORDER'";
        db.execSQL(sql);
        sql = "insert into ConfigTable (ConfigCode,ConfigName,ConfigValue) VALUES ('PUBLICORDER'," +
            "'SaveOrder','"+value+"');";
        db.execSQL(sql);
    }


    /**
     * 返回存储的顺序,若为空字符串则表明当前还没有存储过
     * @param db
     * @return
     */
    public static String read(SQLiteDatabase db){
        String resultValue = "";
        String sql = "select * from ConfigTable where ConfigCode = 'PUBLICORDER'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            resultValue = cursor.getString(cursor.getColumnIndex("ConfigValue"));
        }
        return resultValue;
    }

    public static void clear(SQLiteDatabase db){
        String sql = "delete from ConfigTable where ConfigCode = 'PUBLICORDER'";
        db.execSQL(sql);
    }
}
