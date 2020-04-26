//package synway.module_publicaccount.publiclist;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.os.Handler;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Locale;
//
//import qyc.library.tool.exp.ThrowExp;
//import qyc.library.tool.http.HttpHead;
//import qyc.library.tool.http.HttpPost;
//import synway.module_interface.config.BaseUtil;
//import synway.module_interface.db.SQLite;
//import synway.module_publicaccount.db.table_util.Table_PublicAccount;
//import synway.module_publicaccount.Main;
//import synway.module_publicaccount.publiclist.GetHttpData.GetPublicList;
//import synway.module_publicaccount.until.DbUntil;
//import synway.module_publicaccount.until.NetUrlUntil;
//
//public class SyncGetPublicByHttp {
//
//	private boolean isStop = false;
//
//	private Handler handler = null;
//
//	public SyncGetPublicByHttp() {
//		handler = new Handler();
//	}
//
//	public void start(String ip, int port, String userID, ArrayList<String> arrayList) {
//		if (isStop) {
//			ThrowExp.throwRxp("【异模】  在stop之后不允许start");
//			return;
//		}
//
//		String url = GetPublicList.geturl(ip,port);
//		new Thread(new mRunnable(url, userID, arrayList)).start();
//	}
//
//	public void stop() {
//		isStop = true;
//		this.lsn = null;
//	}
//
//	private class mRunnable implements Runnable {
//
//		private String url = null;
//		private String userID = null;
//
//		private ArrayList<Obj_PublicAccount> AllList = null;
//		private ArrayList<Obj_PublicAccount> resultList = null;
//
//		private HashMap<String, Integer> selePos = null;
//
//		private ArrayList<String> idList = null;
//		private ArrayList<Obj_PublicAccount> appList=null;
//		private mRunnable(String url, String userID, ArrayList<String> idList) {
//			this.url = url;
//			this.userID = userID;
//			this.idList = idList;
//		}
//
//		@Override
//		public void run() {
//			JSONObject requestJson = GetPublicList.getJson(userID);
//
////			MLog.Log("liujie", url);
////			MLog.Log("liujie", requestJson.toString());
//
//			final JSONObject resultJson = HttpPost.postJsonObj(url, requestJson);
//			final String reuslt[] = HttpPost.checkResult(resultJson);
//			if(null != reuslt){
//				if(null == lsn){
//					return;
//				}
//
//				final String deatil = resultJson.optString("Detail");
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//						lsn.onFail(reuslt[0], reuslt[0] + ",请重试", reuslt[1] + "\n" + deatil);
//					}
//				});
//				return;
//			}
//
//			JSONArray jsonArray = resultJson.optJSONArray("FC_BASIC_INFO");
//			int length = jsonArray.length();
//			if(length == 0){
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//						if(null != lsn){
//							lsn.onFail("通知", "没有公众号数据", "");
//						}
//					}
//				});
//				return;
//			}
//
//          HashMap<String,Object> publicList=GetPublicList.getPublicList(jsonArray,idList,Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
//			AllList=(ArrayList<Obj_PublicAccount>)publicList.get("alllist");
//			selePos=(HashMap<String, Integer>)publicList.get("selepos");
//			appList=(ArrayList<Obj_PublicAccount>)publicList.get("applist");
//			handler.post(new Runnable() {
//				@Override
//				public void run() {
//					if(null != lsn){
//						lsn.onResult(AllList, selePos,appList);
//					}
//				}
//			});
//
//		}
//
//
//
//
//	}
//
//	public void setLsn(IOnGetAccountByHttp lsn) {
//		this.lsn = lsn;
//	}
//
//	private IOnGetAccountByHttp lsn = null;
//
//	public interface IOnGetAccountByHttp {
//
//		public void onResult(ArrayList<Obj_PublicAccount> arrayList, HashMap<String, Integer> selePos,ArrayList<Obj_PublicAccount> applist);
//
//		public void onFail(String title, String reason, String detail);
//
//	}
//
//}