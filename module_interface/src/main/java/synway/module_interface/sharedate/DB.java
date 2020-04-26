package synway.module_interface.sharedate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 存储数据的数据库
 * Created by 钱园超 on 2017/9/4.
 */

class DB extends SQLiteOpenHelper {

    private Context context;
    private static final int DBVERSION = 1;

    DB(Context context) {
        super(context, "CrossProcessShareData", null, DBVERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        update(db, 0, DBVERSION);//新建库从0开始升到最新版
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //newVersion就是构造函数里传进去的数据库版本.
        update(db, oldVersion, oldVersion);//旧库从它当前版本升级到最新版
    }

    private static void update(SQLiteDatabase db, int oldversion, int newVersion) {
        if (oldversion == 0) {
            //升到1
            String sql = "Create TABLE BaseTable" +
                    "([name] varchar NOT NULL," +
                    "[key] varchar NOT NULL," +
                    "[value] nvarchar," +
                    " CONSTRAINT [name_key] UNIQUE([name],[key]))";
            db.execSQL(sql);
        }
        db.setVersion(newVersion);
    }
}
