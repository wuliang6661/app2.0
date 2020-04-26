package synway.module_publicaccount.fingerprint;

import org.json.JSONArray;
import org.json.JSONObject;

import qyc.library.tool.http.HttpHead;
import qyc.library.tool.http.HttpPost;

/**
 * Created by QSJH on 2016/5/26 0026.
 */
public class SyncGetFingerprint {

    public String[] getFingerprint(String ip, int port, String userID) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("USER_ID", userID);

        JSONObject resultJson = HttpPost.postJsonObj(HttpHead.urlHead(ip, port)
                + "OSCService/BasicData/GetFingerprint.osc", jsonObject);

        String result[] = HttpPost.checkResult(resultJson);
        if (result != null) {
            throw new Exception(result[0] + result[1]);
        }

        JSONArray jsonArray = resultJson.optJSONArray("FINGERPRINT_LIST");
        int size = jsonArray == null ? 0 : jsonArray.length();
        final String[] idArray = new String[size];
        for (int i = 0; i < size; i++) {
            idArray[i] = jsonArray.getJSONObject(i).getString("ID");
        }
        return idArray;
    }
}
