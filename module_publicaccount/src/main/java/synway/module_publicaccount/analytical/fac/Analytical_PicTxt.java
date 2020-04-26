package synway.module_publicaccount.analytical.fac;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgPicTxt;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewBase;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewDivider;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewPicTxt;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewTxt;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewTxtArea;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewUrlTxt;

//import synway.osc.MLog;

public class Analytical_PicTxt implements IAnalytical_Base {

    @SuppressWarnings("unused")
    private Context context = null;
//	private NetConfig netConfig = null;


    public int msgType() {
        return 4;
    }

    public void onInit(Context context) {
        this.context = context;

//		this.netConfig = Sps_NetConfig.getNetConfigFromSpf(this.context);
    }

    //图文消息
    public Obj_PublicMsgBase onDeal(JSONObject jsonObject) {
        Obj_PublicMsgPicTxt obj_PublicMsgPicTxt = new Obj_PublicMsgPicTxt();
        JSONArray dataLine;
        JSONArray jsonMsg=null;
        try {
            obj_PublicMsgPicTxt.title = jsonObject.getString("TITLE");
            obj_PublicMsgPicTxt.titleSize = jsonObject.getInt("TITLE_SIZE");
            obj_PublicMsgPicTxt.titleUrl = jsonObject.getString("TITLE_URL");
            obj_PublicMsgPicTxt.titleUrlName = jsonObject.getString("TITLE_URL_NAME");
            obj_PublicMsgPicTxt.titlePostiton = jsonObject.getInt("TITLE_POSITION");
            dataLine = jsonObject.getJSONArray("DATALINE");
       if(jsonObject.has("URL_MSG")){
           jsonMsg=jsonObject.getJSONArray("URL_MSG");//有URL域数据时新增URL_MSG字段存放的页面请求数据
       }
        } catch (JSONException e) {
            return null;
        }

        if (null == dataLine || dataLine.length() == 0) {
            return obj_PublicMsgPicTxt;
        }

        ArrayList<Obj_ViewBase> dataLineList = new ArrayList<Obj_ViewBase>();
        try {
            for (int i = 0; i < dataLine.length(); i++) {
                JSONObject iItem = dataLine.getJSONObject(i);
                int dataType = iItem.getInt("DATATYPE");
                Obj_ViewBase base=null;

                if (dataType == 1) {
                    base = getTxt(iItem);
                } else if (dataType == 3) {
                    base = getDivider(iItem);
                } else if (dataType == 4) {
                    base = getTxtArea(iItem);
                } else if (dataType == 5) {
                    base = getPicTxt(iItem);
                } else if (dataType == 6) {
                    //判断是否有页面请求数据
                    if(jsonMsg!=null){
                        for(int k=0;k<jsonMsg.length();k++){
                            if(jsonMsg.getJSONObject(k).getInt("ROW")==i){
                                base = getUrlTxt(iItem,jsonMsg.getJSONObject(k).getString("URLDATA"));
                                break;
                            }

                        }
                    }else {
                        base = getUrlTxt(iItem);
                    }
                } else {
                    base = null;
                }

                if (null != base) {
                    dataLineList.add(base);
                }
            }
            obj_PublicMsgPicTxt.dataLines = dataLineList;
            return obj_PublicMsgPicTxt;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private Obj_ViewDivider getDivider(JSONObject item) {
        Obj_ViewDivider divider = new Obj_ViewDivider();
        try {
            divider.septype = new JSONObject(item.getString("DATA")).optInt("SEPTYPE");
        } catch (JSONException e) {
            return null;
        }
        return divider;
    }


    private Obj_ViewTxt getTxt(JSONObject iItem) {

        JSONObject data = null;
        try {
//			data = iItem.getJSONObject("DATA");
            String strData = iItem.getString("DATA");
            data = new JSONObject(strData);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }

        Obj_ViewTxt obj_ViewTxt = new Obj_ViewTxt();
        try {
            JSONArray text = data.getJSONArray("TEXT");

            if (null == text || text.length() <= 0) {
                return null;
            }

            StringBuffer sb = new StringBuffer();
            for (int k = 0; k < text.length(); k++) {
                JSONObject kItem = text.getJSONObject(k);
//				if(k == 0){
                obj_ViewTxt.color = kItem.optString("COLOR", "#FFFFFF");
                obj_ViewTxt.size = kItem.optInt("SIZE", 2);
                obj_ViewTxt.url = kItem.getString("URL");
//					obj_ViewTxt.url = UrlDeal.getUrl(
//							obj_ViewTxt.url, netConfig.functionIP, netConfig.functionPort);
                obj_ViewTxt.urlName = kItem.getString("URL_NAME");
//				}
                sb.append(kItem.get("CONTENT"));
            }
            obj_ViewTxt.content = sb.toString();
            return obj_ViewTxt;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Obj_ViewTxtArea getTxtArea(JSONObject iItem) {
        JSONObject data = null;
        try {
//			data = iItem.getJSONObject("DATA");
            String strData = iItem.getString("DATA");
            data = new JSONObject(strData);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }

        try {
            Obj_ViewTxtArea obj_ViewTxtArea = new Obj_ViewTxtArea();
            obj_ViewTxtArea.url = data.getString("URL");
//			obj_ViewTxtArea.url = UrlDeal.getUrl(
//					obj_ViewTxtArea.url, netConfig.functionIP, netConfig.functionPort);
            obj_ViewTxtArea.urlName = data.getString("URL_NAME");
            JSONArray textArea = data.getJSONArray("TEXTAREA");

            obj_ViewTxtArea.content = new String[textArea.length()];
            obj_ViewTxtArea.size = new int[textArea.length()];
            obj_ViewTxtArea.color = new String[textArea.length()];
            for (int k = 0; k < textArea.length(); k++) {
                JSONObject kItemArray = textArea.getJSONObject(k);
                JSONArray text = kItemArray.getJSONArray("TEXT");
                if (null == text || text.length() <= 0) {
                    continue;
                }

                StringBuffer sb_p = new StringBuffer();
                String color = "";
                int size = 2;
                for (int p = 0; p < text.length(); p++) {
                    JSONObject pItem = text.getJSONObject(p);
                    if (p == 0) {
                        color = pItem.optString("COLOR", "#FFFFFF");
                        size = pItem.optInt("SIZE", 2);
                    }
                    sb_p.append(pItem.get("CONTENT"));
                }

                obj_ViewTxtArea.content[k] = sb_p.toString();
                obj_ViewTxtArea.size[k] = size;
                obj_ViewTxtArea.color[k] = color;
            }

            return obj_ViewTxtArea;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Obj_ViewPicTxt getPicTxt(JSONObject iItem) {
        JSONObject data = null;
        try {
//			data = iItem.getJSONObject("DATA");
            String strData = iItem.getString("DATA");
            data = new JSONObject(strData);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }

        Obj_ViewPicTxt obj_ViewPicTxt = new Obj_ViewPicTxt();
        try {
            obj_ViewPicTxt.url = data.getString("URL");
//			obj_ViewPicTxt.url = UrlDeal.getUrl(
//					obj_ViewPicTxt.url, netConfig.functionIP, netConfig.functionPort);
            obj_ViewPicTxt.urlName = data.getString("URL_NAME");

            obj_ViewPicTxt.picType = data.optInt("PICTYPE");
            obj_ViewPicTxt.picUrl = data.getString("PICURL");
//			obj_ViewPicTxt.picUrl = UrlDeal.getUrl(
//					obj_ViewPicTxt.picUrl, netConfig.functionIP, netConfig.functionPort);
//			//测试
//			obj_ViewPicTxt.picUrl = "http://218.108.76.82:7000/OSCUserPic/User_0571_1114_small";
//			//测试
            obj_ViewPicTxt.picName = getImgName(obj_ViewPicTxt.picUrl);

            obj_ViewPicTxt.color = data.optString("COLOR", "#FFFFFF");
            obj_ViewPicTxt.size = data.optInt("SIZE", 2);

            obj_ViewPicTxt.content = data.optString("CONTENT");
            String str[] = obj_ViewPicTxt.content.split("<br/>", -1);
            if (str.length > 1) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < str.length; i++) {
                    sb.append(str[i]);
                    if (i != (str.length - 1)) {
                        sb.append("\n");
                    }
                }
                obj_ViewPicTxt.content = sb.toString();
            }
            return obj_ViewPicTxt;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * dataType=6类型专用
     * @param iItem
     * @param urlData
     * @return
     */
    private Obj_ViewUrlTxt getUrlTxt(JSONObject iItem,String urlData) {
        JSONObject data = null;
        try {
            String strData = iItem.getString("DATA");
            data = new JSONObject(strData);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }

        Obj_ViewUrlTxt obj_viewUrlTxt = new Obj_ViewUrlTxt();
        try {

            obj_viewUrlTxt.url = data.getString("URL");
            obj_viewUrlTxt.urlType = data.getInt("URL_TYPE");
            obj_viewUrlTxt.color = data.optString("COLOR", "#FFFFFF");
            obj_viewUrlTxt.size = data.optInt("SIZE", 2);
            obj_viewUrlTxt.urlName=data.optString("URL_NAME");
            obj_viewUrlTxt.content = data.optString("CONTENT");
            Boolean isUserTitle = data.optBoolean("IS_DISPLAY_BANNER",true);
            if(!isUserTitle){
                obj_viewUrlTxt.isShowTitle = 0;
            }else{
                obj_viewUrlTxt.isShowTitle=1;
            }
            obj_viewUrlTxt.sourceUrl =data.optString("SOURCE_URL");
            obj_viewUrlTxt.data=urlData;
            String str[] = obj_viewUrlTxt.content.split("<br/>", -1);
            if (str.length > 1) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < str.length; i++) {
                    sb.append(str[i]);
                    if (i != (str.length - 1)) {
                        sb.append("\n");
                    }
                }
                obj_viewUrlTxt.content = sb.toString();
            }
//            FileTestLog.write("Analytical_PicTxt","IS_DISPLAY_BANNER: "+obj_viewUrlTxt.isShowTitle+"   SOURCE_URL:"+obj_viewUrlTxt.sourceUrl);
            return obj_viewUrlTxt;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Obj_ViewUrlTxt getUrlTxt(JSONObject iItem) {
        JSONObject data = null;
        try {
            String strData = iItem.getString("DATA");
            data = new JSONObject(strData);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }

        Obj_ViewUrlTxt obj_viewUrlTxt = new Obj_ViewUrlTxt();
        try {

            obj_viewUrlTxt.url = data.getString("URL");
            obj_viewUrlTxt.urlType = data.getInt("URL_TYPE");

            obj_viewUrlTxt.color = data.optString("COLOR", "#FFFFFF");
            obj_viewUrlTxt.size = data.optInt("SIZE", 2);
            obj_viewUrlTxt.urlName=data.optString("URL_NAME");
            obj_viewUrlTxt.content = data.optString("CONTENT");
            obj_viewUrlTxt.data=data.optString("EXTEND_CONTENT");
            Boolean isUserTitle = data.optBoolean("IS_DISPLAY_BANNER",true);
            if(!isUserTitle){
                obj_viewUrlTxt.isShowTitle = 0;
            }else{
                obj_viewUrlTxt.isShowTitle=1;
            }
            obj_viewUrlTxt.sourceUrl =data.optString("SOURCE_URL");
            String str[] = obj_viewUrlTxt.content.split("<br/>", -1);
            if (str.length > 1) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < str.length; i++) {
                    sb.append(str[i]);
                    if (i != (str.length - 1)) {
                        sb.append("\n");
                    }
                }
                obj_viewUrlTxt.content = sb.toString();
            }
//            FileTestLog.write("Analytical_PicTxt","IS_DISPLAY_BANNER: "+obj_viewUrlTxt.isShowTitle+"   SOURCE_URL:"+obj_viewUrlTxt.sourceUrl);
            return obj_viewUrlTxt;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    //picUrl= " http://192.168.110.130:8080/OSCUserPic/test.jpg "
    //所以我将下载下来图片的名字，命为 test.jpg
    //picUrl = " http://192.168.110.130:8080/OSCUserPic/test "
    //命为 test
    private static final String getImgName(String picUrl) {
        if (null == picUrl || "".equals(picUrl.trim())) {
            return null;
        }
        try {
            int startIndex = picUrl.lastIndexOf("/") + 1;

            int lastIndex = picUrl.lastIndexOf(".");
            if (startIndex >= lastIndex) {
                lastIndex = picUrl.length();
            }
            String picName = picUrl.substring(startIndex, lastIndex);
//            MLog.Log("liujie", picName);
            return picName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("liujie", "图文消息，下载图片出错, e=" + e.toString());
            return null;
        }

    }

}