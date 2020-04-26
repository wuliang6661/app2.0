package synway.module_publicaccount.push;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.SQLite;
import synway.module_interface.db.table_util.Table_LastContact;

public class SetLCForPublicAccount {

	/** 最近联系人类型，tinyint，0=单聊，1=群聊，2=公众 */
	public static final String TARGET_TYPE = "pushUtil.newLastContact.extra.TargetType";

	/** 目标Id，varchar(40) */
	public static final String TARGET_ID = "pushUtil.newLastContact.extra.TargetId";

	/** 目标名称，varchar(50) */
	public static final String TARGET_NAME = "pushUtil.newLastContact.extra.TargetName";

	/** 最近一次通讯时间， dateTime */
	public static final String LAST_MSG_SERVER_TIME = "pushUtil.newLastContact.extra.LastMsgServerTime";

	/** 最近一次通讯的内容， varchar */
	public static final String LAST_MSG_CONTENT = "pushUtil.newLastContact.extra.LastMsgContent";

	/** 未读消息数量 */
	public static final String UN_READ_COUNT = "pushUtil.newLastContact.extra.UnReadCount";
	
	/** 消息发送结果广播 */

	public static final String ACTION_CLEAR = "lastcontact.clear";

	public static final String EXTRA_SEND_TARGET_ID = "lastcontact.send.targetid";

	/** 消息产生者的ID */
	public static final String FROM_USER_ID = "pushUtil.newLastContact.extra.FromUserID";

	/** 消息产生者的名字 */
	public static final String FROM_USER_NAME = "pushUtil.newLastContact.extra.FromUserName";

	public static final String ACTION = "action.newLastPublicMsg";
	/** 清除未读数 */
	public static final void clearUnReadCount(Context context, SQLiteDatabase db, String targetID) {
		ContentValues cv = new ContentValues();
		cv.put(Table_LastContact.UN_READ_COUNT, 0);// 消息
		SQLite.update(db, Table_LastContact._TABLE_NAME, cv, Table_LastContact.TARGET_ID + "=?", new String[]{targetID});
		// 再发送广播
		Intent intent = new Intent(ACTION_CLEAR);
		intent.putExtra(EXTRA_SEND_TARGET_ID, targetID);
		context.sendBroadcast(intent);
	}

	public static final void updateOrInsert(Context context, SQLiteDatabase db,
			String publicGUID, String publicName, String jsonMsg,
			long localeTime, long serverTime, int unReadCount,
			String fromUserID, String fromUserName) {

		ContentValues cv2 = new ContentValues();
		cv2.put(Table_LastContact.TARGET_ID, publicGUID);
		cv2.put(Table_LastContact.TARGET_NAME, publicName);
		// 公众号 = 2
		cv2.put(Table_LastContact.TARGET_TYPE, 2);
		cv2.put(Table_LastContact.LAST_MSG_CONTENT, jsonMsg);
		cv2.put(Table_LastContact.LAST_MSG_LOCALE_TIME, localeTime);
		cv2.put(Table_LastContact.LAST_MSG_SHOW_TIME, serverTime);
		cv2.put(Table_LastContact.UN_READ_COUNT, unReadCount);
		cv2.put(Table_LastContact.FROM_USER_ID, fromUserID);
		cv2.put(Table_LastContact.FROM_USER_NAME, fromUserName);
		
		String cols[] = new String[]{Table_LastContact.ID,
				Table_LastContact.TARGET_ID,Table_LastContact.TARGET_NAME};
		
		String strs = SQLite.queryOneRecord(db, Table_LastContact._TABLE_NAME, cols,
				"|", Table_LastContact.TARGET_ID +"=?",new String[]{publicGUID}, null);
		//已有记录，修改操作
		if(strs != null){
			String selectionArgs[] = new String[]{publicGUID};
			SQLite.update(db, Table_LastContact._TABLE_NAME, cv2, 
					Table_LastContact.TARGET_ID+"=?",selectionArgs);
		}else{
			// [修改/新增数据库里的最近联系人]
			SQLite.insert(db, Table_LastContact._TABLE_NAME, null, cv2);
		}

		// 发送广播到 最近联系人
		Intent sendLCBC = new Intent();
		sendLCBC.setAction(ACTION);
		sendLCBC.putExtra(TARGET_ID, publicGUID);
		sendLCBC.putExtra(TARGET_NAME, publicName);
		sendLCBC.putExtra(TARGET_TYPE, 2);
		sendLCBC.putExtra(LAST_MSG_CONTENT, jsonMsg);
		sendLCBC.putExtra(LAST_MSG_SERVER_TIME, serverTime);
		// 消息生产者的ID，
		sendLCBC.putExtra(FROM_USER_ID, publicGUID);
		sendLCBC.putExtra(FROM_USER_NAME, publicName);
		// 消息未读数
		// 设置最近联系人的未读数

		sendLCBC.putExtra(UN_READ_COUNT, unReadCount);

		context.sendBroadcast(sendLCBC);

	}
}