//package synway.osc.public_account.public_chat.analytical;//package synway.osc.public_account.public_chat.analytical;
//
//import java.util.ArrayList;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import synway.osc.MLog;
//import synway.osc.common.ThrowExp;
//import synway.osc.public_account.public_chat.adapter.obj.Obj_PublicPicTXT;
//import synway.osc.public_account.public_chat.adapter.obj.pic_txt.View_MsgBase;
//import synway.osc.public_account.public_chat.adapter.obj.pic_txt.View_MsgDivision;
//import synway.osc.public_account.public_chat.adapter.obj.pic_txt.View_MsgImgArea;
//import synway.osc.public_account.public_chat.adapter.obj.pic_txt.View_MsgTxT;
//import synway.osc.public_account.public_chat.adapter.obj.pic_txt.View_MsgTxtArea;
//
//public class Analytical_PicTXT {
//
//	public static final Obj_PublicPicTXT analyPicTXT(JSONObject jsonObject) {
//		Obj_PublicPicTXT obj_PublicPicTXT = new Obj_PublicPicTXT();
//		obj_PublicPicTXT.title = jsonObject.optString("TITLE");
//		obj_PublicPicTXT.titleUrl = jsonObject.optString("TITLE_URL");
//		obj_PublicPicTXT.titleUrlName = jsonObject.optString("TITLE_URL_NAME");
//		JSONArray dataLine = jsonObject.optJSONArray("DATALINE");
//
//		if (dataLine.length() <= 0) {
//			return null;
//		}
//
//		ArrayList<View_MsgBase> dataLines = new ArrayList<View_MsgBase>();
//		for (int i = 0; i < dataLine.length(); i++) {
//			JSONObject jItem = dataLine.optJSONObject(i);
//			int dataType = jItem.optInt("DATATYPE");
//			View_MsgBase base = null;
//			if (dataType == 1) {
//				base = getTxT(jItem);
//			} else if (dataType == 2) {
//				continue;
//			} else if (dataType == 3) {
//				base = getDivision(jItem);
//			} else if (dataType == 4) {
//				base = getTxtArea(jItem);
//			} else if (dataType == 5) {
//				base = getImgArea(jItem);
//			}
//
//			if (null != base) {
//				dataLines.add(base);
//			}
//		}
//
//		obj_PublicPicTXT.dataLines = dataLines;
//
//		return obj_PublicPicTXT;
//	}
//
//	private static final View_MsgTxT getTxT(JSONObject jsonObject) {
//		View_MsgTxT view_MsgTxT = new View_MsgTxT();
//
//		// JSONObject data = jsonObject.optJSONObject("DATA");
//		String strData = jsonObject.optString("DATA");
//		JSONObject data = null;
//		try {
//			data = new JSONObject(strData);
//		} catch (JSONException e) {
//			ThrowExp.throwRxp("Analytical_PicTXT throw error , e="
//					+ e.toString());
//		}
//
//		JSONArray text = data.optJSONArray("TEXT");
//		if (null == text || text.length() <= 0) {
//			return null;
//		}
//
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < text.length(); i++) {
//			JSONObject jItem = text.optJSONObject(i);
//			String content = jItem.optString("CONTENT");
//			sb.append(content);
//		}
//
//		view_MsgTxT.content = sb.toString();
//
//		return view_MsgTxT;
//	}
//
//	private static final View_MsgDivision getDivision(JSONObject jsonObject) {
//		View_MsgDivision division = new View_MsgDivision();
//		division.septype = 1;
//		return division;
//	}
//
//	private static final View_MsgTxtArea getTxtArea(JSONObject jsonObject) {
//		View_MsgTxtArea msgTxtArea = new View_MsgTxtArea();
//
//		// JSONObject data = jsonObject.optJSONObject("DATA");
//		String strData = jsonObject.optString("DATA");
//		JSONObject data = null;
//		try {
//			data = new JSONObject(strData);
//		} catch (JSONException e) {
//			ThrowExp.throwRxp("Analytical_PicTXT throw error , e="
//					+ e.toString());
//		}
//
//		JSONArray textArea = data.optJSONArray("TEXTAREA");
//		if (null == textArea || textArea.length() <= 0) {
//			return null;
//		}
//
//		StringBuffer sb_i = new StringBuffer();
//		for (int i = 0; i < textArea.length(); i++) {
//			JSONObject iItem = textArea.optJSONObject(i);
//			JSONArray text = iItem.optJSONArray("TEXT");
//			if (null == text || text.length() <= 0) {
//				continue;
//			}
//
//			StringBuffer sb_k = new StringBuffer();
//			for (int k = 0; k < text.length(); k++) {
//				JSONObject kItem = text.optJSONObject(k);
//				String content = kItem.optString("CONTENT");
//
//				sb_k.append(content);
//			}
//
//			sb_i.append(sb_k.toString());
//			sb_i.append("\n");
//
//		}
//		msgTxtArea.content = sb_i.toString();
//
//		return msgTxtArea;
//	}
//
//	private static final View_MsgImgArea getImgArea(JSONObject jsonObject) {
//		View_MsgImgArea view_MsgImgArea = new View_MsgImgArea();
//
//		// JSONObject data = jsonObject.optJSONObject("DATA");
//		String strData = jsonObject.optString("DATA");
//		JSONObject data = null;
//		try {
//			data = new JSONObject(strData);
//		} catch (JSONException e) {
//			ThrowExp.throwRxp("Analytical_PicTXT throw error , e="
//					+ e.toString());
//		}
//
//		view_MsgImgArea.picUrl = data.optString("PICURL");
//		view_MsgImgArea.content = data.optString("CONTENT");
//		view_MsgImgArea.url = data.optString("URL");
//		MLog.Log("liujie", "Public-> picUrl:" + view_MsgImgArea.picUrl);
//		view_MsgImgArea.picName = getImgName(view_MsgImgArea.picUrl);
//		return view_MsgImgArea;
//	}
//
//	private static final String getImgName(String picUrl){
//		//picUrl= " http://192.168.110.130:8080/OSCUserPic/test.jpg "
//		// test.jpg
//		if(null == picUrl || "".equals(picUrl.toString().trim())){
//			return null;
//		}
//		
//		int startIndex = picUrl.lastIndexOf("/") + 1;
//		int lastIndex = picUrl.lastIndexOf(".");
//		if(startIndex >= lastIndex){
//			return null;
//		}
//		return picUrl.substring(startIndex,lastIndex);
//	}
//
//}