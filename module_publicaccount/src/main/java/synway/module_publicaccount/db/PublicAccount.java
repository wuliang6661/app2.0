package synway.module_publicaccount.db;

import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.SQLite;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;

/**
 * Created by QSJH on 2016/6/17 0017.
 */
public class PublicAccount {
    public static String getName(SQLiteDatabase db, String publicAccountID) {
        String[] name = SQLite.query(db, Table_PublicAccount._TABLE_NAME,
                new String[]{Table_PublicAccount.FC_NAME}, "|",
                Table_PublicAccount.FC_ID + "=?", new String[]{publicAccountID}, null);
        return name.length > 0 ? name[0] : "";
    }
}
