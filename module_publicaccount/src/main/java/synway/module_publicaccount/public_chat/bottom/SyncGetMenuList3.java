package synway.module_publicaccount.public_chat.bottom;

import android.content.Context;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import qyc.library.tool.http.HttpHead;
import qyc.library.tool.http.HttpPost;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.publiclist.GetHttpData.PublicPost;

/**
 * 通过业务注册查询服务查询列表消息而不是从原来的汇信服务查询
 */
public class SyncGetMenuList3 {

	private Handler handler = null;
    private Context context;
	private String publicId;
	public SyncGetMenuList3(String publicID, Context context) {
		this.publicId=publicID;
		this.context=context;
		handler = new Handler();
	}

	public void start(String ip, int port, String userID) {
		String url = HttpHead.urlHead(ip, port)
				+ "/publicFunc/getMenuInfo?userId="+userID;
		new Thread(new mRunnable(url, userID)).start();
	}

	public void stop() {
		this.lsn = null;
	}

	private class mRunnable implements Runnable {

		private String url = null;
		private String userID = null;

		// ==============result
		private JSONObject resultJson = null;
		private String reuslt[] = null;

		/**  */
		private ArrayList<Obj_Menu> firstMenu = null;

		public mRunnable(String url, String userID) {
			this.url = url;
			this.userID = userID;
		}
		@Override
		public void run() {
			resultJson = HttpPost.postJsonObj(url, new JSONObject());
//			FileTestLog.write("SyncGetMenuList3","获取菜单的数据= "+resultJson.toString());
			reuslt = PublicPost.checkResult(resultJson);
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

			JSONArray jsonArray = resultJson.optJSONArray("result");
			if (jsonArray.length() <= 0) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (null != lsn) {
							lsn.onResult(null);
						}
					}
				});
				return;
			}
			firstMenu = new ArrayList<>();
			for(int i =0;i<jsonArray.length();i++){
				try {
					JSONObject obj = jsonArray.getJSONObject(i);
					String ID = obj.optString("FC_ID");
					String FC_NAME =obj.optString("FC_NAME");
					JSONArray menuArray = obj.getJSONArray("FC_MENUS");
					if(ID.equals(publicId)){
						for(int j =0;j<menuArray.length();j++) {
							JSONObject menuJson = menuArray.getJSONObject(j);
							Obj_Menu obj_Menu = new Obj_Menu();
							obj_Menu.ID = publicId;
							obj_Menu.menuGUID = menuJson.optString("bTID");
							//menuType 现阶段只支持一级菜单
							obj_Menu.menuType = 1;
							obj_Menu.menuName = menuJson.optString("bTName");
							obj_Menu.menuUrlType = menuJson.optInt("mobileUrlType");
							obj_Menu.menuUrl = menuJson.optString("mobileUrl");
							Boolean isUserTitle = menuJson.optBoolean("is_display_banner",true);
							if(!isUserTitle){
								obj_Menu.isShowTitle = 0;
							}else{
								obj_Menu.isShowTitle=1;
							}
							obj_Menu.sourceUrl = menuJson.optString("source_url");
							firstMenu.add(obj_Menu);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			if (firstMenu.size() == 0) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (null != lsn) {
							lsn.onResult(null);
						}
					}
				});
				return;
			}

			handler.post(new Runnable() {
				@Override
				public void run() {
					if (null != lsn) {
						lsn.onResult(firstMenu);
					}
				}
			});

		}

	}

	public  IOnGetMenuListLsn lsn = null;

	public void setLsn(IOnGetMenuListLsn lsn) {
		this.lsn = lsn;
	}

	public interface IOnGetMenuListLsn {
		 void onResult(ArrayList<Obj_Menu> firstMenuList);
		 void onFail(String title, String reason, String detail);
	}

}