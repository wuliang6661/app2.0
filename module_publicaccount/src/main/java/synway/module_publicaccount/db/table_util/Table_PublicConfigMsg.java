package synway.module_publicaccount.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

public class Table_PublicConfigMsg implements TableUpdate {

	public static final String _TABLE_NAME = "PublicAccountConfigMsg";
	/******************** ID为公众号ID  ************/
	public static final String PAC_ID = "ID";
	public static final String PAC_SourceUrl = "sourceUrl";

	//公众号类型type=2 时，点击公众号直接跳转的url类型 0 html，1 weex
	public static final String PAM_PublicUrlType = "publicUrlType";
	//点击跳转后的url
	public static final String PAM_PublicUrl = "publicUrl";

	public static final String PAM_ISSHowTitle = "isShowTitle";
	//公众号新消息提示铃声设置
	public static final String PAM_RingUri = "ringUri";

	//公众号新消息提示震动设置
	public static final String PAM_IsOpenVibrate = "isOpenVibrate";

	public static final String verson471 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_PublicUrlType + " int DEFAULT 0" ;
	public static final String verson472 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_PublicUrl + " varchar(40)" ;

	public static final String verson50 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_ISSHowTitle + " int DEFAULT 1" ;
	public static final String verson501 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_RingUri + " varchar(100)" ;
	public static final String verson51 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_IsOpenVibrate + " int DEFAULT 1" ;


	public static final String getCreatTableSql() {
		return "CREATE TABLE IF NOT EXISTS " + _TABLE_NAME + " ( " + PAC_ID
				+ " varchar(40) PRIMARY KEY ," +  PAC_SourceUrl + "  nvarchar(1024)" + " ) ";
	}


	@Override
	public String[] update(int version, SQLiteDatabase db) {
		if (version == 44) {
			return new String[] { getCreatTableSql() };
		}else if(version == 47){
			return new String[]{verson471,verson472};
		}else if(version ==50){
			return new String[]{verson50,verson501};
		}else if(version ==51){
			return new String[]{verson51};
		}
		return null;
	}
	@Override
	public void delete(SQLiteDatabase db) {
		String deleteSql = "drop table " + _TABLE_NAME;
		db.execSQL(deleteSql);
	}

}
