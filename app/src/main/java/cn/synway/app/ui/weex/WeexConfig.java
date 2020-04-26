package cn.synway.app.ui.weex;

/**
 * Created by Android Studio.
 * User: dell
 * Date: 2019/8/15
 * Time: 10:49
 */
public class WeexConfig {
    // 手机资源调用接口文档：1.0.6
    public static final String InterfaceVersion = "1.0.6";
    //weex的SDK版本
    public static final String WeexSdkVersion = "weex_sdk:0.19.0.7";
    //模块版本
    public static final String AppModuleVersion = "1.0.0";

    //hot refresh
    public static final int HOT_REFRESH_CONNECT = 0x111;
    public static final int HOT_REFRESH_DISCONNECT = HOT_REFRESH_CONNECT + 1;
    public static final int HOT_REFRESH_REFRESH = HOT_REFRESH_DISCONNECT + 1;
    public static final int HOT_REFRESH_CONNECT_ERROR = HOT_REFRESH_REFRESH + 1;

}
