package synway.module_publicaccount.public_chat.bottom;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import qyc.library.tool.http.HttpHead;
import qyc.library.tool.http.HttpPost;

public class SyncSendMenuLsn {

	private Handler handler= null;
	public SyncSendMenuLsn() {
		handler = new Handler();
	}

	public void start(String ip, int port, String userID, String publicGUID,
			String menuKey) {
		String url = HttpHead.urlHead(ip, port)
				+ "PFService/PublicFunction/SendMenuEvent.osc";
		new Thread(new mRunnable(url, userID, publicGUID, menuKey)).start();
	}

	public void stop() {
		this.lsn = null;
	}

	private class mRunnable implements Runnable {

		private String userID = null;
		private String publicGUID = null;

		private String menuKey = null;
		
		private String url = null;

		public mRunnable(String url, String userID, String publicGUID,
				String menuKey) {
			this.menuKey = menuKey;
			this.userID = userID;
			this.publicGUID = publicGUID;
			
			this.url = url;
		}

		@Override
		public void run() {
			JSONObject requestJson = getJson(userID,publicGUID,menuKey);
			final JSONObject resultJson = HttpPost.postJsonObj(url, requestJson);

			String result[] = HttpPost.checkResult(resultJson);
			if(null != result ){
				handler.post(new Runnable() {
					@Override
					public void run() {
						if(lsn != null){
							String reason = resultJson.optString("REASON");
							String detail = resultJson.optString("DETAIL");
							lsn.onFail("错误", reason, detail);
						}
					}
				});
				return;
			}

			handler.post(new Runnable() {
				@Override
				public void run() {
					if(null != lsn){
						lsn.onResult();
					}
				}
			});
			

		}
		
		private JSONObject getJson(String userID, String publicGUID,String menuKey){
			JSONObject jsonObject = new JSONObject();
			
			try {
				jsonObject.put("USER_ID", userID);
				jsonObject.put("FC_ID", publicGUID);
				jsonObject.put("MENU_KEY", menuKey);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return jsonObject;
		}

	}

	public IOnSendLsn lsn = null;

	public void setLsn(IOnSendLsn lsn) {
		this.lsn = lsn;
	}

	public interface IOnSendLsn {
		void onResult();

		void onFail(String title, String reason, String detail);
	}
}
