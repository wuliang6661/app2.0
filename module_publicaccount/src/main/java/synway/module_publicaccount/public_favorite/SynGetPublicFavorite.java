package synway.module_publicaccount.public_favorite;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import synway.module_publicaccount.Main;
import synway.module_publicaccount.customconfig.CustomConfigRW;
import synway.module_interface.config.CustomConfigType;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.until.StringUtil;

import static synway.module_publicaccount.public_favorite.SynUploadAndSavePublicFav.AppInformation;
import static synway.module_publicaccount.public_favorite.SynUploadAndSavePublicFav.SynPublictype;
import static synway.module_publicaccount.publiclist.bean.App_InformationBean.getAppInforMation;

/**
 * Created by quintet on 2016/10/15.
 *
 * 获取本地数据库获取常用应用的信息。
 */
public class SynGetPublicFavorite {
    public static final String ID = "ID";
    public static final String MenuId="MenuId";
    public static final String Name = "Name";
    public static final String menuName = "menuName";
    public static final String menuUrl = "menuUrl";
    public static final String isHtml = "isHtml";
    public static final String PublicFavorite = "PublicFavorite";
    public static final String  accountPicId="accountpicid";
    public List<Obj_PublicAccount_Favorite> favoriteLists = new ArrayList<>();
    private Handler handler = null;


    public SynGetPublicFavorite() {
        handler = new Handler();
    }


    public void start() {
        new Thread(new mRunnable()).start();
    }


    public void stop() {
        if (listener != null) {
            listener = null;
        }
    }

    private class mRunnable implements Runnable {
        @Override public void run() {
            final List<Obj_PublicAccount_Favorite> favoriteList = new ArrayList<>();
            String result = CustomConfigRW.read(CustomConfigType.PUBLICFAVORITE, "SavePublicFavorite");

            if (result == null) {
                handler.post(new Runnable() {
                    @Override public void run() {
                        if (listener != null) {
                            listener.onFail("数据库表里没有数据");
                        }

                    }
                });

            }else if(result.equals("")){

                //result为空字符串这种情况表示常用应用的个数为0个(是用户自己将常用应用都删除光了)
              handler.post(new Runnable() {
                  @Override public void run() {
                      List<Obj_PublicAccount_Favorite> list = new ArrayList<Obj_PublicAccount_Favorite>();
                      listener.onResult(list);

                  }
              });
            } else {
                try {
                    JSONObject root = new JSONObject(result);
                    JSONArray jsonArray = root.getJSONArray(PublicFavorite);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
                        favorite.ID = jsonObject.getString(ID);
                        favorite.MenuId=jsonObject.getString(MenuId);
//                        favorite.isHtml = jsonObject.getBoolean(isHtml);
//                        favorite.fc_mobilepic=jsonObject.getString(accountPicId);
                        favorite.Name = jsonObject.getString(Name);
                        favorite.menuName = jsonObject.getString(menuName);
                        favorite.menuUrl = jsonObject.getString(menuUrl);
                        favorite.type=jsonObject.optInt(SynPublictype);
                        favorite.app_information=getAppInforMation(jsonObject.optString(AppInformation));
                        favoriteList.add(favorite);

                    }
                     favoriteLists=getMenuOrPublicMsg(favoriteList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override public void run() {

                        if (listener != null) {
                            listener.onResult(favoriteLists);
                        }


                    }
                });
            }
        }
    }


    public interface OnSynGetPublicFavoriteListener {
        void onResult(List<Obj_PublicAccount_Favorite> favoriteList);

        void onFail(String reason);
    }


    public void setOnSynGetPublicFavoriteByDBListener(OnSynGetPublicFavoriteListener listener) {
        this.listener = listener;
    }

    private OnSynGetPublicFavoriteListener listener = null;

private  List<Obj_PublicAccount_Favorite>getMenuOrPublicMsg(List<Obj_PublicAccount_Favorite> favoriteList){
     ArrayList<Obj_PublicAccount_Favorite> obj_publicAccount_favorites=new ArrayList<Obj_PublicAccount_Favorite>();
    SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    ArrayList<String> arrayList = new ArrayList<String>();
    for(int i=0;i<favoriteList.size();i++) {
        //为公众号
        if(favoriteList.get(i).menuName.equals("")&&favoriteList.get(i).menuUrl.equals("")) {
            String tableName = Table_PublicAccount._TABLE_NAME;
            String col = Table_PublicAccount.FC_ID + ","
                    + Table_PublicAccount.FC_NAME + ","
                    + Table_PublicAccount.FC_MOBILEPIC+ ","+Table_PublicAccount.FC_TYPE+ ","
                    +Table_PublicAccount.APP_INFORMATION;
            Cursor cursor = sqliteHelp.getWritableDatabase().rawQuery("select " + col + " from " + tableName + "  where " + Table_PublicAccount.FC_ID + "=" + "'" + favoriteList.get(i).ID + "'", null);
            if(cursor.getCount()==0){
                favoriteList.remove(i);
                --i;
            }
            while (cursor.moveToNext()) {
                StringBuffer sb = new StringBuffer();
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_ID)));
                sb.append("|");
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_NAME)));
                sb.append("|");
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_MOBILEPIC)));
                sb.append("|");
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_TYPE)));
                sb.append("|");
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicAccount.APP_INFORMATION)));
                String[] sList = sb.toString().split("\\|", -1);
                favoriteList.get(i).ID = sList[0];
                favoriteList.get(i).Name = sList[1];
                favoriteList.get(i).fc_mobilepic = sList[2];
                if(StringUtil.isNotEmpty(sList[3])) {
                    favoriteList.get(i).type = Integer.parseInt(sList[3]);
                    if (favoriteList.get(i).type == 1) {
                        favoriteList.get(i).app_information = getAppInforMation(sList[4]);
                    }
                }
            }
        }
        //是菜单
        else{
            String menutableName = Table_PublicMenu._TABLE_NAME;
            String menucol = "a." + Table_PublicMenu.PAM_menuGUID + ","
                    +  "a." +Table_PublicMenu.PAM_menuName + ","
                    +  "a." +Table_PublicMenu.PAM_menuUrl+","
                    + "a." +Table_PublicMenu.PAM_menuPicId+ ","
                    +  "a." +Table_PublicMenu.PAM_ID+","+
                    "b."+Table_PublicAccount.FC_NAME+" as publicname";
            Cursor cursor = sqliteHelp.getWritableDatabase().rawQuery(
                    "select " + menucol + " from " + menutableName+" a left join "+Table_PublicAccount._TABLE_NAME+
                            " b on "+"b."+Table_PublicAccount.FC_ID+"="+
                            "a."+Table_PublicMenu.PAM_ID
                    + "  where a." + Table_PublicMenu.PAM_menuGUID + "=" + "'" + favoriteList.get(i).MenuId + "'"
                    , null);
            //在菜单表里找不到该常用菜单，说明被删除了
            if(cursor.getCount()==0){
                favoriteList.remove(i);
                --i;
            }
            while (cursor.moveToNext()) {
                StringBuffer sb = new StringBuffer();
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuGUID )));
                sb.append("|");
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuName )));
                sb.append("|");
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuUrl)));
                sb.append("|");
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuPicId)));
                sb.append("|");
                sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_ID)));
                sb.append("|");
                sb.append(cursor.getString(cursor.getColumnIndex("publicname")));
                String[] sList = sb.toString().split("\\|", -1);
                favoriteList.get(i).MenuId = sList[0];
                favoriteList.get(i).menuName = sList[1];
                favoriteList.get(i).menuUrl = sList[2];
                favoriteList.get(i).fc_mobilepic = sList[3];
                favoriteList.get(i).ID = sList[4];
                favoriteList.get(i).Name = sList[5];
                favoriteList.get(i).isHtml=true;

            }
        }
    }

    return  favoriteList;

}



}
