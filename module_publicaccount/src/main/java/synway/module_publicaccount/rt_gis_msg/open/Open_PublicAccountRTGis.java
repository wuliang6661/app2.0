package synway.module_publicaccount.rt_gis_msg.open;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import synway.module_interface.db.table_util.Table_RTGISPoints;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.rt_gis_msg.Obj_RTGis_Point;

/**
 * Created by 13itch on 2016/7/15.
 */
public class Open_PublicAccountRTGis {
    public final static long shuaxinjiangeTime = 10;
    public final static long duqujiangeTime = 10 * 60;
    public final static String ACTION_GIS_POINTS = "rt_gis_points";
    public final static String EX_PUBLIC_GUID = "public_guid";
    public final static String EX_GIS_MSG_INFO = "msg_info";
    public final static String EX_TIME = "time";

    public static void dealRTGIS(Context context, JSONObject MSG_INFO, String time, String publicGUID, SQLiteOpenHelper sqlIteHelp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = sqlIteHelp.getWritableDatabase();
        long d = date.getTime();
        try {
            db.beginTransaction();
            String sql = "delete from " + Table_RTGISPoints.table_name + " where " + Table_RTGISPoints.public_guid + "='" + publicGUID + "' and " + Table_RTGISPoints.time + "<" + (d / 1000 - duqujiangeTime);
            db.execSQL(sql);
            sql = "insert into " + Table_RTGISPoints.table_name + " (PublicGUID,MsgInfo,Time) values ('" + publicGUID + "','" + MSG_INFO.toString() + "'," + (d / 1000) + ")";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_GIS_POINTS);
        intent.putExtra(EX_PUBLIC_GUID, publicGUID);
        intent.putExtra(EX_TIME, d);
        intent.putExtra(EX_GIS_MSG_INFO, MSG_INFO.toString());
        context.sendBroadcast(intent);
    }

    /**
     * 读取本地缓存，一般是新进入activity时调用
     *
     * @param publicGUID
     * @return
     */
    public static List<Obj_RTGis_Point> getPointsByDB(String publicGUID) {
        HashMap<String, Obj_RTGis_Point> hashMap = new HashMap<>();
        long msgTime = 0;
        List<Obj_RTGis_Point> list = new ArrayList<>();
        SQLiteDatabase db = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase();
        long d = new Date().getTime() / 1000;
        String sql = "select * from " + Table_RTGISPoints.table_name + " where " + Table_RTGISPoints.public_guid + " ='" + publicGUID + "' and Time>" + (d - duqujiangeTime);
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String msginfo = cursor.getString(cursor.getColumnIndex("MsgInfo"));
            msgTime = cursor.getLong(cursor.getColumnIndex("Time"));
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(msginfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonObject != null) {
                JSONArray jsonArray = jsonObject.optJSONArray("Points");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Obj_RTGis_Point obj_rtGis_point = new Obj_RTGis_Point();
                    JSONObject jsonObject2 = jsonArray.optJSONObject(i);
                    int type = jsonObject2.optInt("Type");
                    if (type == 0) {
                        continue;
                    }
                    obj_rtGis_point.PublicGUID = publicGUID;
                    obj_rtGis_point.ID = jsonObject2.optString("ID");
                    obj_rtGis_point.name = jsonObject2.optString("Name");
                    obj_rtGis_point.X = jsonObject2.optString("X");
                    obj_rtGis_point.Y = jsonObject2.optString("Y");
                    obj_rtGis_point.tip = jsonObject2.optString("Tip");
                    obj_rtGis_point.time = msgTime + "";
                    if (hashMap.get(obj_rtGis_point.ID) == null) {
                        list.add(obj_rtGis_point);
                        hashMap.put(obj_rtGis_point.ID, obj_rtGis_point);
                    } else {
                        Obj_RTGis_Point oldPoint = hashMap.get(obj_rtGis_point.ID);
                        if (Long.parseLong(obj_rtGis_point.time) >= Long.parseLong(oldPoint.time)) {
                            oldPoint.time = obj_rtGis_point.time;
                            oldPoint.X = obj_rtGis_point.X;
                            oldPoint.Y = obj_rtGis_point.Y;
                            oldPoint.name = obj_rtGis_point.name;
                            oldPoint.tip = obj_rtGis_point.tip;
                        }
                    }
                }
            }
        }
        cursor.close();
        return list;
    }

    public static List<Obj_RTGis_Point> parseDataFromString(String msgInfo, String publicGUID, String time) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(msgInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<Obj_RTGis_Point> points_list = new ArrayList<>();
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.optJSONArray("Points");
            for (int i = 0; i < jsonArray.length(); i++) {
                Obj_RTGis_Point obj_rtGis_point = new Obj_RTGis_Point();
                JSONObject jsonObject2 = jsonArray.optJSONObject(i);
                obj_rtGis_point.type = jsonObject2.optInt("Type");
                obj_rtGis_point.PublicGUID = publicGUID;
                obj_rtGis_point.ID = jsonObject2.optString("ID");
                obj_rtGis_point.name = jsonObject2.optString("Name");
                obj_rtGis_point.X = jsonObject2.optString("X");
                obj_rtGis_point.Y = jsonObject2.optString("Y");
                obj_rtGis_point.tip = jsonObject2.optString("Tip");
                obj_rtGis_point.time = time;
                points_list.add(obj_rtGis_point);
            }
        }
        return points_list;
    }

}

//        JSONArray jsonArray = MSG_INFO.optJSONArray("Points");
//        List<Obj_RTGis_Point> pointLIst = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            Obj_RTGis_Point obj_rtGis_point = new Obj_RTGis_Point();
//            JSONObject jsonObject = jsonArray.optJSONObject(i);
//            obj_rtGis_point.PublicGUID=publicGUID;
//            obj_rtGis_point.ID = jsonObject.optString("ID");
//            obj_rtGis_point.name = jsonObject.optString("Name");
//            obj_rtGis_point.X = jsonObject.optString("X");
//            obj_rtGis_point.Y = jsonObject.optString("Y");
//            obj_rtGis_point.tip = jsonObject.optString("Tip");
//            obj_rtGis_point.time = jsonObject.optString("Time");
//            pointLIst.add(obj_rtGis_point);
//        }
//        SQLiteDatabase db = MainApp.getInstance().sqlIteHelp.getWritableDatabase();
//        savePoints(pointLIst, db);


//    //删除原来的数据，再重新插入现在的数据
//    private static boolean savePoints(List<Obj_RTGis_Point> pointLIst, SQLiteDatabase db) {
//        boolean isSuccess=true;
//        try {
//            db.beginTransaction();
//            if(pointLIst.size()>0){
//                String sql="delete from "+ Table_RTGISPoints.table_name +" where PublicGUID='"+pointLIst.get(0).PublicGUID+"'";
//                db.execSQL(sql);
//            }
//            for (Obj_RTGis_Point obj_rtGis_point : pointLIst) {
//                String sql = "insert into " + Table_RTGISPoints.table_name + " (PublicGUID,TargetID,Name,X,Y,Tip,Time) values ('"+obj_rtGis_point.PublicGUID+ "','"+ obj_rtGis_point.ID + "','" + obj_rtGis_point.name + "','" + obj_rtGis_point.X + "','" + obj_rtGis_point.Y + "','" + obj_rtGis_point.tip + "','" + obj_rtGis_point.time + "')";
//                db.execSQL(sql);
//            }
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            isSuccess=false;
//        } finally {
//            db.endTransaction();
//        }
//        return isSuccess;
//    }


