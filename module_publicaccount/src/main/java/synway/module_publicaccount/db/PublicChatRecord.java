package synway.module_publicaccount.db;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.SQLite;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;


public class PublicChatRecord {

	public static final String[] read(SQLiteDatabase db,
			String targetID, int startIndex, int count) {
		String tableName = Table_PublicAccountRecord.getTableName(targetID);

		String cols[] = new String[] {
				Table_PublicAccountRecord.publicRecord_col_ID,
				Table_PublicAccountRecord.publicRecord_col_GUID,
				Table_PublicAccountRecord.publicRecord_col_localeTime,
				Table_PublicAccountRecord.publicRecord_col_msg,
				Table_PublicAccountRecord.publicRecord_col_serverTime };

		String queryList[] = SQLite
				.query(db, tableName, cols, "|", null, null,
						Table_PublicAccountRecord.publicRecord_col_localeTime
								+ " desc", startIndex + "," + count);
		
		return queryList;
	}


}