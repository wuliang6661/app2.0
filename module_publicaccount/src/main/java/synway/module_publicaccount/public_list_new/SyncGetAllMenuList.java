package synway.module_publicaccount.public_list_new;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import qyc.library.tool.http.HttpHead;
import qyc.library.tool.http.HttpPost;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.db.SQLite;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.publiclist.GetHttpData.PublicPost;
import synway.module_publicaccount.publiclist.PicidPublicidBean;
import synway.module_publicaccount.publiclist.SyncGetHeadThu;
import synway.module_publicaccount.until.StringUtil;

/**
 * Created by huangxi
 * DATE :2019/2/13
 * Description ：获取并更新所有公众号菜单信息
 */

public class SyncGetAllMenuList {
    private Context context;
    private SyncGetHeadThu syncGetHeadThu;
    //下载需要下载的菜单图标
    private ArrayList<PicidPublicidBean> needDownMenuList ;
    public SyncGetAllMenuList(Context context) {
        this.context=context;
        needDownMenuList = new ArrayList<>();
    }

    public void start(String ip, int port, String userID) {
        String url = HttpHead.urlHead(ip, port)
                + "/publicFunc/getMenuInfo?userId="+userID;
        syncGetHeadThu = new SyncGetHeadThu(ip, port, context);
        new Thread(new SyncGetAllMenuList.mRunnable(url, userID)).start();
    }

    private class mRunnable implements Runnable {

        private String url = null;
        private String userID = null;

        // ==============result
        private JSONObject resultJson = null;
        private String reuslt[] = null;

        public mRunnable(String url, String userID) {
            this.url = url;
            this.userID = userID;
        }
        @Override
        public void run() {
            resultJson = HttpPost.postJsonObj(url, new JSONObject());
            Log.d("hx------------------->", "resultJson= "+resultJson.toString());
            reuslt = PublicPost.checkResult(resultJson);
            if (null != reuslt) {
                return;
            }
            JSONArray jsonArray = resultJson.optJSONArray("result");
            if (jsonArray.length() <= 0) {
                return;
            }

            for(int i =0;i<jsonArray.length();i++){
                try {
                    ArrayList<Obj_Menu> menus=new ArrayList<>();
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String ID = obj.optString("FC_ID");
                    String FC_NAME =obj.optString("FC_NAME");
                    JSONArray menuArray = obj.getJSONArray("FC_MENUS");
                        for(int j =0;j<menuArray.length();j++) {
                            JSONObject menuJson = menuArray.getJSONObject(j);
                            Obj_Menu obj_Menu = new Obj_Menu();
                            obj_Menu.ID = ID;
                            obj_Menu.menuGUID = menuJson.optString("bTID");
                            //menuType 现阶段只支持一级菜单
                            obj_Menu.menuType = 1;
                            obj_Menu.menuName = menuJson.optString("bTName");
                            obj_Menu.menuUrlType = menuJson.optInt("mobileUrlType");
                            obj_Menu.menuUrl = menuJson.optString("mobileUrl");
                            Boolean isUserTitle = menuJson.optBoolean("is_display_banner",true);
                            if(!isUserTitle){
                                obj_Menu.isShowTitle = 0;
                            }else{
                                obj_Menu.isShowTitle=1;
                            }
                            obj_Menu.sourceUrl = menuJson.optString("source_url");
                            menus.add(obj_Menu);
                    }

                    DBDeal(ID,menus);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d("hx------------------->", "需要下载的个数. " + needDownMenuList.size());
            syncGetHeadThu.startPublicIdIcon(needDownMenuList);

        }

    }


    private void DBDeal(String publicGUID,ArrayList<Obj_Menu> arrayList) {
        SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
        //先对比和服务器的图片ID是否一样，如果不一样，就把图片直接删除，如果一样，就不删除
        String col = Table_PublicMenu.PAM_menuGUID + "," + Table_PublicMenu.PAM_menuPicId;
        String sql = "select " + col + " from " + Table_PublicMenu._TABLE_NAME + " where " + Table_PublicMenu.PAM_ID + "=" + "'" + publicGUID + "'";
        Cursor cursor = sqliteHelp.getWritableDatabase().rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                StringBuffer sb = new StringBuffer();
                String menuid = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuGUID));
                String picid = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuPicId));
                if (arrayList.size() > 0) {
                    for (Obj_Menu firstmenu : arrayList) {
                        String path = BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + firstmenu.menuGUID;
                        if (!new File(path).exists()) {
                            //图片本地不存在
                            PicidPublicidBean picidPublicidBean = new PicidPublicidBean();
                            picidPublicidBean.publicid = firstmenu.menuGUID;
                            picidPublicidBean.picid = firstmenu.menuPicUrl;
                            needDownMenuList.add(picidPublicidBean);
                        }
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
            }
        }
        // 清空该公众号对应的菜单表
        SQLite.del(sqliteHelp.getWritableDatabase(), Table_PublicMenu._TABLE_NAME, "ID=?", new String[]{publicGUID});
        ArrayList<ContentValues> contentValues = new ArrayList<>();
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
            cv.put(Table_PublicMenu.PAM_menuUrlType, arrayList.get(i).menuUrlType);
            cv.put(Table_PublicMenu.PAC_urlParam, arrayList.get(i).sourceUrl);
            cv.put(Table_PublicMenu.PAM_ISSHowTitle, arrayList.get(i).isShowTitle);
            contentValues.add(cv);
        }
        SQLite.inserts(sqliteHelp.getWritableDatabase(),
                Table_PublicMenu._TABLE_NAME, contentValues);

        // db.close();
    }

}
