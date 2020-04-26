package qyc.library.tool.http;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP无线程封装
 * 
 * @author 钱园超 2015年8月19日下午3:23:34
 */
public class HttpPost {

	/**
	 * 发起一个Http请求
	 * @param url
	 * @param jsonObject
	 * @return
	 */
	public static final JSONObject postJsonObj(String url, JSONObject jsonObject) {
		return postJsonObj(url, jsonObject, 30000, 30000);
	}

	/**
	 * 发起一个Http请求
	 * @param url
	 * @param jsonObject
	 * @param soTimeOut
	 * @param connectTimeOut
	 * @return
	 */
	public static final JSONObject postJsonObj(String url, JSONObject jsonObject, int soTimeOut, int connectTimeOut) {
		// System.out.println("HTTP_Send:" + jsonObject.toString());
		byte[] data = HttpClass.strToUnicodeBytes(jsonObject.toString());
		JSONObject resultJson = null;
		String result = null;
		try {
			InputStream is = null;
			if (soTimeOut <= 0 || connectTimeOut <= 0) {
				is = HttpClass.postByte(url, data);
			} else {
				is = HttpClass.postByte(url, data, connectTimeOut, soTimeOut);
			}
			result = HttpClass.stream2String(is);

			try {
				is.close();
			}
			catch (Exception e) {
			}
			// 降低网速
			// Thread.sleep(5000);
			// 降低网速

			resultJson = new JSONObject(result);
		}
		catch (ClientProtocolException e) {
			resultJson = getExpJSON(-1, e.toString());
		}
		catch (IOException e) {
			resultJson = getExpJSON(-1, e.toString());
		}
		catch (JSONException e) {
			resultJson = getExpJSON(-2, result);
		}
		catch (Exception e) {
			resultJson = getExpJSON(-1, e.toString());
		}

		return resultJson;
	}

	private static final JSONObject getExpJSON(int result, String wrongMsg) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("RESULT", result);
			jsonObject.put("REASON", wrongMsg);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * 检查结果
	 * 
	 * @param jsonObject
	 * @return NULL表示结果正常，非NULL[0]表示错误信息描述,非NULL[1]表示详细错误信息
	 */
	public static String[] checkResult(JSONObject jsonObject) {
		int result = jsonObject.optInt("RESULT", -1);
		// 检查结果
		if (result == -2) {
			return new String[]{"返回数据格式有误", jsonObject.optString("REASON", "JSON has no KEY=REASON")};
		} else if (result == -1) {
			return new String[]{"网络请求失败", jsonObject.optString("REASON", "JSON has no KEY=REASON")};
		} else if (result == 0) {
			return new String[]{"操作失败", jsonObject.optString("REASON", "JSON has no KEY=REASON")};
		} else {
			return null;
		}
	}

}
