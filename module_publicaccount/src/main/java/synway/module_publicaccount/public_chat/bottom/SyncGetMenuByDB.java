package synway.module_publicaccount.public_chat.bottom;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import java.util.ArrayList;

import synway.module_interface.db.SQLite;
import synway.module_publicaccount.R;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.public_chat.Obj_Menu;

import static synway.module_publicaccount.until.ConfigUtil.PUB_CJPT_GUID;

public class SyncGetMenuByDB {
	private Handler handler = null;
	public IOnGetMenuByDB lsn = null;
	/**  */
	private ArrayList<Obj_Menu> firstMenu = null;
	/**  */
	private ArrayList<Obj_Menu> secondMenu = null;
    private Context context;
	public SyncGetMenuByDB(Context context) {
		this.context=context;
		handler = new Handler();
	}

	// gвactе SQLiteDatabase,
	// act onDestorySQLiteDatabase
	// gпεеIJ
	// ′ act Contextgп’SQLiteDatabaseapplicationK
	public void start(String ID) {
		new Thread(new mRunnbale(ID)).start();
	}

	public void stop() {
		this.lsn = null;
	}

	private class mRunnbale implements Runnable {

		private String ID = null;

		private mRunnbale(String ID) {
			this.ID = ID;
		}

		@Override
		public void run() {
			String tableName = Table_PublicMenu._TABLE_NAME;
			String columns[] = new String[] { Table_PublicMenu.PAM_ID,
					Table_PublicMenu.PAM_menuGUID,
					Table_PublicMenu.PAM_menuName,
					Table_PublicMenu.PAM_menuFather,
					Table_PublicMenu.PAM_menuKey,
					Table_PublicMenu.PAM_menuType,
					Table_PublicMenu.PAM_menuUrl,
					Table_PublicMenu.PAM_menuPicId,
					Table_PublicMenu.PAM_menuUrlType,
					Table_PublicMenu.PAM_ISSHowTitle,
					Table_PublicMenu.PAC_urlParam
			 };

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
				String s=e.toString();
				return;
			} finally {
				// db.close();
			}

			firstMenu = new ArrayList<Obj_Menu>();
			secondMenu = new ArrayList<Obj_Menu>();
			if(ID.equals(PUB_CJPT_GUID)){
				Obj_Menu obj_menu=getBlueToothMenu();
				firstMenu.add(obj_menu);
			}
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
				obj_Menu.isShowTitle=Integer.parseInt(sList[9]);
				obj_Menu.sourceUrl=sList[10];
				if (obj_Menu.ID.equals(this.ID)) {
					if (obj_Menu.menuType == 1) {
						firstMenu.add(obj_Menu);
					} else {
						secondMenu.add(obj_Menu);
					}
				}
			}

			if (firstMenu.size() == 0) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (null != lsn) {
							lsn.onFail("", "^u", " resultList.size() == 0 ");
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
	private Obj_Menu getBlueToothMenu(){
		Obj_Menu obj_menu=new Obj_Menu();
		obj_menu.menuName=context.getString(R.string.receiveBlueToothFile);
		return obj_menu;
	}
	public void setLsn(IOnGetMenuByDB lsn) {
		this.lsn = lsn;
	}

	public interface IOnGetMenuByDB {
		void onResult(ArrayList<Obj_Menu> firstMenuList,
                      ArrayList<Obj_Menu> secondMenuList);

		void onFail(String title, String reason, String detail);
	}
}
