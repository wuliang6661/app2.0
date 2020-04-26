package synway.module_publicaccount.public_message;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.SQLite;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_LastMsg;

class PaMsgTopIndexDeal {

	/**
	 * 获取最大置顶数
	 * 
	 * @param db
	 * @return int maxValue
	 */
	static final int GetMaxTopIndex(SQLiteDatabase db) {
		int maxValue = 0;

		String sql = "select max(" + Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX + ")  from "
				+ Table_PublicAccount_LastMsg._TABLE_NAME;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			maxValue = cursor.getInt(0);
		}
		cursor.close();

		return maxValue;
	}

	/**
	 * 置顶 联系人
	 * 
	 * @param db
	 * @param targetID
	 *            联系人ID
	 * @param topIndex
	 *            置顶 数
	 */
	static final void update(SQLiteDatabase db, String targetID, int topIndex) {
		ContentValues cvs = new ContentValues();

		cvs.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID, targetID);
		cvs.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX, topIndex);

		SQLite.update(db, Table_PublicAccount_LastMsg._TABLE_NAME, cvs,
				Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?", new String[] { targetID });

	}
	
	
	/**
	 * 取消置顶
	 * @param db
	 * @param targetID
	 */
	static final void cancelTop(SQLiteDatabase db, String targetID){
		ContentValues cvs = new ContentValues();

		cvs.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID, targetID);
		cvs.put(Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX, 0);

		SQLite.update(db, Table_PublicAccount_LastMsg._TABLE_NAME, cvs,
				Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?", new String[] { targetID });
		
		
	}

	/**
	 * 根据TargetID 获取 topIndex
	 */
	static final int getTopIndex(SQLiteDatabase db, String targetID) {
		int topIndex = 0;

		String[] columns = new String[]{Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID, Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_TOP_INDEX};
		String str = SQLite.queryOneRecord(db, Table_PublicAccount_LastMsg._TABLE_NAME, columns, "|",
				Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?", new String[]{targetID}, null);
		if(null != str){
			String[] strs = str.split("\\|",-1);
			if(strs.length != columns.length){
				return 0;
			}
			topIndex = Integer.valueOf(strs[1]);
			return topIndex;
		}

		return topIndex;
	}

}