package synway.module_publicaccount.push;

import org.json.JSONException;
import org.json.JSONObject;

class JsonParseMsg {

	static final String getAnnexType( JSONObject msgJson ) {
		String result = "[附件]";

		try {
			int msgType = msgJson.getInt("MSG_TYPE");
			JSONObject infoJson = msgJson.getJSONObject("MSG_INFO");

			if (msgType == 1) {
				String msgContent = infoJson.getString("CONTENT");
				return msgContent;
			} else if (msgType == 2) {
				String annexName = infoJson.getString("FILE_NAME");

				int pointIndex = annexName.lastIndexOf(".");

				if(pointIndex >= (annexName.length() - 1)){
					return "[附件]";
				}
				String annexSuffix = annexName.substring(pointIndex + 1);
				return getTypeFromSuffix(annexSuffix);

			} else if(msgType == 3){
				return "[静态位置]";
			}else {
				return "[附件]";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}


	/** 1=图片 2=视频 3=声音 4=其他 */
	static final String getTypeFromSuffix(String suffix) {
		//当文件后缀名为大写的 .JPG .PNG 格式的情况,需要转小写进行比对,不然会识别为附件格式
		suffix = suffix.toLowerCase();
		if (suffix.equals("png")) {
			return "[图片]";
		} else if (suffix.equals("jpg")) {
			return "[图片]";
		} else if (suffix.equals("bmp")) {
			return "[图片]";
		} else if (suffix.equals("amr")) {
			return "[语音]";
		} else if (suffix.equals("mp3")) {
			return "[语音]";
		} else if (suffix.equals("wav")) {
			return "[语音]";
		} else if (suffix.equals("3gp")) {
			return "[视频]";
		} else if (suffix.equals("mp4")) {
			return "[视频]";
		} else {
			return "[附件]";
		}
	}


}
