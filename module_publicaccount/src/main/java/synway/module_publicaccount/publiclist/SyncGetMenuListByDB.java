package synway.module_publicaccount.publiclist;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import java.util.ArrayList;

import synway.module_interface.db.SQLite;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.public_chat.Obj_Menu;

public class SyncGetMenuListByDB {
	private Handler handler = null;
	public IOnGetMenuListByDB lsn = null;
	/**  */
	private ArrayList<Obj_Menu> firstMenu = null;
	/**  */
	private ArrayList<Obj_Menu> secondMenu = null;

	public SyncGetMenuListByDB() {
		handler = new Handler();
	}

	// gвactе SQLiteDatabase,
	// act onDestorySQLiteDatabase
	// gпεеIJ
	// ′ act Contextgп’SQLiteDatabaseapplicationK
	public void start(ArrayList<Obj_PublicAccount> arrayList) {
		new Thread(new mRunnbale(arrayList)).start();
	}

	public void stop() {
		this.lsn = null;
	}

	private class mRunnbale implements Runnable {

		private ArrayList<Obj_PublicAccount> arrayList = null;

		private mRunnbale(ArrayList<Obj_PublicAccount> arrayList) {
			this.arrayList = arrayList;
		}

		@Override
		public void run() {
			String tableName = Table_PublicMenu._TABLE_NAME;
			String columns[] = new String[] { Table_PublicMenu.PAM_ID,
					Table_PublicMenu.PAM_menuGUID,
					Table_PublicMenu.PAM_menuName,
					Table_PublicMenu.PAM_menuFather,
					Table_PublicMenu.PAM_menuKey,
					Table_PublicMenu.PAM_menuType, Table_PublicMenu.PAM_menuUrl , Table_PublicMenu.PAM_menuPicId,Table_PublicMenu.PAM_menuUrlType};

			// SQLiteDatabase db = null;
			// try {
			// db = SQLiteDatabase.openDatabase(
			// BaseUtil.getDBPath(MainApp.getInstance()), null,
			// SQLiteDatabase.OPEN_READWRITE);
			// } catch (Exception e) {
			// ThrowExp.throwRxp("SyncGetPublicByDB ");
			// }
			SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();

			String strs[];
			try {
				strs = SQLite.query(sqliteHelp.getWritableDatabase(),
						tableName, columns, "|", null, null,
						Table_PublicMenu.PAM_ID + " desc ");
			} catch (Exception e) {
				// MLog.Log("qsjh", "!");
				return;
			} finally {
				// db.close();
			}
			for(int j=0;j<this.arrayList.size();j++){
				firstMenu = new ArrayList<Obj_Menu>();
				secondMenu = new ArrayList<Obj_Menu>();
			for (int i = 0; i < strs.length; i++) {
				String sList[] = strs[i].split("\\|", -1);
				Obj_Menu obj_Menu = new Obj_Menu();
				obj_Menu.ID = sList[0];
				obj_Menu.menuGUID = sList[1];
				obj_Menu.menuName = sList[2];
				obj_Menu.menuFather = sList[3];
				obj_Menu.menuKey = sList[4];
				obj_Menu.menuType = Integer.parseInt(sList[5]);
				obj_Menu.menuUrl = sList[6];
				obj_Menu.menuPicUrl=sList[7];
				obj_Menu.menuUrlType = Integer.parseInt(sList[8]);
				if (obj_Menu.ID.equals(this.arrayList.get(j).ID)) {
						Obj_PublicAccount obj_publicAccount=arrayList.get(j);
					if (obj_Menu.menuFather == null || obj_Menu.menuFather.equals("")) {
						firstMenu.add(obj_Menu);
					} else {
						secondMenu.add(obj_Menu);
					}
						obj_publicAccount.firstmenus=firstMenu;
						obj_publicAccount.secondmenus=secondMenu;
						arrayList.set(j,obj_publicAccount);
					}
				}

			}

//			if (firstMenu.size() == 0) {
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//						if (null != lsn) {
//							lsn.onFail("", "^u", " resultList.size() == 0 ");
//						}
//					}
//				});
//				return;
//			}

			handler.post(new Runnable() {
				@Override
				public void run() {
					if (null != lsn) {
						lsn.onResult(arrayList);
					}
				}
			});

		}

	}

	public void setLsn(IOnGetMenuListByDB lsn) {
		this.lsn = lsn;
	}

	public interface IOnGetMenuListByDB {
		void onResult(ArrayList<Obj_PublicAccount> arraylist);

		void onFail(String title, String reason, String detail);
	}
}
