package synway.module_publicaccount.push;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;

import synway.module_publicaccount.db.table_util.Table_PublicConfigMsg;
import synway.module_publicaccount.until.StringUtil;

class GetNewMsgRing {

	static final String[] getRingUri(Context context, SQLiteDatabase db,
			String targetID) {

		String sql = "select * from " + Table_PublicConfigMsg._TABLE_NAME + " where "
				+ Table_PublicConfigMsg.PAC_ID + "='" + targetID + "'";
		Cursor cursor = db.rawQuery(sql, null);
		String uri_String = "";
		int isVibrateOpen = 1;
		Uri uri = null;
		if (cursor.moveToNext()) {
			uri_String = cursor.getString(cursor
					.getColumnIndex(Table_PublicConfigMsg.PAM_RingUri));
            isVibrateOpen = cursor.getInt(cursor
                    .getColumnIndex(Table_PublicConfigMsg.PAM_IsOpenVibrate));
		}
		if (StringUtil.isEmpty(uri_String)) {
			uri = RingtoneManager.getActualDefaultRingtoneUri(context,
					RingtoneManager.TYPE_NOTIFICATION);
			if(uri!=null){
                uri_String = uri.toString();
            }
		}
		cursor.close();
		return new String[]{uri_String,String.valueOf(isVibrateOpen)};
	}

}
