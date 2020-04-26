package synway.module_publicaccount.weex_module.catchexp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.weex_module.beans.WxMapData;

/**
 * Created by huangxi
 * DATE :2019/3/28
 * Description ：处理Weex报错异常
 */

public class WeexErrorHandler {
    private Context context = null;

    public static final String LOG_FOLDER_NAME = "WeexExpLog";
    public static final String LOG_FILE_NAME = "WeexExpLog";

    public WeexErrorHandler(Context context) {
        this.context = context;
    }

    public void writeWeexError(String errCode, String msg,String url,WxMapData wxMapData){
        //设备信息
        String deviceInfo = collectCrashDeviceInfo(context);//设备信息
        //打印日志
        String logMsg = "用户名:" + Sps_RWLoginUser.readUserID(context);
        logMsg += "\n";
        logMsg += "设备信息:" + deviceInfo;
        logMsg += "\n";
        logMsg += "接口文档版本:" + Main.InterfaceVersion;
        logMsg += "\n";
        logMsg += "weex的SDK版本:" + Main.WeexSdkVersion;
        logMsg += "\n";
        logMsg += "模块版本:" + Main.AppModuleVersion;
        logMsg += "\n";
        logMsg += "异常信息:\n";
        logMsg += "Error："+errCode+"MSG："+msg;
        logMsg += "\n";
        logMsg += "页面跳转地址:" + url;
        logMsg += "\n";
        if(wxMapData!=null){
            logMsg += "SourceUrl:" + wxMapData.getWxMapData().get("SourceUrl");
            logMsg += "\n";
            logMsg += "PaId:" + wxMapData.getWxMapData().get("PaId");
            logMsg += "\n";
            logMsg += "QueryData:" + wxMapData.getWxMapData().get("QueryData");
        }
        FileLog.write(LOG_FOLDER_NAME, LOG_FILE_NAME, logMsg);

    }
    /**
     * 收集程序报错的设备信息
     */
    private static String collectCrashDeviceInfo(Context context) {
        String msgInfo = "";
        msgInfo += "[MODEL(手机型号) " + Build.MODEL + "]";
        msgInfo += " ";
        msgInfo += "[SDK(安卓版本) " + Build.VERSION.SDK_INT + "]";//Android版本
        msgInfo += " ";

        String versionName = "";
        try {
            PackageInfo pkg = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionName = pkg.versionName;
        } catch (Exception e) {
        }
        if (!versionName.equals("")) {
            msgInfo += "[APP版本 " + versionName + "]";
        }

        return msgInfo;
    }
}
