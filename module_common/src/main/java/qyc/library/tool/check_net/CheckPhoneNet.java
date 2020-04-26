package qyc.library.tool.check_net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class CheckPhoneNet {

	private Context context = null;
	private ConnectivityManager manager = null;
	private int notif = -1;
	private String lastWifiName = "";
	private boolean isWakeLock = false;
	private WakeLock wakeLock = null;

	private OnNetChangedListen onNetChangedListen = null;

	public CheckPhoneNet(Context context) {
		this.context = context;
		this.manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public void setOnListen(OnNetChangedListen onNetChangedListen) {
		this.onNetChangedListen = onNetChangedListen;
	}

	/**
	 * @param isWakeLock
	 *            <p>
	 *            在收到系统广播及执行接口代码期间,是否保持系统唤醒.默认false
	 *            <p>
	 *            如果调用它的组件自身不具备唤醒策略,就将它设为true,由它自己来确保接口执行期间系统处于唤醒状态.
	 */
	public void setAutoWakeLock(boolean isWakeLock) {
		this.isWakeLock = isWakeLock;
	}

	public void start() {
		// 先注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(OnNetChangeReceiver.ACTION);
		context.registerReceiver(broadcastReceiver, intentFilter);

		// 申请休眠锁
		if (isWakeLock) {
			PowerManager powerManager = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"LIBRARY_CHECK_PHONE_NET");
		}

		// 然后检查当前网络
		check(context);
	}

	public void stop() {
		context.unregisterReceiver(broadcastReceiver);
	}

	private void notifToWifi(String wifiName) {
		if (onNetChangedListen != null
				&& (notif != 1 || !wifiName.equals(lastWifiName))) {
			notif = 1;
			lastWifiName = wifiName;
			onNetChangedListen.turnToWifi();
		}
	}

	private void notifTo2_3_4_G() {
		if (onNetChangedListen != null) {
			notif = 2;
			onNetChangedListen.turnTo2_3_4_G();
		}
	}

	private void notifToClose() {
		if (onNetChangedListen != null && notif != 0) {
			notif = 0;
			onNetChangedListen.turnToClose();
		}
	}

	public interface OnNetChangedListen {
		void turnToWifi();

		void turnTo2_3_4_G();

		void turnToClose();
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (isWakeLock) {
				wakeLock.acquire();
			}

			check(context);

			if (isWakeLock) {
				Handler handler = new Handler();
				handler.post(new Runnable() {

					@Override
					public void run() {
						wakeLock.release();
					}
				});
			}
			// if (wakeLock!=null) {

			// }
		}
	};

	private void check(Context context) {
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();// 获取当前活动的网络连接

		if (networkInfo == null) {
			// DialogMsg.show(CheckPhoneNet.this.context, "网络",
			// "NetworkInfo=null");
			notifToClose();
			return;
		}

		// DialogMsg.show(CheckPhoneNet.this.context, "网络",
		// print(networkInfo));
		boolean isAvailable = networkInfo.isAvailable();
		State state = networkInfo.getState();
		if (state != State.CONNECTED) {
			return;// 不是已连接,就不报
		}

		if (!isAvailable) {
			notifToClose();// 没活性,就报关闭
			return;
		}

		int type = networkInfo.getType();

		if (type == ConnectivityManager.TYPE_WIFI) {
			notifToWifi(networkInfo.getExtraInfo());
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			notifTo2_3_4_G();
		}
	}

	// private static final String print(NetworkInfo networkInfo) {
	// String str = "NetworkInfo:";
	// str += ("[networkInfo.isAvailable():" + networkInfo.isAvailable() + "]");
	// str += ("[networkInfo.isConnected():" + networkInfo.isConnected() + "]");
	// // str += ("[networkInfo.isConnectedOrConnecting():" +
	// // networkInfo.isConnectedOrConnecting()+ "]");
	// // str += ("[networkInfo.isFailover():" + networkInfo.isFailover()+
	// // "]");
	// // str += ("[networkInfo.isRoaming():" + networkInfo.isRoaming() + "]");
	// str += ("[networkInfo.getTypeName():" + networkInfo.getTypeName() + "]");
	// str += ("[networkInfo.getState():" + networkInfo.getState() + "]");
	// str += ("[networkInfo.getExtraInfo():" + networkInfo.getExtraInfo() +
	// "]");
	// return str;
	// }

}
