package synway.module_interface.db.table_util;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.TableUpdate;

/**
 * @author 钱园超 2015年9月29日下午5:00:06 加入自动更新接口
 */
public class Table_GroupUser implements TableUpdate {

    /**
     * 用户信息表
     */
    public static final String _tableName = "GroupUser";

    /**
     * 群组表ID，intger，pk，自增
     */
    public static final String groupUser_col_ID = "ID";

    /**
     * 群组ID，nvarchar ,
     */
    public static final String groupUser_col_groupID = "GroupID";

    /**
     * 用户ID， nvarchar
     */
    public static final String groupUser_col_userID = "UserID";

    /**
     * 用户在该群的别名， nvarchar
     */
    public static final String groupUser_col_userAlias = "UserAlias";

    public static final String groupUser_col_role = "UserRole";

//	/** 最后的已读时间， long  */
//	public static final String groupUser_col_lastReadTime = "LastReadTime";
//
//	/** 该用户是否已经退群， boolean  */
//	public static final String groupUser_col_isExistGroup = "IsExistGroup";
//
//	/** 用户入群时间， long  */
//	public static final String groupUser_col_joinGroupTime = "JoinGroupTime";
//
//	/** 用户退群时间， long  */
//	public static final String groupUser_col_exitGroupTime = "ExitGroupTime";
//
//	/** 用户在群时间段text，入群时间和退群时间用“-”分隔，时间段之间用“|”分隔  */
//	public static final String groupUser_col_inGroupPeriod = "InGroupPeriod";

    // /** 用户地区， nvarchar(50) */
    // public static final String groupUser_col_userArea = "UserArea";
    //
    // /** 群组成员图片， nvarchar(100) 用户图片，图片信息重数据库中获取，不是由服务端推送 */
    // public static final String groupUser_col_userPic = "UserPic";

    public static String getCreatTableSql() {

        return "CREATE TABLE " + Table_GroupUser._tableName + " ( " + Table_GroupUser.groupUser_col_ID
                + " integer PRIMARY KEY AUTOINCREMENT," + Table_GroupUser.groupUser_col_groupID + " nvarchar(40),"
                + Table_GroupUser.groupUser_col_userAlias + " varchar(50),"
                + Table_GroupUser.groupUser_col_role + " varchar(50),"
//				+ Table_GroupUser.groupUser_col_lastReadTime + " datetime,"
//				+Table_GroupUser.groupUser_col_isExistGroup + " boolean default false,"
//				+Table_GroupUser.groupUser_col_joinGroupTime + " datetime,"
//				+Table_GroupUser.groupUser_col_exitGroupTime + " datetime,"
//				+Table_GroupUser.groupUser_col_inGroupPeriod + " datetime,"
                + Table_GroupUser.groupUser_col_userID + " varchar(25)" + " ) ";
        // + Table_GroupUser.groupUser_col_userPic + " varchar(100)"

    }

    public static final String[] groupUserAll = {groupUser_col_ID, groupUser_col_groupID, groupUser_col_userID, groupUser_col_userAlias, groupUser_col_role
            // groupUser_col_userArea,
            // groupUser_col_userPic
    };

    private static final String v23 = "ALTER TABLE " + _tableName + " ADD COLUMN " + groupUser_col_userAlias + " varchar(50)";
    private static final String v27 = "ALTER TABLE " + _tableName + " ADD COLUMN " + groupUser_col_role + " varchar(50) ";
    private static final String v30 = "CREATE Unique INDEX [GroupUser_Index1] On [" + _tableName + "] ([" + groupUser_col_userID + "] ,[" + groupUser_col_groupID + "] )";


    //	public static final String v28_1 = "ALTER TABLE " + _tableName + " ADD COLUMN " + groupUser_col_lastReadTime + " datetime ";
//	public static final String v28_2 = "ALTER TABLE " + _tableName + " ADD COLUMN " + groupUser_col_isExistGroup + " boolean default false";
//	public static final String v28_3 = "ALTER TABLE " + _tableName + " ADD COLUMN " + groupUser_col_joinGroupTime + " datetime ";
//	public static final String v28_4 = "ALTER TABLE " + _tableName + " ADD COLUMN " + groupUser_col_exitGroupTime + " datetime";
//	public static final String v28_5 = "ALTER TABLE " + _tableName + " ADD COLUMN " + groupUser_col_inGroupPeriod + " text ";
    @Override
    public String[] update(int verson, SQLiteDatabase db) {
        if (verson == 1) {
            return new String[]{getCreatTableSql()};
        } else if (verson == 2) {
            //
        } else if (verson == 3) {
            // ....
        } else if (verson == 23) {
            return new String[]{v23};
        } else if (verson == 27) {
            return new String[]{v27};
//		} else if(verson ==28){
//			return new String[]{v28_1,v28_2,v28_3,v28_4,v28_5};
        } else if (verson == 30) {
            return new String[]{v30};
        }
        return null;
    }
    @Override
    public void delete(SQLiteDatabase db) {
        String deleteSql = "drop table " + _tableName;
        db.execSQL(deleteSql);
    }
}