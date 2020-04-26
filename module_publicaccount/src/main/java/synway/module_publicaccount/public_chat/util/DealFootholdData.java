package synway.module_publicaccount.public_chat.util;

import android.util.Log;

import com.amap.api.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import synway.module_publicaccount.public_chat.util.bean.FootHoldPointBen;

/**
 * Created by ysm on 2017/9/29.
 * 落脚点经纬度列表获取
 */

public class DealFootholdData {
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static HashMap<Character, Integer> CHARSMAP;
    public static String LNG="lng";
    public static String LAT="lat";
    static {
        CHARSMAP = new HashMap<Character, Integer>();
        for (int i = 0; i < CHARS.length; i++) {
            CHARSMAP.put(CHARS[i], i);
        }
    }

    /***
     * 获取web页面中得到的落脚点数据中的地图点
     * @param json 页面中得到的原始落脚点数据
     * @return 落脚点经纬度列表
     */
    public static ArrayList<FootHoldPointBen> GetFootHoldPoints(String json) {
        ArrayList<FootHoldPointBen> latLngs = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                Log.i("testy", "得到的反编译前经纬度" + jsonObject1.get("region"));
//                String firstSecond=getGeoHashBinaryString(jsonObject1.get("region").toString());
              FootHoldPointBen footHoldPointBen=getGeoHashBinaryString(jsonObject1.get("region").toString(),jsonObject1.get("total_duration").toString());
//                double[] lng=lnglat.get(LNG);
//                double[] lat=lnglat.get(LAT);
//                Log.i("testy","纬度范围"+lat[0]+"~"+lat[1]);
//                Log.i("testy","经度范围"+lng[0]+"~"+lng[1]);
                latLngs.add(footHoldPointBen);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return latLngs;
    }
    private static double[] getlngS(String lat){
        double[] lats=new double[2];
        //获取经度范围
        double maxX = 180;
        double minX = -180;
        double s;
        for (int i = 0; i < lat.length(); i++) {
            s= (maxX - minX) / 2;
            if (lat.substring(i, i+1).equals("0")) {
                maxX = minX + s;
            }else{
                minX = maxX - s;
            }
        }
        lats[0]=minX;
        lats[1]=maxX;
        return  lats;
    }
    private static double[] getlatS(String lng){
        double[] lngs=new double[2];
        //获取经度范围
        double maxX = 90;
        double minX = -90;
        double s;
        for (int i = 0; i < lng.length(); i++) {
            s= (maxX - minX) / 2;
            if (lng.substring(i, i+1).equals("0")) {
                maxX = minX + s;
            }else{
                minX = maxX - s;
            }
        }
        lngs[0]=minX;
        lngs[1]=maxX;
        return  lngs;
    }



    private static FootHoldPointBen getGeoHashBinaryString(String geoHash,String total_duration) {
        FootHoldPointBen footHoldPointBen=new FootHoldPointBen();
        if (geoHash == null || "".equals(geoHash)) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < geoHash.length(); i++) {
            char c = geoHash.charAt(i);
            if (CHARSMAP.containsKey(c)) {
                String cStr = getBase32BinaryString(CHARSMAP.get(c));
                if (cStr != null) {
                    sb.append(cStr);
                }
            }
        }
        String[] latlng=getLatLng(sb.toString());
        double[] lngs=getlngS(latlng[0]);
        double[] lats=getlatS(latlng[1]);
        footHoldPointBen.setMinlng(lngs[0]);
        footHoldPointBen.setMaxlng(lngs[1]);
        footHoldPointBen.setMinlat(lats[0]);
        footHoldPointBen.setMaxlat(lats[1]);
        footHoldPointBen.setTotal_duration(total_duration);
        return footHoldPointBen;
    }
    //拆分字符串 获得经纬度各自的2进制字符串
    private static String[] getLatLng(String code) {
        String[] latlng=new String[2];
        String lat = "", lng = "";
        for (int i = 0; i < code.length(); i++) {
            if (i % 2 == 0) {
                lng += code.charAt(i);
            } else {
                lat += code.charAt(i);
            }
        }
        latlng[0]=lng;
        latlng[1]=lat;
        return latlng;
    }

    private static String getBase32BinaryString(int i) {
        if (i < 0 || i > 31) {
            return null;
        }
        String str = Integer.toBinaryString(i + 32);
        return str.substring(1);
    }

}
