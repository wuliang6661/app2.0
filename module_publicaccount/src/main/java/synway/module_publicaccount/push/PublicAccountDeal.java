package synway.module_publicaccount.push;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.SQLite;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;

class PublicAccountDeal {

	static final String getPAName(SQLiteDatabase db, String targetID) {

		String cols[] = new String[] { Table_PublicAccount.FC_NAME };
		String selectionArgs[] = new String[] { targetID };

		String str = SQLite.queryOneRecord(db, Table_PublicAccount._TABLE_NAME,
				cols, "|", Table_PublicAccount.FC_ID + "=?", selectionArgs,
				null);

		if (null == str || "".equals(str.trim())) {
			return null;
		}

		return str;
	}

}
