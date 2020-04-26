package synway.module_interface.groupinterface;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import synway.module_interface.db.table_util.Table_GroupUser;
import synway.module_interface.db.table_util.Table_UserInfo;

/**
 * Created by zjw on 2017/6/22.
 * 获取群角色
 * 注意获取角色变更的广播
 */

public class GroupRole {
    /**
     *群角色变更广播
     * */
    public static class SetRole{
        public static final String GROUP_ID = "pushUtil.setRole.groupID";
        public static final String USER_ID_LIST = "pushUtil.setRole.userIDList";
        public static final String USER_ROLE_LIST = "pushUtil.setRole.userIDList";

        static final String ACTION = "pushUtil.setRole_";
        public static String getRoleAction(String targetID) {
            return ACTION + targetID;
        }
    }
    /**
    *多角色查询
    * */
    public static ArrayList<RoleObj> getRoles(SQLiteDatabase db,String groupID, ArrayList<String> userIDList){
        ArrayList<String> arrayList = new ArrayList<String>();
        String table = Table_GroupUser._tableName + ","
                + Table_UserInfo._tableName;
        String col = Table_GroupUser._tableName + "."
                + Table_GroupUser.groupUser_col_groupID + " as gGroupID ,"
                + Table_GroupUser._tableName + "."
                + Table_GroupUser.groupUser_col_userID + " as gUereID,"
                + Table_GroupUser.groupUser_col_role + " as userRole";
        String where = " " + Table_GroupUser._tableName + "."
                + Table_GroupUser.groupUser_col_userID + " = "
                + Table_UserInfo._tableName + "."
                + Table_UserInfo.userInfo_col_userID + " "
                + " and " + Table_GroupUser.groupUser_col_groupID + "= '" + groupID+ "' ";
        String orderBy = " order by " + Table_GroupUser.groupUser_col_ID + " asc ";
        String sql = "select " + col + " from " + table + " where " + where + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            StringBuffer sb = new StringBuffer();
            sb.append(cursor.getString(cursor.getColumnIndex("gGroupID")));
            sb.append("|");
            sb.append(cursor.getString(cursor.getColumnIndex("gUereID")));
            sb.append("|");
            sb.append(cursor.getString(cursor.getColumnIndex("userRole")));
            arrayList.add(sb.toString());
        }
        cursor.close();
        if (arrayList.size() == 0) {
            return null;
        }
        ArrayList<RoleObj> objList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            String strs[] = arrayList.get(i).split("\\|", -1);
            if (strs.length < 4) {
                continue;
            }
            RoleObj roleObj = new RoleObj();
            roleObj.groupID = strs[0];
            roleObj.userID = strs[1];
            roleObj.userRole = strs[2];
            objList.add(roleObj);
        }
        return objList;
    }
    /**
     *单角色查询,该userID不存在返回null，该用户角色为空 返回的角色字段为"";
     * */
    public static RoleObj getSingleRole(SQLiteDatabase db,String groupID,String userID){
        ArrayList<RoleObj> arrayList = new ArrayList<>();
        String table = Table_GroupUser._tableName + ","
                + Table_UserInfo._tableName;
        String col = Table_GroupUser._tableName + "."
                + Table_GroupUser.groupUser_col_groupID + " as gGroupID ,"
                + Table_GroupUser._tableName + "."
                + Table_GroupUser.groupUser_col_userID + " as gUereID,"
                + Table_GroupUser.groupUser_col_role + " as userRole";
        String where = " " + Table_GroupUser._tableName + "."
                + Table_GroupUser.groupUser_col_userID + " = "
                + userID+ " "
                + " and " + Table_GroupUser.groupUser_col_groupID + "= '" + groupID+ "' ";
        String sql = "select " + col + " from " + table + " where " + where;
        Cursor cursor = db.rawQuery(sql, null);
        RoleObj roleObj = new RoleObj();
        while (cursor.moveToNext()) {
            roleObj.groupID = cursor.getString(cursor.getColumnIndex("gGroupID"));
            roleObj.userID = cursor.getString(cursor.getColumnIndex("gUereID"));
            roleObj.userRole = cursor.getString(cursor.getColumnIndex("userRole"));
            arrayList.add(roleObj);
        }
        cursor.close();
        if (arrayList.size() == 0) {
            return null;
        }
        return roleObj;
    }
}
