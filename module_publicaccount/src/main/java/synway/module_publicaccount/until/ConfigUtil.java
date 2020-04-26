package synway.module_publicaccount.until;

import synway.module_interface.config.BaseUtil;

import static synway.module_interface.config.BaseUtil.getFolderPath;

/**
 * Created by admin on 2017/4/10.
 */

public class ConfigUtil {
    /**获取头像地址**/
    public static  String getPath(String id) {
        return BaseUtil.FILE_HEAD_IMG_THU + "/" + id;
    }
    public static String getBlueToothPath(){
        return getFolderPath() + "/PublicAccount/bluetooth";
    }
    public static  String SENDIMRIURL="http://%s:%s/PFService/PublicFunction/SendImriURL.osc";
    public static  String UPLOADFILEURL="http://%s:%s/SynwayOSCFTP/FTP/FileUpload.osc";
    /***
     * 各个公众平台GUID，不变
     */
    /** 案件侦控 **/
    public static final String PUB_AJZK_GUID = "33a21d87-e534-484c-941f-a3ca5477c671";
    /** 信息预警 **/
    public static final String PUB_XXYJ_GUID = "c6653c8c-9b7c-4fba-b86e-d8bcdff799e3";
    /** 案件审批 **/
    public static final String PUB_AJSP_GUID = "8e5fbc6d-271b-4ac0-b3a9-1b086ff64585";
    /** 城际平台 **/
    public static final String PUB_CJPT_GUID = "6fbef487-bf06-444b-bbb6-aa056926d771";
    public static final String ENEMYCOLOR="#FF00FF";
    public static final String FRIENDCOLOR="#BCEE68";
    public static final String EQUIPCOLOR="#1E90FF";
}
