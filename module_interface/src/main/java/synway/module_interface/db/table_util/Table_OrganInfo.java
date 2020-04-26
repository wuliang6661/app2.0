package synway.module_interface.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

/**
 * 
 * @author 钱园超 2015年9月29日下午5:02:09 加入自动更新接口
 */
public class Table_OrganInfo implements TableUpdate {

	public static final String organInfo_table_name = "OrganInfo";

//	/** integer,pk */
//	public static final String organInfo_col_orgainInfoId = "OrganInfoID";

	/**integer ,pk自增长 */
	public static final String organInfo_col_ID = "ID";

	/** String */
	public static final String organInfo_col_orgainInfoId = "OrganInfoID";

	
	/** 机构名称，nvarchar(50) */
	public static final String organInfo_col_orgainInfoName = "OrganInfoName";

	/** 该机构的父级机构Id，String,默认值为0 */
	public static final String organInfo_col_orgainInfoFather = "OrganInfoFather";

	public static final String orgainInfoAllCol[] = {organInfo_col_orgainInfoId, organInfo_col_orgainInfoName,
			organInfo_col_orgainInfoFather};

//	public static final String getCreatTableSql() {
//		return "CREATE TABLE " + organInfo_table_name + " ( " + organInfo_col_orgainInfoId + " integer PRIMARY KEY,"
//				+ organInfo_col_orgainInfoName + " varchar(50)," + organInfo_col_orgainInfoFather
//				+ " integer DEFAULT 0 " + " ) ";
//	}
	
	public static final String getCreatTableSql() {
		return "CREATE TABLE " + organInfo_table_name 
				+ " ( " 
				+ organInfo_col_ID + " integer PRIMARY KEY AUTOINCREMENT,"
				+ organInfo_col_orgainInfoId + " varchar(50),"
				+ organInfo_col_orgainInfoName + " varchar(50)," +
				organInfo_col_orgainInfoFather + " varchar(50) DEFAULT 0 " 
				+ " ) ";
	}

	//因为SQLite不能完全支持SQL语句，只能增加字段，不能drop字段，所以，要修改某表的某字段数据类型，只能
	//先将表重命名 
	//将旧表的内容插入到新表中
	//删除旧表
	private void verson5(SQLiteDatabase db){

		String oldTableName = organInfo_table_name + "_old";
		String sqlRename = "ALTER TABLE "+organInfo_table_name+ " RENAME TO " + oldTableName;
		db.execSQL(sqlRename);

		String createNewTable = getCreatTableSql();
		db.execSQL(createNewTable);
		
		String removeData =  "INSERT INTO " + organInfo_table_name 
				+" ('" +organInfo_col_orgainInfoId+ "', '" +organInfo_col_orgainInfoName +"', '" +organInfo_col_orgainInfoFather+ "' )"
				+ " select " + organInfo_col_orgainInfoId +", " + organInfo_col_orgainInfoName + ", " + organInfo_col_orgainInfoFather
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
			verson5(db);
		}
		return null;
	}
	@Override
	public void delete(SQLiteDatabase db) {
		String deleteSql = "drop table " + organInfo_table_name;
		db.execSQL(deleteSql);
	}
}
