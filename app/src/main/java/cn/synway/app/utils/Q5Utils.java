package cn.synway.app.utils;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import java.util.HashMap;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2916:56
 * desc   : 腾讯内核打开文件方法
 * version: 1.0
 */
public class Q5Utils {


    public static void openOtherFile(String path) {
        /*QbSdk.canOpenFile(activity, path, new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean aBoolean) {
                Log.e(TAG,"文件是否能够打开:"+aBoolean);
            }
        });*/
        HashMap<String, String> params = new HashMap<>();
        //“0”表示文件查看器使用默认的UI 样式。“1”表示文件查看器使用微信的UI 样式。不设置此key或设置错误值，则为默认UI 样式。
        params.put("style", "1");
        //“true”表示是进入文件查看器，如果不设置或设置为“false”，则进入miniqb 浏览器模式。不是必须设置项
        params.put("local", "true");
        //定制文件查看器的顶部栏背景色。格式为“#xxxxxx”，例“#2CFC47”;不设置此key 或设置错误值，则为默认UI 样式。
        params.put("topBarBgColor", "#649aff");
        QbSdk.openFileReader(AppManager.getAppManager().curremtActivity(),
                path, params, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
    }
}
