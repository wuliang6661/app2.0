package synway.module_publicaccount.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

public class Table_PublicAccount_Gis implements TableUpdate{

	public static final String _TABLE_NAME = "PublicAccount_Gis";
	public static final String USER_ID = "ID";//用戶ID
	public static final String USER_NAME = "Name";//用戶名字
	public static final String DataType="DataType"; //数据类型 1=普通数据，2=信标数据（设备数据）
	public static final String GISTYPE="GisType"; //GIS类型：1=敌方，2=友方
	public static final String USER_GROUP = "UserGroup";//用戶分組
	public static final String USER_PIC = "PicUrl";//用户头像地址
	public static final String  EQUIP_TYPE= "equip_type";//设备类型
//	public static final String POINT = "Point";//用户最新轨迹点
	public static final String TIME = "Time";  //最新轨迹点时间
    public static final String X="x";//
	public static final String Y="y";
	public static final String verson39= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + DataType + " int";
	public static final String verson39_2= "ALTER  TABLE  " + _TABLE_NAME + "  ADD COLUMN  " + EQUIP_TYPE + " String";
	public static final String getCreatTableSql(){
		return "CREATE TABLE IF NOT EXISTS " + _TABLE_NAME + " ( " 
				+ USER_ID + " varchar(40) PRIMARY KEY ,"
				+ USER_NAME + " varchar(100),"
				+ USER_GROUP + " varchar(40),"
				+ USER_PIC + " varchar(200),"
				+ X + " varchar(200),"
				+ Y + " varchar(200),"
				+ GISTYPE + " int,"
				+TIME+" datetime"
				+ " ) ";
	}

	@Override
	public String[] update(int verson, SQLiteDatabase db) {
         if(verson == 24){
			return new String[]{getCreatTableSql()};
		}else if(verson==39){
         	return  new String[]{verson39,verson39_2};
		 }
		return null;
	}
	@Override
	public void delete(SQLiteDatabase db) {
		String deleteSql = "drop table " + _TABLE_NAME;
		db.execSQL(deleteSql);
	}
}