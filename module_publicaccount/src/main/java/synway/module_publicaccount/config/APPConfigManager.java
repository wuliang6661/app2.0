package synway.module_publicaccount.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 13itch on 2016/8/4.
 */
public class APPConfigManager {
    public static final String APP_CONFIG="OSC_APP_CONFIG";

    /**获取APPConfig*/
    public static APPConfigObj getAPPConfigFromSpf(Context context){
        APPConfigObj appConfigObj=new APPConfigObj();
        SharedPreferences spf = context.getSharedPreferences(APP_CONFIG,
                Context.MODE_MULTI_PROCESS);
        appConfigObj.setIS_INTENT_OPEN(spf.getBoolean("IS_INTENT_OPEN",true));
        appConfigObj.setIS_WIFI_OPEN(spf.getBoolean("IS_WIFI_OPEN",true));
        appConfigObj.setIS_PISH_SLEEP(spf.getBoolean("IS_PISH_SLEEP",false));
        appConfigObj.setPPVOICE_GROUP_OPEN(spf.getBoolean("PPVOICE_GROUP_OPEN",true));
        appConfigObj.setPPVOICE_PERSON_OPEN(spf.getBoolean("PPVOICE_PERSON_OPEN",false));

        appConfigObj.setLOCATION_DEFAULT_TYPE(spf.getInt("LOCATION_DEFAULT_TYPE",0));
        appConfigObj.setLOCATION_DEFAULT_PERIOD(spf.getInt("LOCATION_DEFAULT_PERIOD",8));
        appConfigObj.setREPORT_CONNECT_CLOSE_OUTTIME(spf.getInt("REPORT_CONNECT_CLOSE_OUTTIME",5));

        return appConfigObj;
    }

    /**保存APPConfig*/
    public static void saveAPPConfig2Spf(Context context,APPConfigObj appConfigObj){
        SharedPreferences.Editor edt = context.getSharedPreferences(APP_CONFIG,
                Context.MODE_MULTI_PROCESS).edit();
        edt.putBoolean("IS_INTENT_OPEN", appConfigObj.IS_INTENT_OPEN);
        edt.putBoolean("IS_WIFI_OPEN", appConfigObj.IS_WIFI_OPEN);
        edt.putBoolean("IS_PISH_SLEEP", appConfigObj.IS_PISH_SLEEP);
        edt.putBoolean("PPVOICE_GROUP_OPEN", appConfigObj.PPVOICE_GROUP_OPEN);
        edt.putBoolean("PPVOICE_PERSON_OPEN", appConfigObj.PPVOICE_PERSON_OPEN);
        edt.putInt("LOCATION_DEFAULT_TYPE", appConfigObj.LOCATION_DEFAULT_TYPE);
        edt.putInt("LOCATION_DEFAULT_PERIOD", appConfigObj.LOCATION_DEFAULT_PERIOD);
        edt.putInt("REPORT_CONNECT_CLOSE_OUTTIME", appConfigObj.REPORT_CONNECT_CLOSE_OUTTIME);
        edt.commit();
    }

    /**
     * 重置配置信息
     */
    public static void clear(Context context) {
        context.getSharedPreferences(APP_CONFIG, Context.MODE_MULTI_PROCESS)
                .edit().clear().commit();
    }

}
