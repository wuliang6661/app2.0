package synway.module_publicaccount.publiclist.GetHttpData;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import qyc.library.tool.http.HttpHead;
import synway.module_publicaccount.public_chat.Obj_PaConfigMsg;
import synway.module_publicaccount.public_list_new.adapter.PublicGridItem;
import synway.module_publicaccount.public_list_new.adapter.UrlObj;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;
import synway.module_publicaccount.publiclist.PinYinDeal;
import synway.module_publicaccount.until.DbUntil;
import synway.module_publicaccount.until.NetUrlUntil;

/**
 * Created by ysm on 2019/1/9.
 * 通过金辉的业务号服务拉取
 */

public class GetPublicList2 {
    public static 	String geturl(String ip,int  port,String userID){
        return  	HttpHead.urlHead(ip, port)+"publicFunc/getAuthorizedFuncList?userId="+userID;
    }
    public static  JSONObject getJson(String userID){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static ArrayList<PublicGridItem>  getPublicList2(JSONArray jsonArray , SQLiteDatabase db){

        ArrayList<Obj_PublicAccount> resultList  = new ArrayList<Obj_PublicAccount>();
        ArrayList<Obj_PaConfigMsg> resultList1  = new ArrayList<Obj_PaConfigMsg>();
        ArrayList<PublicGridItem> gridItems = new ArrayList<>();
        int length = jsonArray.length();
        for(int i = 0; i < length; i++){
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            if(null == jsonObject){
                continue;
            }
            //处理公众号配置表
            Obj_PaConfigMsg obj_paConfigMsg=new Obj_PaConfigMsg();
            obj_paConfigMsg.ID=jsonObject.optString("id");
            try {
                obj_paConfigMsg.sourceUrl = jsonObject.optString("source_url");
            }catch (Exception e){
                obj_paConfigMsg.sourceUrl ="";
            }
            resultList1.add(obj_paConfigMsg);
            Obj_PublicAccount obj = new Obj_PublicAccount();
            obj.ID = jsonObject.optString("id");
            obj.name = jsonObject.optString("fCName");
            obj.namePinYin = PinYinDeal.getPinYin2(obj.name);
            obj.fc_mobilepic= NetUrlUntil.getUrlId(jsonObject.optString("mobilePic"));
            obj.fatherGroupName = jsonObject.optString("belong_group_name");
            obj.fatherGroupID = jsonObject.optString("belong_to_group");
            obj.pushMsgTypeList = jsonObject.optString("push_msg_type");
            String state = jsonObject.optString("jump_type");
            if(state.equals("click")){
                obj.type = 2;
                UrlObj urlObj= new UrlObj();
                urlObj.urlType = jsonObject.optInt("business_type");
                urlObj.publicUrl=jsonObject.optString("business_url");
                urlObj.urlParam = jsonObject.optString("source_url");
                Boolean isUseLocal = jsonObject.optBoolean("is_display_banner",true);
                if(!isUseLocal){
                    urlObj.isShowTitle =0;
                }else {
                    urlObj.isShowTitle =1;
                }
                obj.urlObj = urlObj;
            }else if(state.equals("empty")){
                obj.type=3;
            }else{
                obj.type=0;
            }

            resultList.add(obj);

            PublicGridItem item = new PublicGridItem();
            item.id = obj.ID;
            item.name = obj.name;
            item.type = obj.type;
            item.mobilePic = obj.fc_mobilepic;
            if (item.type == 1) {
                item.app_information = obj.app_information;
            }else if(item.type==2){
                item.urlObj = new UrlObj();
                item.urlObj.urlType = obj.urlObj.urlType;
                item.urlObj.publicUrl = obj.urlObj.publicUrl ;
                item.urlObj.urlParam = obj.urlObj.urlParam;
                item.urlObj.isShowTitle = obj.urlObj.isShowTitle;
            }
            item.fatherGroupID = obj.fatherGroupID;
            item.fatherGroupName = obj.fatherGroupName;
            gridItems.add(item);
        }

        DbUntil.DBDeal(resultList,resultList1,db);//处理公众帐号表
        DbUntil.DBDealPublicMenu(resultList,db);//处理菜单表,删除没有公众号的菜单

        return  gridItems;
    }

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
//            obj.fatherGroupName = jsonObject.optString("belong_group_name");
//            obj.fatherGroupID = jsonObject.optString("belong_to_group");
//
//            obj.pushMsgTypeList = jsonObject.optString("push_msg_type");
//            String state = jsonObject.optString("jump_type");
//            if(state.equals("click")){
//                obj.type = 2;
//                UrlObj urlObj= new UrlObj();
//                urlObj.urlType = jsonObject.optInt("business_type");
//                urlObj.publicUrl=jsonObject.optString("business_url");
//                urlObj.sourceUrl = jsonObject.optString("source_url");
//                String isUseLocal = jsonObject.optString("is_display_banner");
//                if(isUseLocal.equals("off")){
//                    urlObj.isShowTitle =0;
//                }else {
//                    urlObj.isShowTitle =1;
//                }
//                obj.urlObj = urlObj;
//            }else if(state.equals("empty")){
//                obj.type=3;
//            }else{
//                obj.type=0;
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

}
