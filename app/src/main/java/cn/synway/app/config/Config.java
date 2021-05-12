package cn.synway.app.config;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.util.List;

import cn.synway.app.db.dbmanager.ConfigIml;
import cn.synway.app.db.table.ConfigEntry;
import cn.synway.app.utils.NetUtils;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/1715:37
 * desc   :  所有APP里的配置项
 * version: 1.0
 */
public class Config {


    /**
     * App连接的服务ip
     */
    public static String SERVER_IP = "176.16.21.19";

    /**
     * App连接的服务端口
     */
    public static String SERVER_PORT = "9066";

    /**
     * 本地缓存的sp名称
     */
    public static final String SP_NAME = "SYNWAY";

    /**
     * 活体开关是否开启
     */
    public static boolean isOpenLive = false;

    /**
     * App是否自动登录
     */
    public static boolean isOpenLogin = false;

    /**
     * App水印是否开启
     */
    public static boolean isOpenWaterMaker = false;

    /**
     * App是否可以检查更新
     */
    public static boolean isUpdate = true;

    /**
     * 推送服务的IP端口
     */
    public static String pushIp = "";
    public static String pushPort = "";

    //推送服务的文件上传端口
    public static String pushUpdatePort = "";


    /**
     * 公众号服务的IP端口
     */
    public static String publicIP = "";

    public static String publicPort = "";

    /**
     * App是否有新版本
     */
    public static boolean isHaveNewVersion = false;

    /**
     * App是在前台还是在后台
     */
    public static boolean AppInBack = false;

    /**
     * App应用中心名称
     */
    public static String AppCenterName = "";


    /**
     * 数据上报地址
     */
    public static String gartherUrl = "";
    /**
     * 数据上报APPkEY
     */
    public static String appKey = "123";

    /**
     * 收集工具的LOG开关
     */
    public static boolean SynCountlyLog = true;

    /**
     * 消息界面是否显示 (默认显示)
     */
    public static boolean showMessageMenu = false;

    /**
     * 通讯录界面是否显示（默认显示）
     */
    public static boolean showAddressListMenu = false;

    /**
     * 本机IP地址
     */
    public static String DEVICE_IP = "";


    /**
     * 本机IP地址适用异步线程获取
     */
    public static void getDeviceIP() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DEVICE_IP = NetUtils.getIPAddress(Utils.getApp());
                LogUtils.e(DEVICE_IP);
            }
        }).start();
    }


    /**
     * 根据数据库配置检测哪个IP当前选中
     */
    public static String getServerIp() {
        List<ConfigEntry> configEntries = ConfigIml.getSelectConfig();
        if (configEntries.isEmpty()) {
            return SERVER_IP;
        }
        SERVER_IP = configEntries.get(0).serverIp;
        return SERVER_IP;
    }


    /**
     * 根据数据库配置检测哪个IP当前选中
     */
    public static String getServerPort() {
        List<ConfigEntry> configEntries = ConfigIml.getSelectConfig();
        if (configEntries.isEmpty()) {
            return SERVER_PORT;
        }
        SERVER_PORT = configEntries.get(0).serverPort;
        return SERVER_PORT;
    }

}
