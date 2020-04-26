package synway.module_publicaccount.public_chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import qyc.library.tool.http.HttpPost;

//将 地球GSP 转换为火星坐标
public class SyncGpsToMars {

	public void start(String ip, int port, double lon, double lat,String picurl) {
		String url = "http://" + ip + ":" + port
				+ "/OSCService/RTLocation/GetMarsCoordinate.osc";
		new Thread(new mRunnable(url, lon, lat,picurl)).start();
	}

	public void stop() {
		this.lsn = null;
	}

	private class mRunnable implements Runnable {

		private double lon;
		private double lat;
		private String url = null;
        private String picurl;
		private mRunnable(String url, double lon, double lat,String picurl) {
			this.url = url;
			this.lon = lon;
			this.lat = lat;
			this.picurl=picurl;
		}

		@Override
		public void run() {
			JSONObject requestJson = getJson(10086, lon, lat);
			JSONObject resultJson = HttpPost.postJsonObj(url, requestJson);
			String result[] = HttpPost.checkResult(resultJson);
			if (null != result) {
				return;
			}

			JSONArray jArray = resultJson.optJSONArray("POINTS");
			if (jArray.length() <= 0) {
				return;
			}

			JSONObject jObj = jArray.optJSONObject(0);
			double marsLon = jObj.optDouble("MARS_LON",0);
			double marsLat = jObj.optDouble("MARS_LAT",0);

			if(marsLat == 0 || marsLon == 0){
				return;
			}

			if (null != lsn) {
				lsn.onResult(marsLon, marsLat,picurl);
			}

		}

		private JSONObject getJson(int index, double lon, double lat) {
			JSONObject jRoot = new JSONObject();
			try {
				JSONArray jArray = new JSONArray();
				
				JSONObject jItem = new JSONObject();
				jItem.put("INDEX", index);
				jItem.put("LON", lon);
				jItem.put("LAT", lat);
				
				jArray.put(jItem);
				
				jRoot.put("POINTS", jArray);
				
			} catch (JSONException e) {
			}
			return jRoot;
		}

	}

	public void setLsn(IOnGspToMarsLsn lsn) {
		this.lsn = lsn;
	}

	private IOnGspToMarsLsn lsn = null;

	public interface IOnGspToMarsLsn {
		void onResult(double marsLon, double marsLat, String picurl);
	}

}