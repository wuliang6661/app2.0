package synway.module_publicaccount.push;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import synway.module_interface.db.SQLite;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;

class PublicInfoDeal {

	static final String getName(SQLiteDatabase db, String publicGUID) {

		String cols[] = new String[] { Table_PublicAccount.FC_NAME , Table_PublicAccount.FC_MOBILEPIC};
		String selection = Table_PublicAccount.FC_ID + "=?";
		String selectionArgs[] = new String[] { publicGUID };
		String strs = SQLite.queryOneRecord(db,
				Table_PublicAccount._TABLE_NAME, cols, "|", selection,
				selectionArgs, null);

		if (null == strs || "".equals(strs.trim())) {
			return null;
		}
		return strs;
	}

	static final void insert(SQLiteDatabase db, String publicGUID,
			String publicName, String publicCompany, String publicContact,
			String publicTel,String mobilepic) {

		ContentValues contentValues = new ContentValues();
		contentValues.put(Table_PublicAccount.FC_ID, publicGUID);
		contentValues.put(Table_PublicAccount.FC_NAME, publicName);
		contentValues.put(Table_PublicAccount.FC_COMPANY, publicCompany);
		contentValues.put(Table_PublicAccount.FC_CONTACT, publicContact);
		contentValues.put(Table_PublicAccount.FC_TEL, publicTel);
		contentValues.put(Table_PublicAccount.FC_MOBILEPIC, mobilepic);
		SQLite.insert(db, Table_PublicAccount._TABLE_NAME, null, contentValues);

	}

}