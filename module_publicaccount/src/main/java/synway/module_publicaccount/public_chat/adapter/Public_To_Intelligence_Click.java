package synway.module_publicaccount.public_chat.adapter;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import synway.module_interface.chatinterface.obj.ChatMsgObj;
import synway.module_interface.chatinterface.obj.ChatPublicLocationObj;
import synway.module_interface.chatinterface.obj.ChatPublicUrlObj;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgPicTxt;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewBase;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewTxt;
import synway.module_publicaccount.until.DialogTool;
import synway.module_publicaccount.until.StringUtil;

/**
 * Created by ysm on 2017/6/13.
 */

public class Public_To_Intelligence_Click {
    public static void locationChangeIntellgence(Obj_PublicMsgPicTxt obj_publicMsgPicTxt, Activity activity, String publicGuid, String selectTitle) {
        ChatPublicLocationObj chatLocationObj = new ChatPublicLocationObj();
        String description = "";
        String title = "";
        JSONArray jsonArray = new JSONArray();
        try {
            for (Obj_ViewBase obj_viewBase : obj_publicMsgPicTxt.dataLines) {
                if (obj_viewBase instanceof Obj_ViewTxt) {
                    Obj_ViewTxt obj_viewTxt = (Obj_ViewTxt) obj_viewBase;
                    PublicAccountURL publicAccountURL = PublicAccountURLFactory.getPublicAccountURL(obj_viewTxt.url);
                    if (publicAccountURL.getUrlType() == PublicAccountURL.URL_TYPE_GPS) {
                        PublicAccountURL.URL_GPS url_gps = (PublicAccountURL.URL_GPS) publicAccountURL;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("USER_ID", "TAG");
                        jsonObject.put("LAT", url_gps.lat);
                        jsonObject.put("LON", url_gps.lon);
                        jsonObject.put("DESC", "");
                        jsonObject.put("TAG_TYPE", 1);
                        jsonObject.put("TAG_INDEX", 1);
                        jsonObject.put("MY_TYPE", 1);
                        jsonArray.put(jsonObject);
                    } else {
                        String content = obj_viewBase.content;
                        if (StringUtil.isNotEmpty(content)) {//内容不为空
                            if (content.contains("在控号码")) {
                                title = content;
                            }
                            description = description + content + "\n";
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String number[] = title.split("：");
        if(number.length==2) {
            chatLocationObj.title = number[1] + "_" + selectTitle;
        }
        chatLocationObj.description = description;
        chatLocationObj.jArray = jsonArray.toString();
        chatLocationObj.content = description;
        ArrayList<ChatMsgObj> chatMsgObjArrayList = new ArrayList<>();
        chatMsgObjArrayList.add(chatLocationObj);
        Main.instance().handlerIntelligence.goIntelligenceAct(activity, publicGuid, 2, chatMsgObjArrayList);
        DialogTool.dialog.dismiss();
    }

    public static void urlChangeIntellgence(Obj_PublicMsgPicTxt obj_publicMsgPicTxt, Activity activity, String publicGuid, String selecttitle) {
        ChatPublicUrlObj chatLocationObj = new ChatPublicUrlObj();
        String description = "";
        String title = "";
        String url = "";
        String urlcontent="";
        for (Obj_ViewBase obj_viewBase : obj_publicMsgPicTxt.dataLines) {
            if (obj_viewBase instanceof Obj_ViewTxt) {
                Obj_ViewTxt obj_viewTxt = (Obj_ViewTxt) obj_viewBase;
                String content = obj_viewBase.content;
                PublicAccountURL publicAccountURL = PublicAccountURLFactory.getPublicAccountURL(obj_viewTxt.url);
                if (publicAccountURL.getUrlType() == PublicAccountURL.URL_TYPE_SUPERLINK) {
                    PublicAccountURL.URL_SUPERLINK url_superlink = (PublicAccountURL.URL_SUPERLINK) publicAccountURL;
                    if (StringUtil.isNotEmpty(url_superlink.superLinkUrl)) {
                        url = url_superlink.superLinkUrl;
                        urlcontent=content;
                    } else {
                        if (StringUtil.isNotEmpty(content)) {//内容不为空
                            if (content.contains("目标号码")) {
                                title = content;
                            }
                            description = description + content + "\n";
                        }
                    }
                } else {
                    if (StringUtil.isNotEmpty(content)) {//内容不为空
                        if (content.contains("目标号码")) {
                            title = content;
                        }
                        description = description + content + "\n";
                    }
                }
            }
        }
        String number[] = title.split("：");
        if(number.length==2) {
            chatLocationObj.title = number[1] + "_" + selecttitle;
        }
        chatLocationObj.description = description;
        chatLocationObj.url = url;
        chatLocationObj.content = description;
        chatLocationObj.urlText=urlcontent;
        ArrayList<ChatMsgObj> chatMsgObjArrayList = new ArrayList<>();
        chatMsgObjArrayList.add(chatLocationObj);
       Main.instance().handlerIntelligence.goIntelligenceAct(activity, publicGuid, 2, chatMsgObjArrayList);
        DialogTool.dialog.dismiss();
    }

//    public static void ownerChangeIntellgence(Obj_PublicMsgPicTxt obj_publicMsgPicTxt, Activity activity, String publicGuid, String selecttitle) {
//        ChatPublicUrlObj chatLocationObj = new ChatPublicUrlObj();
//        String description = "";
//        String title = "";
//        String url = "";
//        for (Obj_ViewBase obj_viewBase : obj_publicMsgPicTxt.dataLines) {
//            if (obj_viewBase instanceof Obj_ViewTxt) {
//                Obj_ViewTxt obj_viewTxt = (Obj_ViewTxt) obj_viewBase;
//                PublicAccountURL publicAccountURL = PublicAccountURLFactory.getPublicAccountURL(obj_viewTxt.url);
//                if (publicAccountURL.getUrlType() == PublicAccountURL.URL_TYPE_SUPERLINK) {
//                    PublicAccountURL.URL_SUPERLINK url_superlink = (PublicAccountURL.URL_SUPERLINK) publicAccountURL;
//                    url = url_superlink.superLinkUrl;
//                } else {
//                    String content = obj_viewBase.content;
//                    if (StringUtil.isNotEmpty(content)) {//内容不为空
//                        if (content.contains("目标号码")) {
//                            title = content;
//                        } else {
//                            description = description + content + "\n";
//                        }
//                    }
//                }
//            }
//        }
//        String number[] = title.split("：");
//        chatLocationObj.title = number[1] + "_" + selecttitle;
//        chatLocationObj.description = description;
//        chatLocationObj.url = url;
//        chatLocationObj.content = description;
//        Main.instance().handlerIntelligence.goIntelligenceAct(activity, publicGuid, 2, chatLocationObj);
//        DialogTool.dialog.dismiss();
//    }
}
