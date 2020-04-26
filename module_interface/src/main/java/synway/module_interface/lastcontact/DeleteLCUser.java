package synway.module_interface.lastcontact;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import qyc.library.tool.http.HttpPost;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
public class DeleteLCUser {

	public static final synchronized void deleteLCUsera(Context context, String targetID) {

		NetConfig netConfig = Sps_NetConfig.getNetConfigFromSpf(context);

		String url = "http://" + netConfig.httpIP + ":" + netConfig.httpPort
				+ "/OSCService/NRTComm/SetLastChatUser.osc";

		String userID = Sps_RWLoginUser.readUserID(context);
		JSONObject requestJson = new JSONObject();
		try {
			requestJson.put("USER_ID", userID);
			requestJson.put("TARGET_ID", targetID);
			requestJson.put("TARGET_TYPE", 1);
			requestJson.put("TYPE", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HttpPost.postJsonObj(url, requestJson);

//		JSONObject resultJson = HttpPost.postJsonObj(url, requestJson);

//		String result[] = HttpPost.checkResult(resultJson);
//		if (null != result) {
//
//		}
	}

}
