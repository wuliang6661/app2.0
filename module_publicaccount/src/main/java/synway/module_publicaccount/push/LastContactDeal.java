package synway.module_publicaccount.push;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.SQLite;
import synway.module_interface.db.table_util.Table_LastContact;

class LastContactDeal {

	static final int getUnReadCount(SQLiteDatabase db, String targetID) {

		String selectnList[] = { Table_LastContact.UN_READ_COUNT };

		String dbResult = SQLite.queryOneRecord(db,
				Table_LastContact._TABLE_NAME, selectnList, "|",
				Table_LastContact.TARGET_ID + "=?", new String[] { targetID },
				null);

		if (null == dbResult) {
			return 0;
		}

		try {
			int result = Integer.valueOf(dbResult);
			if (result <= 0) {
				result = 0;
			}
			return result;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}

	}

}
