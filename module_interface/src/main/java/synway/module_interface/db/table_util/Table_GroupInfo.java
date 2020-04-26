package synway.module_interface.db.table_util;

import android.database.sqlite.SQLiteDatabase;
import synway.module_interface.db.TableUpdate;

/**
 * @author 钱园超 2015年9月29日下午4:59:24 加入自动更新接口
 */
public class Table_GroupInfo implements TableUpdate {

	/** 群组ID，nvarchar(40) pk */
	public static final String groupInfo_col_groupID = "GroupID";

	/** 群组名， nvarchar(100) */
	public static final String groupInfo_col_groupName = "GroupName";

	/** 群组包含的地区，多个地区用'；'隔开， nvarchar(512) */
	public static final String groupInfo_col_groupArea = "GroupArea";

	/** 创建时间 ， datetime */
	public static final String groupInfo_col_groupCreateTime = "GroupCreateTime";

	/** 是否保存到通讯录 ， int  */
	public static final String groupInfo_col_isSave = "IsSave";

	/** 是否被删除 ， int */
	public static final String groupInfo_col_isDelete = "IsDelete";

	/** 群主ID ，String */
	public static final String groupInfo_col_masterID = "GroupMasterID";

	/** 群角色可设置列表,各角色用符号'#%'隔开*/
	public static final String groupInfo_col_roleList="RoleList";

	/** 群关联的指挥部ID*/
	public static final String groupInfo_col_commandID = "CommandID";

	/** 群组信息表 */
	public static final String table_name = "GroupInfo";

	public static final String[] groupInfoAllCol = {groupInfo_col_groupID, groupInfo_col_groupName,
			groupInfo_col_groupArea, groupInfo_col_groupCreateTime,groupInfo_col_masterID,groupInfo_col_roleList,groupInfo_col_commandID};

	public static final String getCreatTableSql() {
		return "CREATE TABLE " + table_name + " ( "
				+ groupInfo_col_groupID + " nvarchar(40) PRIMARY KEY,"
				+ groupInfo_col_groupName + " varchar(100),"
				+ groupInfo_col_groupArea + " nvarchar(512),"
				+ groupInfo_col_roleList +" text, "
				+ groupInfo_col_groupCreateTime + " datetime"
				+ groupInfo_col_commandID + " nvarchar(40)"
				+ " ) ";
	}

	@Override
	public String[] update(int verson,SQLiteDatabase db) {
		if (verson == 1) {
			return new String[]{getCreatTableSql()};
		} else if (verson == 2) {

		} else if (verson == 3) {
			// ....
		}else if(verson==7){
			return new String[]{v7_1};
		}else if(verson==8){
			return new String[]{v8_1};
		} else if (verson == 20) {
			return new String[]{v20};
		} else if(verson ==27){
			return new String[]{v27};
		} else if(verson == 31){
            return new String[]{v31};
        }
		return null;
	}
	@Override
	public void delete(SQLiteDatabase db) {
		String deleteSql = "drop table " + table_name;
		db.execSQL(deleteSql);
	}
	public static final String v7_1 = "ALTER  TABLE  "+table_name+"  ADD COLUMN  " + groupInfo_col_isSave + " SMALLINT ";
	public static final String v8_1 = "ALTER  TABLE  "+table_name+"  ADD COLUMN  " + groupInfo_col_isDelete + " SMALLINT ";
	public static final String v20 = "ALTER  TABLE  "+table_name+"  ADD COLUMN  " + groupInfo_col_masterID + " varchar(40) ";
	public static final String v27 = "ALTER  TABLE  "+table_name+"  ADD COLUMN  " + groupInfo_col_roleList + " text ";
	public static final String v31 = "ALTER  TABLE  "+table_name+"  ADD COLUMN  " + groupInfo_col_commandID + " varchar(40) ";
}
