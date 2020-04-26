package synway.module_publicaccount.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

public class Table_PublicAccount implements TableUpdate{

	public static final String _TABLE_NAME = "PublicAccountList";
	
	public static final String FC_ID = "ID";
	
	public static final String FC_NAME = "Name";
	
	public static final String FC_COMPANY = "Company";
	
	public static final String FC_CONTACT = "Contact";
	
	public static final String FC_TEL = "Tel";
	//服务端公众号图标名字
	public static final String FC_MOBILEPIC="mobilepic";
	//type :0 默认旧版公众号类型;1:app类型;2:新版只有一个主入口的公众号,具体跳转url和url类型在Table_PublicConfigMsg表
	public static final String FC_TYPE="type";
	public static final String APP_INFORMATION="app_information";

	public static final String PAM_FatherGroupID = "fatherGroupID";
	public static final String PAM_FatherGroupName = "fatherGroupName";

	public static final String PAM_PushMsgTypeList = "pushMsgTypeList";


	public static final String verson20= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + FC_MOBILEPIC + " varchar(100)";
	public static final String verson34= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + FC_TYPE + " varchar(40)";
	public static final String verson342= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + APP_INFORMATION + " text";

	public static final String verson491 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_FatherGroupID + " varchar(40)" ;
	public static final String verson492 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_FatherGroupName + " varchar(40)" ;
	public static final String verson493 = "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + PAM_PushMsgTypeList + " varchar(200)" ;


	public static final String getCreatTableSql(){
		return "CREATE TABLE IF NOT EXISTS " + _TABLE_NAME + " ( " 
				+ FC_ID + " varchar(40) PRIMARY KEY ,"
				+ FC_NAME + " varchar(100),"
				+ FC_COMPANY + " varchar(40),"
				+ FC_CONTACT + " varchar(10),"
				+ FC_TEL + " varchar(20)"
				+ " ) ";
	}

	@Override
	public String[] update(int verson, SQLiteDatabase db) {
		if(verson == 1){
			
		}else if(verson == 2){
			
		}else if(verson == 3){
			
		}else if(verson == 4){
			return new String[]{getCreatTableSql()};
		}else if(verson==20){
			return new String[]{verson20};
		}else if(verson==34){
			return new String[]{verson34,verson342};
		}else if(verson ==49){
			return new String[]{verson491,verson492,verson493};
		}
		return null;
	}
	@Override
	public void delete(SQLiteDatabase db) {
		String deleteSql = "drop table " + _TABLE_NAME;
		db.execSQL(deleteSql);
	}
}