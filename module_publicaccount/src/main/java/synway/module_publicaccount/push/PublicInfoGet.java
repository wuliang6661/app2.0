package synway.module_publicaccount.push;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import qyc.library.tool.http.HttpPost;
import synway.module_publicaccount.publiclist.GetHttpData.PublicPost;
import synway.module_publicaccount.until.NetUrlUntil;


class PublicInfoGet {

    static final Obj_PublicInfo get(String userID, String publicGUID,
                                    String ip, int port) {
        String url = "http://" + ip + ":" + port
                + "/publicFunc/getFunctionInfoById?funcId=" + publicGUID + "&userId=" + userID;

        JSONObject resultJson = HttpPost.postJsonObj(url, new JSONObject());


        String result[] = PublicPost.checkResult(resultJson);
        if (null != result) {
            return null;
        }

        Obj_PublicInfo info = new Obj_PublicInfo();
        try {
            info.fcID = publicGUID;
            info.fcName = resultJson.getString("fCName");
//            info.fcCompany = resultJson.optString("FC_COMPANY");
//            info.fcContact = resultJson.optString("FC_CONTACT");
//            info.fcTel = resultJson.optString("FC_TEL");
//			info.mobile_pic=resultJson.optString("MOBILE_PIC");
            if (resultJson.optString("mobilePic") != null && !resultJson.optString("mobilePic").equals("")) {
                info.mobile_pic = NetUrlUntil.getUrlId(resultJson.optString("mobilePic"));
            }
            Log.i("testy", "查到的单个公众号的头像ID" + info.mobile_pic);
        } catch (JSONException e) {

            return null;
        }

        return info;
    }

    private static final JSONObject getJson(String userID, String publicGUID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("USER_ID", userID);
            jsonObject.put("FUNCTION_ID", publicGUID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}