package synway.module_publicaccount.customconfig;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import qyc.library.tool.http.HttpPost;
import synway.common.ThreadPool;
import synway.module_interface.config.CustomConfigType;
import synway.module_publicaccount.Main;

/**
 * 作用域:提供name-keys-values这个结构的键值对的 保存 的方法
 * 输入:服务器IP,userID,配置类型,配置key-value  输出:两种方式输出保存结果. Future方式和回调方式.(Java1.8以后可以将回调接口也整合到Future中,但1.7的Future接口还无法添加回调接口,没有addListener方法)
 * 生命周期:无
 * Created by qyc on 2016/9/27.
 */
class CustomConfigSave {


    //不要public,由同包下的CustomConfigRW类统一整合

    private static final String url(String ip, int port) {
        return "http://" + ip + ":" + port + "/OSCService/BasicData/ConfigUpLoad.osc";
    }
    /**
     * 将配置上传并保存
     *
     * @param httpIP           服务器IP
     * @param httpPort         服务器端口
     * @param userID           用户ID.
     * @param customConfigType 自定义配置的类型.
     * @param key_Values       自定义配置的数据,以key-value的方式存储.其中key必须是自定义配置已定义好的key.
     * @param onResultListen   结果回调
     * @return Future, 其中结果为空字符串(不是null, 是空字符串)表示成功, 非空表示失败信息
     */
    public static Future<String> save(String httpIP, int httpPort, String userID, CustomConfigType customConfigType, HashMap<String, String> key_Values, OnResultListen onResultListen) {
        //其实上传呢是不需要自定义配置的,但是强制加入了自定义配置枚举,并验证传入的key是否和枚举定义好的key相等.是因为这样的目的:
        //防止程序员不小心(太粗心)修改了自己的key,又忘记了在登陆同步数据的地方也一起修改.导致系统维护一段时间后,登录同步数据的功能各种配置残缺不全.
        if (key_Values.size() == 0) {
            throw new RuntimeException("根本就没有配置项!");
        }
        //
        String[] keys = new String[key_Values.size()];
        String[] values = new String[key_Values.size()];
        Iterator<String> it = key_Values.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            String key = it.next();
            String value = key_Values.get(key);
            if (!isStrInStrs(key, customConfigType.keys())) {
                throw new RuntimeException("新加的配置项忘记在CustomConfigType里定义了傻逼");
            }

            keys[i] = key;
            values[i] = value;
            i++;
        }

        return save(httpIP, httpPort, userID, customConfigType.name(), keys, values, onResultListen);
    }

    //在MVP模式中,这样的做法可以非常的流畅.
    //而在MVC中,也能够十分便捷的实现传统观察者的模式.(java 1.8可以通过Future<T>.addListener的方式,但是1.7只能像过去一样手动写.)
    private static Future<String> save(final String httpIP, final int httpPort, final String userID, final String name, final String[] key, final String[] value, final OnResultListen onResultListen) {
       return ThreadPool.instance().submit(new Callable<String>() {
           @Override
            public String call() {
//               getJson(userID, name, key, value);
                JSONObject jsonResult = HttpPost.postJsonObj(url(httpIP, httpPort),  getJson(userID, name, key, value));
                String[] result = HttpPost.checkResult(jsonResult);
                if (result != null) {
                    String error = result[0] + "\n" + result[1];
                    if (onResultListen != null) {
                        onResultListen.onResult(error);
                    }
                    return error;
                }
                //上传服务器成功后,再保存到本地
                String sql = "delete from ConfigTable  where ConfigCode="+"'"+name+"'";
                Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().execSQL(sql);
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < key.length; i++) {
                    String sqlin = "";
                    sqlin = "insert into ConfigTable (ConfigCode,ConfigName,ConfigValue) values ('"
                            + name + "','"+key[i]+"','"+ value[i]+"')";
                    list.add(sqlin);
                    Log.i("testy",sqlin);
                }
                for(String sql1:list){
                    Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().execSQL(sql1);
                }
                if (onResultListen != null) {
                    onResultListen.onResult("");
                }
                return "";
            }
       });
    }


    interface OnResultListen {
        void onResult(String error);
    }


    private static JSONObject getJson(String userID, String name, String[] key, String[] value) {
        JSONObject postJson = new JSONObject();
        try {
            JSONArray array = new JSONArray();

            for (int i = 0; i < key.length; i++) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("CONFIG_NAME", name);
                jsonObject.put("CONFIG_CODE", key[i]);
                jsonObject.put("CONFIG_VALUE", value[i]);
                array.put(jsonObject);
            }


            postJson.put("USER_ID", userID);
            postJson.put("CONFIG", array);
        } catch (JSONException e) {
            return null;
        }
        return postJson;
    }


    /**
     * 判断字符串是否在字符串数组里
     *
     * @param str  字符串
     * @param strs 字符串数组
     * @return
     */
    private static boolean isStrInStrs(String str, String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            if (str.equals(strs[i])) {
                return true;
            }
        }
        return false;
    }
}
