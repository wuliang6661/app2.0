package cn.synway.synmonitor.config;

import android.annotation.SuppressLint;
import android.content.Context;

import cn.synway.synmonitor.bean.UserBO;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/1710:33
 * desc   :  存放所有全局变量
 * version: 1.0
 */
public class Config {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    /**
     * sdk版本号
     */
    public static final String SYNCOUNTLY_SDK_VERSION_STRING = "1.0";

    /**
     * 上传到后台的URL
     */
    public static String SERVER_URL = "";

    /**
     * AppKEY
     */
    public static String APP_KEY = "9DSA78778DSADS6878";

    /**
     * 上报间隔（分钟数）, 默认5分钟
     */
    public static int UPDATE_TIME = 5;
    
    /**
     * 用户信息
     */
    public static UserBO userBO;


}
