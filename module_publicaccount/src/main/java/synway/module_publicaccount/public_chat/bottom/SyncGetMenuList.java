package synway.module_publicaccount.public_chat.bottom;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import qyc.library.tool.http.HttpHead;
import qyc.library.tool.http.HttpPost;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.until.NetUrlUntil;

import static synway.module_publicaccount.until.ConfigUtil.PUB_CJPT_GUID;

public class SyncGetMenuList {

	private Handler handler = null;
    private Context context;
	private String publicId;
	public SyncGetMenuList(String publicID,Context context) {
		this.publicId=publicID;
		this.context=context;
		handler = new Handler();
	}

	public void start(String ip, int port, String userID, ArrayList<String> fcID_List) {
		String url = HttpHead.urlHead(ip, port)
				+ "PFService/PublicFunction/GetMenuInfo.osc";
		new Thread(new mRunnable(url, userID, fcID_List)).start();
	}

	public void stop() {
		this.lsn = null;
	}

	private class mRunnable implements Runnable {

		private String url = null;
		private String userID = null;
		private ArrayList<String> idList = null;

		// ==============result
		private JSONObject resultJson = null;
		private String reuslt[] = null;

		/**  */
		private ArrayList<Obj_Menu> firstMenu = null;
		/**  */
		private ArrayList<Obj_Menu> secondMenu = null;

		public mRunnable(String url, String userID, ArrayList<String> fcIDList) {
			this.url = url;
			this.userID = userID;
			this.idList = fcIDList;
		}
		@Override
		public void run() {
			JSONObject requestJson = getJson(this.userID, this.idList);
			resultJson = HttpPost.postJsonObj(url, requestJson);
			Log.d("dym------------------->", "resultJson= "+resultJson.toString());
			reuslt = HttpPost.checkResult(resultJson);
			if (null != reuslt) {
				if(null == lsn){
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

			firstMenu = new ArrayList<>();
			secondMenu = new ArrayList<>();
			if(publicId.equals(PUB_CJPT_GUID)){
				Obj_Menu obj_menu=getBlueToothMenu();
				firstMenu.add(obj_menu);
			}
			JSONObject obj = jsonArray.optJSONObject(0);

			JSONArray objInfoList = obj.optJSONArray("MENU_INFO");
			for (int k = 0; k < objInfoList.length(); k++) {
				JSONObject objInfo = objInfoList.optJSONObject(k);

				Obj_Menu obj_Menu = new Obj_Menu();
				obj_Menu.ID = obj.optString("FC_ID");
				obj_Menu.menuType = objInfo.optInt("MENU_TYPE");
				obj_Menu.menuUrl = objInfo.optString("MENU_URL");
				try {
					obj_Menu.menuUrlType = objInfo.optInt("MENU_URL_TYPE",0);
				}catch (Exception e){
					obj_Menu.menuUrlType =0;
				}
				obj_Menu.menuName = objInfo.optString("MENU_NAME");
				obj_Menu.menuKey = objInfo.optString("MENU_KEY");
				obj_Menu.menuGUID = objInfo.optString("MENU_GUID");
				obj_Menu.menuFather = objInfo.optString("MENU_FATHER");
				obj_Menu.menuPicUrl= objInfo.optString("MENU_PICID");
				if(objInfo.optString("MENU_PICID")!=null&&!objInfo.optString("MENU_PICID").equals("")){
					obj_Menu.menuPicUrl = NetUrlUntil.getUrlId(objInfo.optString("MENU_PICID"));
				}
				if (obj_Menu.menuFather == null
						|| obj_Menu.menuFather.equals("")) {
					firstMenu.add(obj_Menu);
				} else {
					secondMenu.add(obj_Menu);
				}
			}

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

		private JSONObject getJson(String strID, ArrayList<String> arrayList) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("USER_ID", strID);

				JSONArray jsonArray = new JSONArray();
				JSONObject objItem = null;
				for (int i = 0; i < arrayList.size(); i++) {
					objItem = new JSONObject();
					objItem.put("FC_ID", arrayList.get(i));
					jsonArray.put(objItem);
				}

				jsonObject.put("FC_IDS", jsonArray);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonObject;
		}

	}
	private Obj_Menu getBlueToothMenu(){
		Obj_Menu obj_menu=new Obj_Menu();
		obj_menu.menuName=context.getString(R.string.receiveBlueToothFile);
		return obj_menu;
	}
	public  IOnGetMenuListLsn lsn = null;

	public void setLsn(IOnGetMenuListLsn lsn) {
		this.lsn = lsn;
	}

	public interface IOnGetMenuListLsn {
		 void onResult(ArrayList<Obj_Menu> firstMenuList,
							 ArrayList<Obj_Menu> secondMenuList);
		 void onFail(String title, String reason, String detail);
	}

}