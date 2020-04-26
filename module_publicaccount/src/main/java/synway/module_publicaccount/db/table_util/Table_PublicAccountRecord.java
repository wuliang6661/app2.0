package synway.module_publicaccount.db.table_util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import synway.module_interface.db.TableUpdate;

public class Table_PublicAccountRecord implements TableUpdate {

	/** 表名 */
	private static final String publicRecord_table_name = "PublicRecord_";

	public static final String getTableName(String guid) {
		if(guid!=null&&!guid.equals("")) {
			guid = guid.replace("-", "");
		}
		return publicRecord_table_name + guid;
	}

	/** ID, integer,PK,自增 */
	public static final String publicRecord_col_ID = "ID";

	/** 之前为：公众功能ID 没有任何意义，现将此字段改为公众号消息的唯一msgId*/
	public static final String publicRecord_col_GUID = "GUID";
	
	/** 消息类型， 1=消息，2=附件，3=位置，4=图文, 7=预警统计 */
	public static final String publicRecord_col_msgType = "msgType";
	
	public static final String publicRecord_col_toUser = "toUser";

	/** json 消息内容 */
	public static final String publicRecord_col_msg = "msg";

	/** 消息时间 (服务器返回), 用于显示*/
	public static final String publicRecord_col_serverTime = "serviceime";

	/** 消息时间(本地时间) */
	public static final String publicRecord_col_localeTime = "LocTime";

	/** data 数据data */
	public static final String publicRecord_col_data = "data";

	/** 消息的推送分页，该消息支持同时出现在多个分页，每个分页对应一个pageCode，其中pageCode值以”|”分割，举例(“0|1|2”)，
	 * 相关的pageCode值从配置网站获取。如果公众号没有在配置网站配置分类，则此字段为空字符串 */
	public static final String publicRecord_col_pushMsgPageCodes = "pushMsgPageCodes";
	public static final String publicRecord_col_pushMsgPageNames = "pushMsgPageNames";

	/**新增公众号消息已读未读的状态，1:已读 0:未读 默认为未读0，判断的依据:当点击进入公众号消息界面的时候，消除所有未读数的时候，
	同时将所有公众号消息置为已读状态(主要是为了删除消息时候，根据消息已读未读的状态 决定是否对未读数加一减一)*/
	public static final String publicRecord_col_isMsgRead = "isMsgRead";

	/** 
	 * 创建公众号聊天记录表， 若不存在会创建 
	 */
	public static final String getCreatTableSql(String guid) {
		return "CREATE TABLE IF NOT EXISTS " + getTableName(guid) 
				+ " ( "
				+ publicRecord_col_ID + " integer PRIMARY KEY AUTOINCREMENT,"
				+ publicRecord_col_GUID + " varchar(50),"
				+ publicRecord_col_msg + " nvarchar(1024),"
				+ publicRecord_col_toUser + " varchar(100),"
				+ publicRecord_col_serverTime + " varchar(40),"
				+ publicRecord_col_localeTime + " datetime,"
				+ publicRecord_col_msgType + " int DEFAULT 0,"
				+ publicRecord_col_data + " text, "
				+ publicRecord_col_pushMsgPageCodes+" varchar(150), "
				+ publicRecord_col_pushMsgPageNames+" varchar(150), "
				+ publicRecord_col_isMsgRead+" int DEFAULT 0 "
				+ " ) ";
	}

	private String[] VERSION46(SQLiteDatabase db) {
		ArrayList<String> arrayList = new ArrayList<String>();
		Cursor cursor = db
				.rawQuery(
						" select name from sqlite_master where type = 'table' and name like 'PublicRecord_%' ",
						null);
		while (cursor.moveToNext()) {
			// 遍历出表名
			String name = cursor.getString(0);
			//为每张表添加一个字段
			String sql = " ALTER TABLE " + name + " ADD COLUMN "+ publicRecord_col_data + " text ";
			arrayList.add(sql);
		}
		cursor.close();

		String sqls[] = new String[arrayList.size()];
		for(int i = 0; i < arrayList.size(); i++){
			sqls[i] = arrayList.get(i);
		}

		return sqls;
	}

	private String[] VERSION53(SQLiteDatabase db) {
		ArrayList<String> arrayList = new ArrayList<String>();
		Cursor cursor = db
			.rawQuery(
				" select name from sqlite_master where type = 'table' and name like 'PublicRecord_%' ",
				null);
		while (cursor.moveToNext()) {
			// 遍历出表名
			String name = cursor.getString(0);
			//为每张表添加一个字段
			String sql1 = " ALTER TABLE " + name + " ADD COLUMN "+ publicRecord_col_pushMsgPageCodes + " varchar(150) ";
			String sql2 = " ALTER TABLE " + name + " ADD COLUMN "+ publicRecord_col_pushMsgPageNames + " varchar(150) ";
			arrayList.add(sql1);
			arrayList.add(sql2);
		}
		cursor.close();

		String sqls[] = new String[arrayList.size()];
		for(int i = 0; i < arrayList.size(); i++){
			sqls[i] = arrayList.get(i);
		}
		return sqls;
	}

	private String[] VERSION54(SQLiteDatabase db) {
		ArrayList<String> arrayList = new ArrayList<String>();
		Cursor cursor = db
			.rawQuery(
				" select name from sqlite_master where type = 'table' and name like 'PublicRecord_%' ",
				null);
		while (cursor.moveToNext()) {
			// 遍历出表名
			String name = cursor.getString(0);
			//为每张表添加一个字段
			String sql = " ALTER TABLE " + name + " ADD COLUMN "+ publicRecord_col_isMsgRead + " int DEFAULT 0 ";
			arrayList.add(sql);
		}
		cursor.close();

		String sqls[] = new String[arrayList.size()];
		for(int i = 0; i < arrayList.size(); i++){
			sqls[i] = arrayList.get(i);
		}
		return sqls;
	}

	@Override
	public String[] update(int verson, SQLiteDatabase db) {
		if (verson == 46) {
			return VERSION46(db);
		}else if (verson == 53) {
			return VERSION53(db);
		}else if(verson == 54){
			return VERSION54(db);
		}
		return null;
	}

	@Override
	public void delete(SQLiteDatabase db) {
		Cursor cursor = db
				.rawQuery(
						" select name from sqlite_master where type = 'table' and name like 'PublicRecord_%' ",
						null);
		while (cursor.moveToNext()) {
			// 遍历出表名
			String name = cursor.getString(0);
			String deleteSql = "drop table " + name;
			db.execSQL(deleteSql);
		}
		cursor.close();
	}
}