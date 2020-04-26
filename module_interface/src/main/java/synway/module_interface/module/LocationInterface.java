package synway.module_interface.module;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import synway.module_interface.config.netConfig.NetConfig;

/**
 * Created by zjw on 2017/11/30.
 * 位置数据共享接口
 */

public abstract class LocationInterface {
//    //位置数据类
//    public int type;
//    //类型的名字
//    public String typeName;

    //    //这种类型的所有数据
    public ArrayList<LoacationObj> getList(SQLiteDatabase sqLiteDatabase, Context context, NetConfig netConfig) {
        return null;
    }
    //位置数据类型
    public int getType(){
        return 0;
    }
    //类型的名字
    public String getTypeName(){
        return "";
    }
}
