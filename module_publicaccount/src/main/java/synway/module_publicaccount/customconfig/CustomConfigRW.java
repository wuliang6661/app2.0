package synway.module_publicaccount.customconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;

import synway.module_interface.config.CustomConfigType;


/**
 * Created by qyc on 2016/9/27.
 */

public class CustomConfigRW {

    //作用域:提供自定义配置的读写方法.
    //输入:读(userID,配置类型,keys)  写(服务器地址,userID,配置类型,配置key-value).写提供了静态和非静态两种方式,非静态支持观察者回调.
    //输出:读(values)  写(保存结果,通过Future或观察者)
    //生命周期:无    写(非静态,增加停止方法)

    public static String read(CustomConfigType customConfigType, String key) {
       String keys[] =new String[1];
        keys[0]=key;
        ArrayList<HashMap<String,String>> hashMaps=CustomConfigRead.read(customConfigType,keys,null);
        if(hashMaps.size()>0){
           return CustomConfigRead.read(customConfigType,keys,null).get(0).get(key);
        }else {
            return null;
        }
    }

    public static ArrayList<HashMap<String,String>> read(CustomConfigType customConfigType, String[] keys) {

        return CustomConfigRead.read(customConfigType,keys,null);
    }

    public static Future<String> write(String httpIP, int httpPort, String userID, CustomConfigType customConfigType, HashMap<String, String> key_values) {
        return CustomConfigSave.save(httpIP, httpPort, userID, customConfigType, key_values, null);
    }

    public static Future<String> write(String httpIP, int httpPort, String userID, CustomConfigType customConfigType, String key, String value) {
        HashMap<String, String> key_values = new HashMap<>();
        key_values.put(key, value);
        return CustomConfigSave.save(httpIP, httpPort, userID, customConfigType, key_values, null);
    }

    private String httpIP;
    private int httpPort;
    private String userID;
    private OnResultListen onResultListen;
    private Object requestLock = new Object();

    public CustomConfigRW(String httpIP, int httpPort, String userID, OnResultListen onResultListen) {
        this.httpIP = httpIP;
        this.httpPort = httpPort;
        this.userID = userID;
        this.onResultListen = onResultListen;
    }

    public void write(CustomConfigType customConfigType, HashMap<String, String> key_values) {
        CustomConfigSave.save(httpIP, httpPort, userID, customConfigType, key_values, new CustomConfigSave.OnResultListen() {
            @Override
            public void onResult(String error) {
                synchronized (requestLock) {
                    if (onResultListen != null) {
                        onResultListen.onResult(error);
                    }
                }
            }
        });
    }

    public void write(CustomConfigType customConfigType, String key, String value) {
        HashMap<String, String> key_values = new HashMap<>();
        key_values.put(key, value);
        CustomConfigSave.save(httpIP, httpPort, userID, customConfigType, key_values, new CustomConfigSave.OnResultListen() {
            @Override
            public void onResult(String error) {
                synchronized (requestLock) {
                    if (onResultListen != null) {
                        onResultListen.onResult(error);
                    }
                }
            }
        });
    }

    public void stop() {
        synchronized (requestLock) {
            this.onResultListen = null;
        }
    }

    public interface OnResultListen {
        void onResult(String error);
    }


}
