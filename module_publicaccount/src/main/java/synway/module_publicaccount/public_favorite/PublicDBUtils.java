package synway.module_publicaccount.public_favorite;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import synway.module_publicaccount.customconfig.CustomConfigRW;
import synway.module_interface.config.CustomConfigType;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
/**
 * Created by quintet on 2016/10/14.
 */
public class PublicDBUtils {

    public static final String IP = "172.16.1.129";
    public static final int Port = 7000;

    public static final String key = "SavePublicFavorite";
    public static final String MenuId="MenuId";
    public static final String ID = "ID";
    public static final String Name = "Name";
    public static final String menuName = "menuName";
    public static final String menuUrl = "menuUrl";
    public static final String isHtml = "isHtml";
    public static final String PublicFavorite = "PublicFavorite";


    /**
     *
     * @param obj_publicFavorite
     * @param loginUserID
     * @return String值，若是为""，说明上传并存储数据库成功，若不为""，说明上传并存储数据库失败.
     */
    public static String save(Obj_PublicAccount_Favorite obj_publicFavorite, String loginUserID) {
        String jsonToString = "";
        String returnResult = "";
        String result = CustomConfigRW.read(CustomConfigType.PUBLICFAVORITE, key);
        if (result == null) {

            JSONObject root = new JSONObject();

            JSONArray array = new JSONArray();



            try {

                JSONObject object = new JSONObject();
                object.put(ID, obj_publicFavorite.ID);
                object.put(Name, obj_publicFavorite.Name);
                object.put(menuName, obj_publicFavorite.menuName);
                object.put(menuUrl, obj_publicFavorite.menuUrl);
                object.put(isHtml, obj_publicFavorite.isHtml);

                array.put(object);

                root.put(PublicFavorite, array);

                jsonToString = root.toString();
                Log.e("dym", "jsonToString= " + jsonToString);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Future<String> future = CustomConfigRW.write(IP, Port, loginUserID,
                CustomConfigType.PUBLICFAVORITE, key,
                jsonToString);

            try {
                returnResult = future.get();


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {

            try {
                JSONObject root = new JSONObject(result);

                Log.e("dym", "root= " + root);
                JSONArray array = root.getJSONArray(PublicFavorite);

                JSONObject newObject = new JSONObject();
                newObject.put(ID, obj_publicFavorite.ID);
                newObject.put(Name, obj_publicFavorite.Name);
                newObject.put(menuName, obj_publicFavorite.menuName);
                newObject.put(menuUrl, obj_publicFavorite.menuUrl);
                newObject.put(isHtml, obj_publicFavorite.isHtml);

                array.put(newObject);

                root.put(PublicFavorite, array);

                jsonToString = root.toString();
                Log.e("dym", "jsonToString= " + jsonToString);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Future<String> future = CustomConfigRW.write(IP, Port, loginUserID,
                CustomConfigType.PUBLICFAVORITE, key,
                jsonToString);

            try {
                returnResult = future.get();


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        return returnResult;
    }

    /**
     * 保存
     * @param favoriteList
     * @param userID
     * @return String值，若是为""，说明上传并存储数据库成功，若不为""，说明上传并存储数据库失败.
     */
    public static String saveList(List<Obj_PublicAccount_Favorite> favoriteList,String userID) {
        String returnResult = "";
        String jsonToString = "";

        JSONObject root = new JSONObject();

        JSONArray array = new JSONArray();

        for (int i = 0; i < favoriteList.size(); i++) {
            try {

                JSONObject object = new JSONObject();
                object.put(ID, favoriteList.get(i).ID);
                object.put(Name, favoriteList.get(i).Name);
                object.put(menuName, favoriteList.get(i).menuName);
                object.put(menuUrl, favoriteList.get(i).menuUrl);
                object.put(isHtml, favoriteList.get(i).isHtml);

                array.put(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            root.put(PublicFavorite, array);

            jsonToString = root.toString();

            Log.e("dym", "jsonToString= " + jsonToString);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Future<String> future = CustomConfigRW.write(IP, Port, userID,
            CustomConfigType.PUBLICFAVORITE, key,
            jsonToString);

        try {
            returnResult = future.get();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return returnResult;
    }


    /**
     *
     * @return 有则返回List(注意如果记录为空字符串,表明常用应用为0个,则返回一个无数据的List),没有则返回null
     */
    public static List<Obj_PublicAccount_Favorite> read() {
        List<Obj_PublicAccount_Favorite> favoriteList = new ArrayList<>();

        String result = CustomConfigRW.read(CustomConfigType.PUBLICFAVORITE, key);

        if (result == null) {
            return null;
        }else if(result.equals("")){
          return favoriteList;
        } else {
            try {
                JSONObject root = new JSONObject(result);
                JSONArray jsonArray = root.getJSONArray(PublicFavorite);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();

                    favorite.ID = jsonObject.getString(ID);
                    favorite.Name = jsonObject.getString(Name);
                    favorite.menuName = jsonObject.getString(menuName);
                    favorite.menuUrl = jsonObject.getString(menuUrl);
                    favorite.isHtml = jsonObject.getBoolean(isHtml);
                    favorite.MenuId=jsonObject.getString(MenuId);
                    favoriteList.add(favorite);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return favoriteList;
        }


    }




}
