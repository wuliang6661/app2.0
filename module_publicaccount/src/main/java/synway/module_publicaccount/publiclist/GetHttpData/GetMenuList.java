package synway.module_publicaccount.publiclist.GetHttpData;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import qyc.library.tool.http.HttpHead;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;
import synway.module_publicaccount.until.NetUrlUntil;

/**
 * Created by ysm on 2017/2/8.
 */

public class  GetMenuList {
    public static 	String geturl(String ip,int  port){
//        return  	 HttpHead.urlHead(ip, port)+ "PFService/PublicFunction/GetMenuInfo.osc";
        return  	 HttpHead.urlHead(ip, port)+ "/publicFunc/getMenuInfo";
    }
    public static ArrayList<String> getidList(ArrayList<Obj_PublicAccount> arrayList){
        ArrayList<String> idList = new ArrayList<String>();
        for (int k = 0; k < arrayList.size(); k++) {
            if (arrayList.get(k).ID != null) {
                idList.add(arrayList.get(k).ID);
            }
        }
        return  idList;
    }
    public static JSONObject getJson(String strID, ArrayList<String> arrayList) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("USER_ID", strID);

            JSONArray jsonArray = new JSONArray();
            JSONObject objItem = null;
            for (int i = 0; i < arrayList.size(); i++) {
                objItem = new JSONObject();
                objItem.put("FC_ID", arrayList.get(i));
                jsonArray.put(objItem);
            }

            jsonObject.put("FC_IDS", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static HashMap<String,ArrayList<Obj_Menu>> getMenu(JSONArray jsonArray){
        HashMap<String,ArrayList<Obj_Menu>> menu=new HashMap<String,ArrayList<Obj_Menu>>();
         ArrayList<Obj_Menu> firstMenu = new ArrayList<Obj_Menu>();
        ArrayList<Obj_Menu> secondMenu=new ArrayList<Obj_Menu>();
        if(jsonArray!=null&&jsonArray.length()!=0) {
            for (int z = 0; z < jsonArray.length(); z++) {
                JSONObject obj = jsonArray.optJSONObject(z);
                Log.i("testy", "得到的长度是" + jsonArray.length());
                JSONArray objInfoList = obj.optJSONArray("MENU_INFO");
                for (int k = 0; k < objInfoList.length(); k++) {
                    JSONObject objInfo = objInfoList.optJSONObject(k);
                    Obj_Menu obj_Menu = new Obj_Menu();
                    obj_Menu.ID = obj.optString("FC_ID");
                    obj_Menu.menuType = objInfo.optInt("MENU_TYPE");
                    obj_Menu.menuUrl = objInfo.optString("MENU_URL");
                    try {
                        obj_Menu.menuUrlType = objInfo.optInt("MENU_URL_TYPE",0);
                    }catch (Exception e){
                        obj_Menu.menuUrlType =0;
                    }
                    obj_Menu.menuName = objInfo.optString("MENU_NAME");
                    obj_Menu.menuKey = objInfo.optString("MENU_KEY");
                    obj_Menu.menuGUID = objInfo.optString("MENU_GUID");
                    obj_Menu.menuFather = objInfo.optString("MENU_FATHER");
                    if (objInfo.optString("MENU_PICID") != null && !objInfo.optString("MENU_PICID").equals("")) {
                        obj_Menu.menuPicUrl = NetUrlUntil.getUrlId(objInfo.optString("MENU_PICID"));
                    }
                    if (obj_Menu.menuFather == null || obj_Menu.menuFather.equals("")) {
                        firstMenu.add(obj_Menu);
                    } else {
                        secondMenu.add(obj_Menu);
                    }
                }
            }
        }
        menu.put("firstmenu",firstMenu);
        menu.put("secondmenu",secondMenu);
        return  menu;
    }

}
