package qyc.library.tool.check_net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnNetChangeReceiver extends BroadcastReceiver {

	public static final String ACTION = "qyc.library.check_net.OnNetChangeReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// 将网络状态改变的广播转发出去
		Intent _intent = new Intent(ACTION);
		context.sendBroadcast(_intent);
	}

}
