//package synway.module_publicaccount.push;
//
//import android.database.sqlite.SQLiteDatabase;
//
//import synway.module_interface.db.SQLite;
//import synway.module_interface.db.table_util.Table_GroupInfo;
//
//class GroupInfoDeal {
//
//	static final String getGroupName(SQLiteDatabase db, String groupID) {
//
//		String strsColumns[] = { Table_GroupInfo.groupInfo_col_groupID,
//				Table_GroupInfo.groupInfo_col_groupName };
//
//		String result = SQLite.queryOneRecord(db, Table_GroupInfo.table_name,
//				strsColumns, "|", Table_GroupInfo.groupInfo_col_groupID + "=?",
//				new String[] { groupID }, null);
//
//		if (result == null) {
//			return null;
//		}
//
//		String strs[] = result.split("\\|", -1);
//		if (strs.length != strsColumns.length) {
//			return null;
//		}
//
//		return strs[1];
//
//	}
//
//}
