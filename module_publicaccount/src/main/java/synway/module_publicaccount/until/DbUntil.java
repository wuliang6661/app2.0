package synway.module_publicaccount.until;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;

import synway.module_interface.config.BaseUtil;
import synway.module_interface.db.SQLite;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicConfigMsg;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_chat.Obj_PaConfigMsg;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;

import static synway.module_publicaccount.publiclist.bean.App_InformationBean.getAppInforMationObject;

/**
 * 公众帐号数据库处理公用类
 * Created by ysm on 2017/1/23.
 */

public class DbUntil {
    //往菜单表加数据
    public static void DBDeal(ArrayList<Obj_Menu> arrayList, ArrayList<Obj_Menu> secondMenuList, String publicGUID, SQLiteDatabase db) {
        //先对比和服务器的图片ID是否一样，如果不一样，就把图片直接删除，如果一样，就不删除
        String col = Table_PublicMenu.PAM_menuGUID + "," + Table_PublicMenu.PAM_menuPicId;
        String sql = "select " + col + " from " + Table_PublicMenu._TABLE_NAME + " where " + Table_PublicMenu.PAM_ID + "=" + "'" + publicGUID + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                StringBuffer sb = new StringBuffer();
                String menuid = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuGUID));
                String picid = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuPicId));
                if (arrayList.size() > 0) {
                    for (Obj_Menu firstmenu : arrayList) {
                        if (firstmenu.menuGUID.equals(menuid)) {
                            //如果本来是空的，就不用删除
                            if (StringUtil.isNotEmpty(picid)) {
                                if (!picid.equals(firstmenu.menuPicUrl)) {//如果图片名称相同的话，就不删除，如果不相同。就删除本地的图片
                                    File filePath = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (menuid));
                                    if (filePath.exists()) {//如果存在该公众号ID的头像，先删除
                                        filePath.delete();
                                    }
                                }
                            }
                        }
                    }
                }
                if (secondMenuList.size() > 0) {
                    for (Obj_Menu secondtmenu : secondMenuList) {
                        if (secondtmenu.menuGUID.equals(menuid)) {
                            if (StringUtil.isNotEmpty(picid)) {
                                if (!picid.equals(secondtmenu.menuPicUrl)) {//如果图片名称相同的话，就不删除，如果不相同。就删除本地的图片
                                    File filePath = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (secondtmenu.menuGUID));
                                    if (filePath.exists()) {//如果存在该公众号ID的头像，先删除
                                        filePath.delete();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // 清空该公众号对应的菜单表
        SQLite.del(db, Table_PublicMenu._TABLE_NAME, "ID=?", new String[]{publicGUID});
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (int i = 0; i < arrayList.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put(Table_PublicMenu.PAM_ID, arrayList.get(i).ID);
            cv.put(Table_PublicMenu.PAM_menuName, arrayList.get(i).menuName);
            cv.put(Table_PublicMenu.PAM_menuFather, arrayList.get(i).menuFather);
            cv.put(Table_PublicMenu.PAM_menuGUID, arrayList.get(i).menuGUID);
            cv.put(Table_PublicMenu.PAM_menuKey, arrayList.get(i).menuKey);
            cv.put(Table_PublicMenu.PAM_menuType, arrayList.get(i).menuType);
            cv.put(Table_PublicMenu.PAM_menuUrl, arrayList.get(i).menuUrl);
            cv.put(Table_PublicMenu.PAM_menuPicId, arrayList.get(i).menuPicUrl);

            contentValues.add(cv);
        }
        for (int i = 0; i < secondMenuList.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put(Table_PublicMenu.PAM_ID, secondMenuList.get(i).ID);
            cv.put(Table_PublicMenu.PAM_menuName,
                    secondMenuList.get(i).menuName);
            cv.put(Table_PublicMenu.PAM_menuFather,
                    secondMenuList.get(i).menuFather);
            cv.put(Table_PublicMenu.PAM_menuGUID,
                    secondMenuList.get(i).menuGUID);
            cv.put(Table_PublicMenu.PAM_menuKey, secondMenuList.get(i).menuKey);
            cv.put(Table_PublicMenu.PAM_menuType,
                    secondMenuList.get(i).menuType);
            cv.put(Table_PublicMenu.PAM_menuUrl, secondMenuList.get(i).menuUrl);
            cv.put(Table_PublicMenu.PAM_menuPicId, secondMenuList.get(i).menuPicUrl);
            contentValues.add(cv);

        }
        SQLite.inserts(db, Table_PublicMenu._TABLE_NAME, contentValues);

    }

    /*****
     * 清空菜单表
     * @param publicGUID
     */
    public static void clearDBfromGUID(String publicGUID, SQLiteDatabase db) {
        SQLite.del(db,
                Table_PublicMenu._TABLE_NAME, "ID=?", new String[]{publicGUID});
    }

    /****
     * 修改公众帐号表中数据
     * @param list
     */
    public static void DBDeal(ArrayList<Obj_PublicAccount> list, ArrayList<Obj_PaConfigMsg> list1, SQLiteDatabase db) {
        //先对比和服务器的图片ID是否一样，如果不一样，就把图片直接删除，如果一样，就不删除
        String tableName = Table_PublicAccount._TABLE_NAME;
        String columns[] = new String[]{Table_PublicAccount.FC_ID, Table_PublicAccount.FC_NAME, Table_PublicAccount.FC_COMPANY,
                Table_PublicAccount.FC_CONTACT, Table_PublicAccount.FC_TEL, Table_PublicAccount.FC_MOBILEPIC};
        String strs[] = SQLite.query(db, tableName, columns, "|", null, null, null);
        if (null == strs || strs.length == 0) {
        } else {
            for (int i = 0; i < strs.length; i++) {
                String sList[] = strs[i].split("\\|", -1);
                String ID = sList[0];
                String fc_mobilepic = sList[5];
                if (list.size() > 0) {
                    for (Obj_PublicAccount obj_publicAccount : list) {
                        if (ID.equals(obj_publicAccount.ID)) {//相同的帐号
                            if (!fc_mobilepic.equals(obj_publicAccount.fc_mobilepic)) {//如果图片名称相同的话，就不删除，如果不相同。就删除本地的图片
                                File filePath = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (ID));
                                if (filePath.exists()) {//如果存在该公众号ID的头像，先删除
                                    filePath.delete();
                                }
                            }
                        }
                    }
                }
            }
        }
        SQLite.del(db, Table_PublicAccount._TABLE_NAME, null, null);
        SQLite.del(db, Table_PublicConfigMsg._TABLE_NAME, null, null);  //先清空该表数据
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>();
        for (int i = 0; i < list.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put(Table_PublicAccount.FC_ID, list.get(i).ID);
            cv.put(Table_PublicAccount.FC_NAME, list.get(i).name);
            cv.put(Table_PublicAccount.FC_COMPANY, list.get(i).company);
            cv.put(Table_PublicAccount.FC_CONTACT, list.get(i).contact);
            cv.put(Table_PublicAccount.FC_TEL, list.get(i).contactTel);
            cv.put(Table_PublicAccount.FC_MOBILEPIC, list.get(i).fc_mobilepic);
            cv.put(Table_PublicAccount.FC_TYPE, list.get(i).type);
            cv.put(Table_PublicAccount.PAM_FatherGroupID, list.get(i).fatherGroupID);
            cv.put(Table_PublicAccount.PAM_FatherGroupName, list.get(i).fatherGroupName);
            cv.put(Table_PublicAccount.PAM_PushMsgTypeList, list.get(i).pushMsgTypeList);
            if (list.get(i).app_information != null) {
                cv.put(Table_PublicAccount.APP_INFORMATION, getAppInforMationObject(list.get(i).app_information).toString());
            }
            contentValues.add(cv);
        }

        SQLite.inserts(db, Table_PublicAccount._TABLE_NAME, contentValues);       //再插入操作
//处理公众号配置表
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).type == 2) {
                ContentValues cv = new ContentValues();
                cv.put(Table_PublicConfigMsg.PAC_ID, list.get(i).ID);
                cv.put(Table_PublicConfigMsg.PAC_SourceUrl, list.get(i).urlObj.urlParam);
                cv.put(Table_PublicConfigMsg.PAM_PublicUrlType, list.get(i).urlObj.urlType);
                cv.put(Table_PublicConfigMsg.PAM_PublicUrl, list.get(i).urlObj.publicUrl);
                cv.put(Table_PublicConfigMsg.PAM_ISSHowTitle, list.get(i).urlObj.isShowTitle);
                contentValuesArrayList.add(cv);
            }
        }

        SQLite.inserts(db, Table_PublicConfigMsg._TABLE_NAME, contentValuesArrayList);

        //db.close();
    }

    public static void DBDealPublicMenu(ArrayList<Obj_PublicAccount> list, SQLiteDatabase db) {
        String deletemenu = "";
        for (Obj_PublicAccount obj_publicAccount : list) {
            deletemenu = deletemenu + " ID!='" + obj_publicAccount.ID + "' and";
        }
        if (StringUtil.isNotEmpty(deletemenu)) {
            deletemenu = deletemenu.substring(0, deletemenu.length() - 3);
        }
        String sql = "delete from " + Table_PublicMenu._TABLE_NAME + " where " + deletemenu;
        db.execSQL(sql);
    }
}
