package cn.synway.app.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/1713:48
 * desc   :  路径工具类
 * version: 1.0
 */
public class UrlUtils {

    /**
     * 检查URL是否有效
     */
    public static boolean isValidURL(String urlStr) {
        boolean validURL = false;
        if (urlStr != null && urlStr.length() > 0) {
            try {
                new URL(urlStr);
                validURL = true;
            } catch (MalformedURLException e) {
                validURL = false;
            }
        }
        return validURL;
    }


}
