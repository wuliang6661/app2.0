//package synway.module_publicaccount.publiclist.GetHttpData;
//
//import android.database.sqlite.SQLiteDatabase;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Locale;
//
//import qyc.library.tool.http.HttpHead;
//import synway.module_interface.config.BaseUtil;
//import synway.module_publicaccount.public_chat.Obj_PaConfigMsg;
//import synway.module_publicaccount.public_list_new.adapter.PublicGridItem;
//import synway.module_publicaccount.publiclist.Obj_PublicAccount;
//import synway.module_publicaccount.publiclist.PinYinDeal;
//import synway.module_publicaccount.publiclist.SortAccountComparator;
//import synway.module_publicaccount.until.DbUntil;
//import synway.module_publicaccount.until.NetUrlUntil;
//
//import static synway.module_publicaccount.publiclist.bean.App_InformationBean.getAppInforMation;
//
///**
// * Created by admin on 2017/2/8.
// */
//
//public class GetPublicList {
//    public static 	String geturl(String ip,int  port){
//        return  	HttpHead.urlHead(ip, port)+"/publicFunc/getAuthorizedFuncList";
////        return  	HttpHead.urlHead(ip, port)+"PFService/PublicFunction/GetFunctionList.osc";
//    }
//    public static HashMap<String,Object>  getPublicList(JSONArray jsonArray ,ArrayList<String> idList,SQLiteDatabase db){
//        HashMap<String,Object> publicList=new HashMap<String,Object>();
//        HashMap<String, Integer> selePos = null;
//        // 存放含有索引字母的位置
//        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
//        ArrayList<Obj_PublicAccount>  AllList = new ArrayList<Obj_PublicAccount>();
//        ArrayList<Obj_PublicAccount> resultList  = new ArrayList<Obj_PublicAccount>();
//        ArrayList<Obj_PaConfigMsg> resultList1  = new ArrayList<Obj_PaConfigMsg>();
//        int length = jsonArray.length();
//        ArrayList<Obj_PublicAccount> appList=new ArrayList<>();
//        for(int i = 0; i < length; i++){
//            JSONObject jsonObject = jsonArray.optJSONObject(i);
//            if(null == jsonObject){
//                continue;
//            }
//            //处理公众号配置表
//            Obj_PaConfigMsg obj_paConfigMsg=new Obj_PaConfigMsg();
//            obj_paConfigMsg.ID=jsonObject.optString("id");
//            try {
//                obj_paConfigMsg.sourceUrl = jsonObject.optString("source_url");
//            }catch (Exception e){
//                obj_paConfigMsg.sourceUrl ="";
//            }
//            resultList1.add(obj_paConfigMsg);
//            Obj_PublicAccount obj = new Obj_PublicAccount();
//            obj.ID = jsonObject.optString("id");
//            obj.name = jsonObject.optString("fcName");
////            obj.company = jsonObject.optString("FC_COMPANY");
////            obj.contact = jsonObject.optString("FC_CONTCT");
////            obj.contactTel = jsonObject.optString("FC_TEL");
//            obj.namePinYin = PinYinDeal.getPinYin2(obj.name);
//            obj.fc_mobilepic= NetUrlUntil.getUrlId(jsonObject.optString("mobilePic"));
//            String state = jsonObject.optString("inner_menu");
//            if(state.equals("off")){
//                obj.type = 2;
//                obj.publicUrlType = jsonObject.optInt("business_type");
//                obj.publicUrl = jsonObject.optString("business_url");
//                obj.sourceParam = jsonObject.optString("source_url");
//            }else{
//                obj.type=0;
//            }
//            if(obj.type==1) {//是APP式应用
//                obj.app_information = getAppInforMation(jsonObject.optString("APP_INFORMATION"));
//            }
//            String firstZiMu = obj.namePinYin.charAt(0) + "";
//            if (!hashMap.containsKey(firstZiMu)) {
//                hashMap.put(firstZiMu, i);
//                Obj_PublicAccount obj2 = new Obj_PublicAccount();
//                obj2.isItem = false;
//                obj2.name = firstZiMu.toUpperCase(Locale.CHINA);
//                obj2.namePinYin = firstZiMu.toUpperCase(Locale.CHINA);
//                AllList.add(obj2);
//            }
//            AllList.add(obj);
//
//            resultList.add(obj);
//
//            File file = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + obj.ID);
//            if(!file.exists()){
//                idList.add(obj.ID);
//            }
//        }
//
//        DbUntil.DBDeal(resultList,resultList1,db);//处理公众帐号表
//        DbUntil.DBDealPublicMenu(resultList,db);//处理菜单表,删除没有公众号的菜单
//        Collections.sort(AllList, new SortAccountComparator());
//        selePos = new HashMap<String, Integer>();
//        selePos.put("#", 0);
//        for(int i = 0; i < AllList.size(); i++){
//            if(AllList.get(i).namePinYin.charAt(0) >= 'A' && AllList.get(i).namePinYin.charAt(0) <= 'Z'){
//                selePos.put(AllList.get(i).namePinYin, i);
//            }
//        }
//        publicList.put("publiclist",resultList);
//        publicList.put("alllist",AllList);
//        publicList.put("selepos",selePos);
//        publicList.put("applist",appList);
//        return  publicList;
//    }
//    public static  JSONObject getJson(String userID){
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("userId", userID);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }
//
//
//    public static ArrayList<PublicGridItem>  getPublicList2(JSONArray jsonArray , SQLiteDatabase db){
//
//        ArrayList<Obj_PublicAccount> resultList  = new ArrayList<Obj_PublicAccount>();
//        ArrayList<Obj_PaConfigMsg> resultList1  = new ArrayList<Obj_PaConfigMsg>();
//        ArrayList<PublicGridItem> gridItems = new ArrayList<>();
//        int length = jsonArray.length();
//        for(int i = 0; i < length; i++){
//            JSONObject jsonObject = jsonArray.optJSONObject(i);
//            if(null == jsonObject){
//                continue;
//            }
//            //处理公众号配置表
//            Obj_PaConfigMsg obj_paConfigMsg=new Obj_PaConfigMsg();
//            obj_paConfigMsg.ID=jsonObject.optString("id");
//            try {
//                obj_paConfigMsg.sourceUrl = jsonObject.optString("source_url");
//            }catch (Exception e){
//                obj_paConfigMsg.sourceUrl ="";
//            }
//            resultList1.add(obj_paConfigMsg);
//            Obj_PublicAccount obj = new Obj_PublicAccount();
//            obj.ID = jsonObject.optString("id");
//            obj.name = jsonObject.optString("fcName");
////            obj.company = jsonObject.optString("FC_COMPANY");
////            obj.contact = jsonObject.optString("FC_CONTCT");
////            obj.contactTel = jsonObject.optString("FC_TEL");
//            obj.namePinYin = PinYinDeal.getPinYin2(obj.name);
//            obj.fc_mobilepic= NetUrlUntil.getUrlId(jsonObject.optString("mobilePic"));
//            String state = jsonObject.optString("inner_menu");
//            if(state.equals("off")){
//                obj.type = 2;
//                obj.publicUrlType = jsonObject.optInt("business_type");
//                obj.publicUrl = jsonObject.optString("business_url");
//                obj.sourceParam = jsonObject.optString("source_url");
//            }else{
//                obj.type=0;
//            }
//            if(obj.type==1) {//是APP式应用
//                obj.app_information = getAppInforMation(jsonObject.optString("APP_INFORMATION"));
//            }else if(obj.type == 2){
//                //weex应用
//            }
//
//            resultList.add(obj);
//
//            PublicGridItem item = new PublicGridItem();
//            item.id = obj.ID;
//            item.name = obj.name;
//            item.type = obj.type;
//            item.mobilePic = obj.fc_mobilepic;
//            if (item.type == 1) {
//                item.app_information = obj.app_information;
//            }
//            gridItems.add(item);
//        }
//
//        DbUntil.DBDeal(resultList,resultList1,db);//处理公众帐号表
//        DbUntil.DBDealPublicMenu(resultList,db);//处理菜单表,删除没有公众号的菜单
//
//        return  gridItems;
//    }
//
//}
