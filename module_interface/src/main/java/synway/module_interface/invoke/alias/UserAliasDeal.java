package synway.module_interface.invoke.alias;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.SQLite;
import synway.module_interface.db.table_util.Table_GroupUser;
import synway.module_interface.db.table_util.Table_UserInfo;

/**
 * Created by zjw on 2017/3/15.
 */

public class UserAliasDeal {
    //如果userName和alias 为空,则 对应字段为null
    public static UserAliasObj getUserName(SQLiteDatabase db,String groupID,String userID){
        String alias = read(db,groupID,userID);
        String realName = readRealName(db,userID);
        UserAliasObj userAliasObj = new UserAliasObj();
        userAliasObj.userID = userID;
        userAliasObj.groupID =groupID;
        userAliasObj.userName = realName;
        userAliasObj.alias = alias;
        return userAliasObj;
    }

    // 查询Alias
    public static String read(SQLiteDatabase db,String groupID,String userID) {
        String table = Table_GroupUser._tableName;
        String col = Table_GroupUser._tableName + "."
                + Table_GroupUser.groupUser_col_groupID + " as gGroupID ,"
                + Table_GroupUser._tableName + "."
                + Table_GroupUser.groupUser_col_userID + " as gUereID,"
                + Table_GroupUser.groupUser_col_userAlias + " as userAlias ";
        String where = Table_GroupUser.groupUser_col_groupID + "= '" + groupID+ "' ";
        String sql = "select " + col + " from " + table + " where " + where ;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            if(userID.equals(cursor.getString(cursor.getColumnIndex("gUereID")))){
                String alias = cursor.getString(cursor.getColumnIndex("userAlias"));
                if(alias!=null&&!alias.equals("")) {
                    return alias;
                }else{
                    return null;
                }
            }
        }
        cursor.close();
        return null;
    }

    public static final String readRealName(SQLiteDatabase db, String userID) {
        String strUserList = SQLite.queryOneRecord(db, Table_UserInfo._tableName, Table_UserInfo.userInfoAllCol, "|",
                Table_UserInfo.userInfo_col_userID + "=?", new String[]{userID + ""}, null);
        if (strUserList == null) {
            return null;
        }

        String strs[] = strUserList.split("\\|", -1);
        if (strs.length != Table_UserInfo.userInfoAllCol.length) {
            return null;
        }

        String userName = strs[1];

        return userName;
    }

}
