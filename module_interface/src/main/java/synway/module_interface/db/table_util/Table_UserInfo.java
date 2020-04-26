package synway.module_interface.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

/**
 * 
 * @author 钱园超 2015年9月29日下午5:02:47 加入自动更新接口
 */
public class Table_UserInfo implements TableUpdate {

	/** 用户信息表 */
	public static final String _tableName = "UserInfo";

	/** 用户ID，nvarchar ,PK */
	public static final String userInfo_col_userID = "UserID";

	/** 用户名， nvarchar20 */
	public static final String userInfo_col_userName = "UserName";

	/** 省份， nvarchar */
	public static final String userInfo_col_userProvince = "UserProvince";

	/** 地区， nvarchar(50) */
	public static final String userInfo_col_userArea = "UserArea";

	/** 电话号码， nvarchar20 */
	public static final String userInfo_col_userPhoneNumber = "UserPhoneNumber";

	/** 电话段号, varchar(10) */
	public static final String userInfo_col_userShortNumber = "UserShortNum";
	/** 头像版本, varchar(20) */
	public static final String userInfo_col_userHeadVersion = "HeadVersion";
	/** 管理角色 目前只有两种 管理员(3) 和 普通人员(1)
	 *  为什么要设置为ManageRole 而不是UserRole 因为这个和Table_GroupUser中UserRole同名了
	 *  但是这两个UserRole的含义完全不同。因此为了区分这里命名为ManageRole
	 * */

	public static final String userInfo_col_manageRole = "ManageRole";

//	/** 最后的已读时间， long  */
//	public static final String userInfo_col_lastReadTime = "LastReadTime";
	// /** 用图片， nvarchar */
	// public static final String userInfo_col_userPic = "UserPic";

	public static final String[] userInfoAllCol = {userInfo_col_userID, userInfo_col_userName,
			userInfo_col_userProvince, userInfo_col_userArea, userInfo_col_userPhoneNumber,
			userInfo_col_userShortNumber,userInfo_col_userHeadVersion,userInfo_col_manageRole
	// userInfo_col_userPic,
	};

	public static final String getCreatTableSql() {
		return "CREATE TABLE " + _tableName + " ( " + userInfo_col_userID + " nvarchar(25) PRIMARY KEY,"
				+ userInfo_col_userName + " varchar(20)," + userInfo_col_userProvince + " varchar(20),"
//				+ userInfo_col_lastReadTime +" datetime, "
				+ userInfo_col_userArea + " varchar(50) " + " ) ";
	}

	static final String v9_1 = "ALTER  TABLE  " + _tableName + "  ADD COLUMN  " + userInfo_col_userPhoneNumber
			+ " varchar(20) ";
	static final String v9_2 = "ALTER  TABLE  " + _tableName + "  ADD COLUMN  " + userInfo_col_userShortNumber
			+ " varchar(10) ";

	static final String v16 = "ALTER  TABLE  " + _tableName + "  ADD COLUMN  " + userInfo_col_userHeadVersion
			+ " varchar(20) ";

//	static final String v28 = "ALTER  TABLE  " + _tableName + "  ADD COLUMN  " + userInfo_col_lastReadTime
//			+ " datetime ";

	static final String v42 = "ALTER  TABLE  " + _tableName + "  ADD COLUMN  " + userInfo_col_manageRole
		+ " int DEFAULT 1 ";
	@Override
	public String[] update(int verson, SQLiteDatabase db) {
		if (verson == 1) {
			return new String[]{getCreatTableSql()};
		} else if (verson == 2) {

		} else if (verson == 3) {
			// ....
		} else if (verson == 9) {
			return new String[]{v9_1, v9_2};
		}else if(verson == 16){
			return new String[]{v16};
//		}else if(verson ==28){
//			return new String[]{v28};
		}else if(verson == 42){
			return new String[]{v42};
		}
		return null;
	}
	@Override
	public void delete(SQLiteDatabase db) {
		String deleteSql = "drop table " + _tableName;
		db.execSQL(deleteSql);
	}
}
