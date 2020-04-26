package synway.module_publicaccount.push;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import qyc.net.push.synwayoscpush.SPushFacInterface;
import synway.common.download.SynDownload;
import synway.module_interface.AppConfig;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.db.SQLite;
import synway.module_interface.db.table_util.Table_LastContact;
import synway.module_interface.lastcontact.LastContact;
import synway.module_interface.push.FacConfig;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.analytical.fac.AnalyticalPath;
import synway.module_publicaccount.analytical.fac.IAnalytical_Base;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_LastMsg;
import synway.module_publicaccount.public_message.PublicMessage;

public class OnPublicHmMsg extends SPushFacInterface {

    private NetConfig netConfig = null;
    private String userID = null;


    @Override
    public int[] regist() {
        return new int[]{5099};
    }

    @Override
    public void onCreat(Object o) {
        FacConfig facConfig = (FacConfig) o;
        userID = facConfig.userID;
        netConfig = facConfig.netConfig;
        SparseArray<IAnalytical_Base> analys = new SparseArray<>();
        IAnalytical_Base analytical;
        for (int i = 0; i < AnalyticalPath.CLASS_PATH.length; i++) {
            try {
                analytical = (IAnalytical_Base) Class.forName(
                        AnalyticalPath.CLASS_PATH[i]).newInstance();
            } catch (Exception e) {
                return;
                // ThrowExp.throwRxp("公众帐号解析工厂类的包路径错误");
            }
            int msgType = analytical.msgType();
            if (msgType <= 0) {
                return;
                // ThrowExp.throwRxp("公众帐号解析工厂类没有注册需要接收的msgType");
            }
            if (analys.get(msgType) != null) {
                return;
            }
            analys.put(msgType, analytical);
            analytical.onInit(context);
        }
    }

    @Override
    public void onDestory() {

    }

    @Override
    public void onReceive(int type, String jsonStr) {
        if (type == 5099) {
            run5099(jsonStr);
        }
    }


    private void run5099(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }
        Log.e("wuliang", jsonStr);
        String loginUserID = userID;
        JSONObject jsonTotal = null;
        String publicGUID = null;
        JSONObject jsonMsg = null;
        JSONObject msg = null;
        Log.i("testy", "收到未解析的推送数据" + jsonStr);
        Log.d("dym------------------->", "OnPublicHmMsg 收到推送,jsonStr= " + jsonStr);
        try {
            jsonTotal = new JSONObject(jsonStr);
            publicGUID = jsonTotal.getString("GUID");
            msg = jsonTotal.getJSONObject("MSG");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String publicName = null;
        String publicpicid;
        // 获取公众号名称
        String publicinfo = PublicInfoDeal.getName(getDB(), publicGUID);
        if (publicinfo != null) {
            String strlist[] = publicinfo.split("\\|", -1);
            publicName = strlist[0];
            publicpicid = strlist[1];
        }
        String path = getPublicName(publicGUID);
        Drawable drawable = Drawable.createFromPath(path);

        if(msg != null){
            Intent intent = new Intent("android.intent.action.HM_WARRING");
            intent.putExtra("msg", msg.toString());
            context.sendBroadcast(intent);
        }
        // 如果数据库中不存该公众号信息，
        // 去http 补全该公众号的信息，
        // 并且入库
        if (publicName == null) {
            Obj_PublicInfo objInfo = PublicInfoGet.get(loginUserID, publicGUID, netConfig.publicServerIP, netConfig.publicServerPort);
            if (null == objInfo) {
                return;
            }
            PublicInfoDeal.insert(getDB(), publicGUID,
                    objInfo.fcName, objInfo.fcCompany, objInfo.fcContact,
                    objInfo.fcTel, objInfo.mobile_pic);
            publicName = objInfo.fcName;
            publicpicid = objInfo.mobile_pic;
            /**
             * 本地图片不存在，说明服务器图片更新了，和服务器图片是否一样的比对在推送或者同步或者拉列表的时候就判断过了
             */
            if (drawable == null) {
                String urlHead = "http://" + netConfig.httpIP + ":" + netConfig.httpPort + "/" + "OSCUserPic/Public_";
                String urlStr = "";
                if (publicpicid != null) {
                    if (publicpicid.contains(".")) {
                        urlStr = "http://" + netConfig.httpIP + ":" + netConfig.httpPort + "/" + "OSCUserPic/" + publicpicid;
                    } else {
                        urlStr = urlHead + publicpicid;
                    }

                    Boolean ifsuccess = SynDownload.httpDownload(new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (publicGUID)), urlStr);
                }
            }
        }
        long serverTime = System.currentTimeMillis();
        long localeTime = System.currentTimeMillis();

        sendPublicMsgBaseToLastContact(publicName, publicGUID, "", serverTime,
                localeTime, "");


    }

    /**
     * 判断公众号是否被授权
     *
     * @param publicAccountID 公众号ID
     * @return 是否被授权
     */
    public Boolean getPublicAccountData(String publicAccountID) {
        String sql = "select * " + " from " + Table_PublicAccount._TABLE_NAME + " where " + Table_PublicAccount.FC_ID + "='" + publicAccountID + "'";
        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().rawQuery(sql, null);
        if (cursor != null) {
            return cursor.getCount() > 0;
        }
        return false;
    }


    private String getPublicName(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + id;
    }


    private void sendPublicMsgBaseToLastContact(String publicName, String publicGUID, String showTime, long serverTime, long localeTime, String briefMsg) {
        long now = System.currentTimeMillis();

        if (AppConfig.PUBLIC_UNREAD_TYPE == 0) {
            dealLastContact(publicName, publicGUID, showTime, serverTime, localeTime, briefMsg);
        } else if (AppConfig.PUBLIC_UNREAD_TYPE == 1) {
            dealPublicLastMsg(publicName, publicGUID, showTime, serverTime, localeTime, briefMsg);
        } else {
            dealLastContact(publicName, publicGUID, showTime, serverTime, localeTime, briefMsg);
            dealPublicLastMsg(publicName, publicGUID, showTime, serverTime, localeTime, briefMsg);
        }

//        // 通知栏 告知公众号新消息
//        newPANotiftDeal.notiyNewMsg(getSqliteHelp(), publicGUID);
//        result.publicGUID = publicGUID;
//        result.showTime = showTime;
//        result.localTime = now;
    }


    private void dealLastContact(String publicName, String publicGUID, String showTime, long serverTime, long localeTime, String briefMsg) {
        // 设置最近联系人的未读数
        int unReadCount = 0;
        String selection = Table_LastContact.TARGET_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(publicGUID)};
        String unReadCountStr = SQLite.queryOneRecord(
                getDB(),
                Table_LastContact._TABLE_NAME,
                new String[]{Table_LastContact.UN_READ_COUNT}, "|",
                selection, selectionArgs, null);
        if (unReadCountStr != null && !unReadCountStr.equals("")) {
            unReadCount = Integer.valueOf(unReadCountStr) + 1;
        } else {
            unReadCount = 1;
        }
        //通过接口模块插入最近联系人
        SQLiteDatabase db = getDB();
        LastContact.insertOrUpdate(2, context,
                db, publicGUID, publicName,
                publicName + "的消息", localeTime, serverTime, unReadCount,
                publicGUID, publicName);
    }

    private void dealPublicLastMsg(String publicName, String publicGUID, String showTime, long serverTime, long localeTime, String briefMsg) {
        // 设置公众号消息未读数
        int unRead = 0;
        String select = Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?";
        String[] selectArgs = new String[]{String.valueOf(publicGUID)};
        String unReadtStr = SQLite.queryOneRecord(
                getDB(),
                Table_PublicAccount_LastMsg._TABLE_NAME,
                new String[]{Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT}, "|",
                select, selectArgs, null);
        if (unReadtStr != null && !unReadtStr.equals("")) {
            unRead = Integer.valueOf(unReadtStr) + 1;
        } else {
            unRead = 1;
        }
        //通过接口模块插入公众号消息表
        SQLiteDatabase db1 = getDB();
        PublicMessage.insertOrUpdate(context,
                db1, publicGUID, publicName,
                localeTime, serverTime, unRead, briefMsg + "收到一条消息");
    }


}
