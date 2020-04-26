package synway.module_publicaccount.publiclist.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import synway.module_publicaccount.until.StringUtil;

/**
 * Created by admin on 2017/11/20.
 * APP详细信息
 */

public class App_InformationBean implements Serializable {
    //APP版本信息
    public String app_version="";
    //APP包名
    public String app_packangename="";
    //APP名称(非包名)
    public String app_name = "";
    //新版本更新信息
    public String app_newinformation="";
    //APP下载地址
    public String app_download_url="";

public static App_InformationBean getAppInforMation(String information){
     App_InformationBean app_informationBean=new App_InformationBean();
     if(StringUtil.isEmpty(information)){
         return app_informationBean;
     }
    try {
        JSONObject jsonObject=new JSONObject(information);
        app_informationBean.app_version=jsonObject.optString("APP_VERSION");
        app_informationBean.app_packangename=jsonObject.optString("APP_PACKAGENAME");
        app_informationBean.app_newinformation=jsonObject.optString("APP_NEWINFORMATION");
        app_informationBean.app_download_url=jsonObject.optString("APP_DOWNLOAD_URL");
        app_informationBean.app_name = jsonObject.optString("APP_NAME");
    } catch (JSONException e) {
        e.printStackTrace();
        return app_informationBean;
    }
    return  app_informationBean;
}
    public static JSONObject getAppInforMationObject(App_InformationBean app_informationBean){
        JSONObject jsonObject=new JSONObject();
        try {
            if(app_informationBean!=null) {
                jsonObject.put("APP_VERSION", app_informationBean.app_version);
                jsonObject.put("APP_PACKAGENAME", app_informationBean.app_packangename);
                jsonObject.put("APP_NEWINFORMATION", app_informationBean.app_newinformation);
                jsonObject.put("APP_DOWNLOAD_URL", app_informationBean.app_download_url);
                jsonObject.put("APP_NAME",app_informationBean.app_name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return  jsonObject;
        }
        return  jsonObject;
    }
}
