package synway.module_publicaccount.customconfig;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;

import synway.module_interface.config.CustomConfigType;
import synway.module_publicaccount.Main;

/**
 *
 * Created by qyc on 2016/9/27.
 */

class CustomConfigRead {

        public static ArrayList<HashMap<String,String>> read(CustomConfigType customConfigType, String keys[], OnResultListen onResultListen) {
        //其实上传呢是不需要自定义配置的,但是强制加入了自定义配置枚举,并验证传入的key是否和枚举定义好的key相等.是因为这样的目的:
        //防止程序员不小心(太粗心)修改了自己的key,又忘记了在登陆同步数据的地方也一起修改.导致系统维护一段时间后,登录同步数据的功能各种配置残缺不全.
        if (keys.length==0) {
            throw new RuntimeException("根本就没有配置项!");
        }
            for(int i=0;i<keys.length;i++){

                if(!isStrInStrs(keys[i],customConfigType.keys())) {
                    throw new RuntimeException("新加的配置项忘记在CustomConfigType里定义了");
                }
            }


        return readsql(customConfigType.name(),keys, onResultListen);
    }

    private static  ArrayList<HashMap<String,String>> readsql(final String name, final String[] keys, final OnResultListen onResultListen) {
                String data[];
                //从本地数据库获取
                ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
                for (int i = 0; i < keys.length; i++) {
                    Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().rawQuery("select ConfigValue from ConfigTable  where ConfigCode="+"'"+name+"' and ConfigName='"+keys[i]+"'",null);
//                   Log.i("testy","select ConfigValue from ConfigTable  where ConfigCode="+"'"+name+"' and ConfigName='"+keys[i]+"'");
                    while (cursor.moveToNext()) {
                        HashMap<String,String>  hashMap=new HashMap<String,String>();
                        hashMap.put(keys[i],cursor.getString(0));
                        list.add(hashMap);
                    }
                }

                return list;
            }




    interface OnResultListen {
        void onResult(String error);
    }
    private static boolean isStrInStrs(String str, String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            if (str.equals(strs[i])) {
                return true;
            }
        }
        return false;
    }
}
