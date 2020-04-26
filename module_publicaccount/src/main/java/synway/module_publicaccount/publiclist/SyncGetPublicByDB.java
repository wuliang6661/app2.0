package synway.module_publicaccount.publiclist;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import qyc.library.tool.exp.ThrowExp;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.db.SQLite;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_chat.bottom.SyncGetMenuByDB;
import synway.module_publicaccount.until.StringUtil;

import static synway.module_publicaccount.publiclist.bean.App_InformationBean.getAppInforMation;

class SyncGetPublicByDB {

	private Handler handler = null;

	public SyncGetPublicByDB() {
		handler = new Handler();
	}

	// 在异步模型中不能传入act中的 SQLiteDatabase,
	// 因为在act onDestory的时候，会去关闭SQLiteDatabase
	// 而这个时候，【异模】中可能还在利用这个对象对数据库进行一系列的操作
	// 也不要使用 act 的Context对象，在【异模】中开始SQLiteDatabase，用application的对象吧
	public void start(ArrayList<String> idList) {
		new Thread(new mRunnbale(idList)).start();
	}

	public void stop() {
		this.lsn = null;
	}

	private class mRunnbale implements Runnable {

		private ArrayList<Obj_PublicAccount> resultList = null;

		private HashMap<String, Integer> selePos = null;

		private ArrayList<String> idList = null;
		private SyncGetMenuByDB syncGetMenuByDB = null;
		private mRunnbale(ArrayList<String> idList) {
			this.idList = idList;
		}

		@Override
		public void run() {
			// SQLiteDatabase db = null;
			SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
			try {
				// db = SQLiteDatabase.openDatabase(
				// BaseUtil.getDBPath(MainApp.getInstance()), null,
				// SQLiteDatabase.OPEN_READWRITE);
			} catch (Exception e) {
				ThrowExp.throwRxp("SyncGetPublicByDB 打开数据库出错");
			}
			String tableName = Table_PublicAccount._TABLE_NAME;
			String columns[] = new String[] { Table_PublicAccount.FC_ID,
					Table_PublicAccount.FC_NAME,
					Table_PublicAccount.FC_COMPANY,
					Table_PublicAccount.FC_CONTACT, Table_PublicAccount.FC_TEL ,Table_PublicAccount.FC_MOBILEPIC,Table_PublicAccount.FC_TYPE,Table_PublicAccount.APP_INFORMATION};
			String strs[] = SQLite.query(sqliteHelp.getWritableDatabase(),
					tableName, columns, "|", null, null, null);
			// Table_PublicAccount.FC_ID + " asc "

			if (null == strs || strs.length == 0) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (null != lsn) {
							lsn.onFail();
						}
					}
				});
				return;
			}
			// 存放含有索引字母的位置
			HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
			resultList = new ArrayList<Obj_PublicAccount>();
			final ArrayList<Obj_PublicAccount> appObjList=new ArrayList<>();
			for (int i = 0; i < strs.length; i++) {
				String sList[] = strs[i].split("\\|", -1);
				Obj_PublicAccount objAccount = new Obj_PublicAccount();
				objAccount.ID = sList[0];
				objAccount.name = sList[1];
				objAccount.company = sList[2];
				objAccount.contact = sList[3];
				objAccount.contactTel = sList[4];
				objAccount.fc_mobilepic=sList[5];
				if(StringUtil.isNotEmpty(sList[6])) {
					objAccount.type = Integer.parseInt(sList[6]);
				}
				if(objAccount.type==1) {
					objAccount.app_information = getAppInforMation(sList[7]);
					appObjList.add(objAccount);
				}else {
					objAccount.namePinYin = PinYinDeal.getPinYin2(objAccount.name);
					String firstZiMu = objAccount.namePinYin.charAt(0) + "";
					if (!hashMap.containsKey(firstZiMu)) {
						hashMap.put(firstZiMu, i);
						Obj_PublicAccount obj2 = new Obj_PublicAccount();
						obj2.isItem = false;
						obj2.name = firstZiMu.toUpperCase(Locale.CHINA);
						obj2.namePinYin = firstZiMu.toUpperCase(Locale.CHINA);
						resultList.add(obj2);
					}
					resultList.add(objAccount);
				}
					File file = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + objAccount.ID);
					if (!file.exists()) {
						idList.add(objAccount.ID);
					}
			}
			Collections.sort(resultList, new SortAccountComparator());
			selePos = new HashMap<String, Integer>();
			selePos.put("#", 0);
			for (int i = 0; i < resultList.size(); i++) {
				if (resultList.get(i).namePinYin.charAt(0) >= 'A'
						&& resultList.get(i).namePinYin.charAt(0) <= 'Z') {
					selePos.put(resultList.get(i).namePinYin, i);
				}
			}
//			for (Obj_PublicAccount obj_publicAccount:appObjList){
//				resultList.add(obj_publicAccount);
//			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (null != lsn) {
						for(int i=0;i<resultList.size();i++){

						}
						lsn.onResult(resultList, selePos,appObjList);
					}
				}
			});

		}
	}

	private IOnGetAccountByDB lsn = null;

	public void setLsn(IOnGetAccountByDB lsn) {
		this.lsn = lsn;
	}

	interface IOnGetAccountByDB {
		void onResult(ArrayList<Obj_PublicAccount> arrayList,
                      HashMap<String, Integer> selePos, ArrayList<Obj_PublicAccount> appList);

		void onFail();
	}



}