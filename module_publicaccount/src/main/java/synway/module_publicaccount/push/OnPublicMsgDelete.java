package synway.module_publicaccount.push;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import qyc.net.push.synwayoscpush.SPushFacInterface;
import synway.module_interface.AppConfig;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.db.SQLite;
import synway.module_interface.db.table_util.Table_LastContact;
import synway.module_interface.lastcontact.LastContact;
import synway.module_interface.push.FacConfig;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_LastMsg;
import synway.module_publicaccount.public_message.PublicMessage;

/**
 * Created by leo on 2019/1/31.
 */

public class OnPublicMsgDelete extends SPushFacInterface {

    private NetConfig netConfig = null;
    private String userID = null;


    @Override public int[] regist() {
        return new int[]{5009};
    }


    @Override public void onCreat(Object o) {
        FacConfig facConfig = (FacConfig) o;
        userID = facConfig.userID;
        netConfig = facConfig.netConfig;
    }


    @Override public void onDestory() {

    }


    @Override public void onReceive(int type, String jsonStr) {
        Log.d("dym------------------->", "收到公众号消息的删除推送 json= "+jsonStr);

        String msgGUID = null;
        String publicAccountID = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            msgGUID = jsonObject.getString("MSG_GUID");
            publicAccountID = jsonObject.getString("GUID");

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        //判断本地公众号进行校验
        // 获取公众号名称
        String publicName = null;
        String publicinfo = PublicInfoDeal.getName(getDB(), publicAccountID);

        if (publicinfo != null) {
            String strlist[] = publicinfo.split("\\|", -1);
            publicName = strlist[0];

        }
        // 如果数据库中不存该公众号信息，去http 补全该公众号的信息，并且入库
        if (publicName == null) {
            Obj_PublicInfo objInfo = PublicInfoGet.get(userID, publicAccountID, netConfig.publicServerIP, netConfig.publicServerPort);
            if (null == objInfo) {
                return;
            }
            PublicInfoDeal.insert(getDB(), publicAccountID, objInfo.fcName, objInfo.fcCompany, objInfo.fcContact,
                objInfo.fcTel, objInfo.mobile_pic);
            publicName = objInfo.fcName;
        }

        //判断此公众号聊天消息表是否存在，如若不存在，则创建一个
        String sql = Table_PublicAccountRecord.getCreatTableSql(publicAccountID);
        getDB().execSQL(sql);


        //根据该条被删除的消息是否未读,若未读则对外部的未读数减一，若已读则不用。
        //发送广播给最近联系人界面、应用界面(或者第二种模式的专属公众号最近消息界面)对未读数进行减一
        String readSql = "select * from "+Table_PublicAccountRecord.getTableName(publicAccountID)+" where GUID = '"+msgGUID+"'";

        Cursor cursor = getDB().rawQuery(readSql, null);
        int isMsgRead = -1;
        if (cursor.moveToFirst()) {
            isMsgRead = cursor.getInt(
                cursor.getColumnIndex(Table_PublicAccountRecord.publicRecord_col_isMsgRead));
        }
        Log.d("dym------------------->", "isMsgRead= "+isMsgRead);
        //执行删除此GUID的消息记录
        getDB().delete(Table_PublicAccountRecord.getTableName(publicAccountID)," GUID = ?",new String[]{msgGUID});

        //发送广播通知公众号聊天界面
        Intent intent2PublicChat = new Intent();
        intent2PublicChat.setAction(PushUtil.PublicDeleteMsg.getAction(publicAccountID));
        intent2PublicChat.putExtra(PushUtil.PublicDeleteMsg.EXTRA_PUBLIC_DELETE_MSG_GUID, msgGUID);
        context.sendBroadcast(intent2PublicChat);

        //发送广播给最近联系人界面、应用界面(或者第二种模式的专属公众号最近消息界面)对未读数进行减一
        if (isMsgRead == 0) {
            if (AppConfig.PUBLIC_UNREAD_TYPE == 0) {
                dealLastContactUnread(publicAccountID);
            } else if (AppConfig.PUBLIC_UNREAD_TYPE == 1) {
                //第二种模式
                dealPublicLastMsgUnread(publicAccountID);
            } else {
                //第三种模式，两者兼备
                dealLastContactUnread(publicAccountID);
                dealPublicLastMsgUnread(publicAccountID);
            }
        }


    }

    private void dealLastContactUnread(String publicAccountID){
        //第一种模式
        String selection = Table_LastContact.TARGET_ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(publicAccountID) };
        String unReadCountStr = SQLite.queryOneRecord(
            getDB(), Table_LastContact._TABLE_NAME,
            new String[] { Table_LastContact.UN_READ_COUNT }, "|",
            selection, selectionArgs, null);

        if (!TextUtils.isEmpty(unReadCountStr)) {
            int unReadCount = Integer.valueOf(unReadCountStr);
            if (unReadCount > 0) {
                unReadCount = unReadCount - 1;

                //将unReadCount减1，并更新数据库，发送广播更新界面
                ContentValues cv = new ContentValues();
                cv.put(Table_LastContact.UN_READ_COUNT, unReadCount);// 消息
                SQLite.update(getDB(), Table_LastContact._TABLE_NAME, cv,
                    Table_LastContact.TARGET_ID + "=?", new String[] { publicAccountID });

                Intent intentUnreadMinus = new Intent();
                intentUnreadMinus.setAction(LastContact.ACTION_UPDATE_UNREAD_COUNT);
                intentUnreadMinus.putExtra(LastContact.TARGET_ID, publicAccountID);
                intentUnreadMinus.putExtra(LastContact.UN_READ_COUNT, unReadCount);
                context.sendBroadcast(intentUnreadMinus);

                //更新一下总的未读数
                Intent updateAllUnreadIntent = new Intent();
                updateAllUnreadIntent.setAction("home.bottomView.action");
                context.sendBroadcast(updateAllUnreadIntent);
            }
        }
    }

    private void dealPublicLastMsgUnread(String publicAccountID){
        String select = Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?";
        String[] selectArgs = new String[]{String.valueOf(publicAccountID)};
        String unReadtStr = SQLite.queryOneRecord(
            getDB(), Table_PublicAccount_LastMsg._TABLE_NAME,
            new String[]{Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT}, "|",
            select, selectArgs, null);

        if (!TextUtils.isEmpty(unReadtStr)) {
            int unReadCount = Integer.valueOf(unReadtStr);
            if (unReadCount > 0) {
                unReadCount = unReadCount - 1;
                //将unReadCount减1，并更新数据库，发送广播更新界面

                ContentValues contentValues = new ContentValues();
                contentValues.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT,unReadCount);
                SQLite.update(getDB(), Table_PublicAccount_LastMsg._TABLE_NAME, contentValues,
                    Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?", new String[] { publicAccountID });

                Intent intentUnreadMinus = new Intent();
                intentUnreadMinus.setAction(PushUtil.PublicLastUnreadCountMinus.ACTION);
                intentUnreadMinus.putExtra(
                    PushUtil.PublicLastUnreadCountMinus.EXTRA_PUBLIC_ACCOUNT_ID, publicAccountID);
                intentUnreadMinus.putExtra(
                    PushUtil.PublicLastUnreadCountMinus.EXTRA_PUBLIC_UNREAD_COUNT, unReadCount);
                context.sendBroadcast(intentUnreadMinus);

                //更新一下总的未读数
                Intent updateAllUnreadIntent = new Intent();
                updateAllUnreadIntent.setAction(PublicMessage.ACTION_UNREAD_COUNT_MINUS);
                context.sendBroadcast(updateAllUnreadIntent);
            }
        }
    }
}
