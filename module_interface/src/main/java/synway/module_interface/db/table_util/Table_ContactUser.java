package synway.module_interface.db.table_util;

import android.database.sqlite.SQLiteDatabase;
import synway.module_interface.db.TableUpdate;

/***
 * 通讯录通讯录
 * 
 * @author 刘杰 2015年7月31日上午11:52:53
 * @author 钱大爷 2015年9月29日下午16:58:53 加入自动更新接口
 */
public class Table_ContactUser implements TableUpdate {

	/** 通讯录ID，nvarchar ,PK */
	public static final String contactUser_col_userID = "UserID";

	/** 通讯录所属机构ID , String */
	public static final String contactUser_col_organId = "OrganId";

	/** 通讯录信息表 */
	public static final String table_name = "ContactUser";

	public static final String[] contactUserAllCol = {contactUser_col_userID, contactUser_col_organId};

//	public static final String getCreatTableSql() {
//		return "CREATE TABLE " + table_name + " ( " + contactUser_col_userID + " nvarchar(25) PRIMARY KEY,"
//				+ contactUser_col_organId + " integer " + " ) ";
//	}

	public static final String getCreatTableSql() {
		return "CREATE TABLE " + table_name + " ( " + contactUser_col_userID + " nvarchar(25) PRIMARY KEY,"
				+ contactUser_col_organId + " varchar(50) " + " ) ";
	}

	private void version5(SQLiteDatabase db){
		String oldTableName = table_name + "_old";
		String sqlRename = "ALTER TABLE "+table_name+ " RENAME TO " + oldTableName;
		db.execSQL(sqlRename);

		String createNewTable = getCreatTableSql();
		db.execSQL(createNewTable);
		
		String removeData =  "INSERT INTO " + table_name 
				+" ('" +contactUser_col_userID+ "', '" +contactUser_col_organId +"' )"
				+ " select " + contactUser_col_userID +", " + contactUser_col_organId
				+ " from " + oldTableName;
		db.execSQL(removeData);

		String deleteSql = "drop table " + oldTableName;
		db.execSQL(deleteSql);
	}

	@Override
	public String[] update(int verson,SQLiteDatabase db) {
		if (verson == 1) {
			return new String[]{getCreatTableSql()};
		} else if (verson == 5) {
			version5(db);
		} 
		return null;
	}
	@Override
	public void delete(SQLiteDatabase db) {
		String deleteSql = "drop table " + table_name;
		db.execSQL(deleteSql);
	}

}
