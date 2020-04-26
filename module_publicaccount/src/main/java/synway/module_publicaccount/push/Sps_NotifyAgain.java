package synway.module_publicaccount.push;

import android.content.Context;
import android.content.SharedPreferences;

public class Sps_NotifyAgain {

	// 文件名称
	private static final String FILE_NAME = "FILE_NOTIFY_AGAIN";
	// 当前 聊天的targetID
	private static final String KEY_CURRENT_CHAT_ID = "KEY_CURRENTID";

	/** 设置当前聊天用户ID */
	public static final void setCurrent(Context context, String userID) {
		SharedPreferences spf = context.getSharedPreferences(FILE_NAME,
				Context.MODE_MULTI_PROCESS);
		spf.edit().putString(KEY_CURRENT_CHAT_ID, userID).commit();
	}

	/** 获取当前聊天用户ID */
	public static final String getCurrent(Context context) {
		SharedPreferences spf = context.getSharedPreferences(FILE_NAME,
				Context.MODE_MULTI_PROCESS);
		String currentID = spf.getString(KEY_CURRENT_CHAT_ID, null);
		return currentID;
	}

	public static final void clear(Context context) {
		context.getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS)
				.edit().clear().commit();
	}

}