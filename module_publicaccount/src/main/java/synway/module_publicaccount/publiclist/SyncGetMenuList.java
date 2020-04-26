package synway.module_publicaccount.publiclist;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import qyc.library.tool.http.HttpPost;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.publiclist.GetHttpData.GetMenuList2;
import synway.module_publicaccount.publiclist.GetHttpData.PublicPost;

public class SyncGetMenuList {

	private Handler handler = null;
	public SyncGetMenuList(String publicID, int publicPor) {
		handler = new Handler();
	}

	public void start(String ip, int port, String userID, ArrayList<Obj_PublicAccount> arrayList ) {
		String url =GetMenuList2.geturl(ip,port,userID);
		new Thread(new mRunnable(url, userID, arrayList)).start();
	}

	public void stop() {
		this.lsn = null;
	}

	private class mRunnable implements Runnable {

		private String url = null;
		private String userID = null;
		private  ArrayList<Obj_PublicAccount> arrayList= null;

		// ==============result
		private JSONObject resultJson = null;
		private String reuslt[] = null;

		/**  */
		private ArrayList<Obj_Menu> firstMenu = null;
		/**  */
		private ArrayList<Obj_Menu> secondMenu = null;

		public mRunnable(String url, String userID,  ArrayList<Obj_PublicAccount> arrayList) {
			this.url = url;
			this.userID = userID;
			this.arrayList = arrayList;
		}
		@Override
		public void run() {
//			ArrayList<String> idList = GetMenuList2.getidList(arrayList);
//			JSONObject requestJson = GetMenuList2.getJson(this.userID, idList);
			resultJson = HttpPost.postJsonObj(url, new JSONObject());
			reuslt = PublicPost.checkResult(resultJson);
			if (null != reuslt) {
				if (null == lsn) {
					return;
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						String title = reuslt[0];
						String reason = reuslt[1];
						String detail = resultJson.optString("DETAIL");
						lsn.onFail(title, reason, detail);

					}
				});
				return;
			}
			JSONArray jsonArray = resultJson.optJSONArray("FC_MENU_INFO");
			if (jsonArray.length() <= 0) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (null != lsn) {
							lsn.onFail("", "^u",
									"jsonArray.length() <= 0 ");
						}
					}
				});
				return;
			}
			HashMap<String,ArrayList<Obj_Menu>> menu=GetMenuList2.getMenu(jsonArray);
			firstMenu =menu.get("firstmenu");
			secondMenu =menu.get("secondmenu");
			if (firstMenu.size() == 0) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (null != lsn) {
							lsn.onResult(null, null);
						}
					}
				});
				return;
			}

			handler.post(new Runnable() {
				@Override
				public void run() {
					if (null != lsn) {
						lsn.onResult(firstMenu, secondMenu);
					}
				}
			});

		}



	}

	public  IOnGetHttpMenuListLsn lsn = null;

	public void setLsn(IOnGetHttpMenuListLsn lsn) {
		this.lsn = lsn;
	}

	public interface IOnGetHttpMenuListLsn{
		 void onResult(ArrayList<Obj_Menu> firstMenuList,
					   ArrayList<Obj_Menu> secondMenuList);
		 void onFail(String title, String reason, String detail);
	}

}