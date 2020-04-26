package synway.module_publicaccount.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

public class Table_PublicMenu implements TableUpdate {

	public static final String _TABLE_NAME = "PublicAccountMenu";
	/******************** ID为公众号ID menuGUID为菜单ID ************/
	public static final String PAM_ID = "ID";
	public static final String PAM_menuGUID = "menuGUID";
	public static final String PAM_menuName = "menuName";
	public static final String PAM_menuFather = "menuFather";
	public static final String PAM_menuKey = "menuKey";
	public static final String PAM_menuType = "menuType";
	//点击跳转后的url
	public static final String PAM_menuUrl = "menuUrl";
    public static final String PAM_menuPicId="menuPicId";
    //菜单栏url类型 0 html，1 weex
    public static final String PAM_menuUrlType = "menuUrlType";
	//url参数
	public static final String PAC_urlParam = "urlParam";

	public static final String PAM_ISSHowTitle = "isShowTitle";
//    public static final String PAM_menuServiceUrl = "menuServiceUrl";
//    public static final String PAM_menuResourceUrl = "menuResourceUrl";
	public static final String verson20= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_menuPicId + " varchar(100)";
	public static final String verson41= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_menuUrlType + " int DEFAULT 0";

	public static final String verson50 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAC_urlParam + " nvarchar(1024)" ;
	public static final String verson501= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_ISSHowTitle + " int DEFAULT 1";

//	public static final String verson44= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_menuServiceUrl + " varchar(40)";
//	public static final String verson44_２= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_menuResourceUrl + " varchar(40)";


	public static final String getCreatTableSql() {
		return "CREATE TABLE IF NOT EXISTS " + _TABLE_NAME + " ( " + PAM_ID
				+ " varchar(40) PRIMARY KEY ," + PAM_menuGUID
				+ " varchar(40) ," + PAM_menuName + " varchar(40),"
				+ PAM_menuFather + " varchar(40)," + PAM_menuKey
				+ " varchar(40)," + PAM_menuType + " varchar(40),"
				+ PAM_menuUrl + " varchar(40)" + " ) ";
	}

	// public static final String

	// 删除并重新创建表，目的是为了去掉ID上的主键
	private void version12(SQLiteDatabase db) {
		String deleteTableSQL = "drop table IF EXISTS PublicAccountMenu";
		db.execSQL(deleteTableSQL);
		
		String reCreateSQL= "CREATE TABLE IF NOT EXISTS " + _TABLE_NAME + " ( " + PAM_ID
				+ " varchar(40) ," + PAM_menuGUID
				+ " varchar(40) ," + PAM_menuName + " varchar(40),"
				+ PAM_menuFather + " varchar(40)," + PAM_menuKey
				+ " varchar(40)," + PAM_menuType + " varchar(40),"
				+ PAM_menuUrl + " varchar(40)" + " ) ";
		db.execSQL(reCreateSQL);
	}

	@Override
	public String[] update(int version, SQLiteDatabase db) {
		if (version == 6) {
			return new String[] { getCreatTableSql() };
		} else if (version == 12) {
			version12(db);
			return null;
		}else if(version==20){
				return new String[]{verson20};
		}else if (version==41){
			return new String[]{verson41};
		}
		else if (version==50){
			return  new String[]{verson50,verson501};
		}
		return null;
	}
	@Override
	public void delete(SQLiteDatabase db) {
		String deleteSql = "drop table " + _TABLE_NAME;
		db.execSQL(deleteSql);
	}
}
