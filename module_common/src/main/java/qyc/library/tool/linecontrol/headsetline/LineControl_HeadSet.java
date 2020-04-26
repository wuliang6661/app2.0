package qyc.library.tool.linecontrol.headsetline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

public class LineControl_HeadSet {

	/**
	 * 耳机线是否插入
	 * 
	 * @return
	 */
	public static final boolean isHeadSetIn(Context context) {
		return isHeadSetIn((AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE));
	}

	@SuppressWarnings("deprecation")
	public static final boolean isHeadSetIn(AudioManager localAudioManager) {
		return localAudioManager.isWiredHeadsetOn();
	}

	private OnHeadSetEvent onHeadSetEvent = null;
	private Context context = null;

	/** 开始监听蓝牙耳机是否插入 */
	public void start(Context context) {
		this.context = context;

		// 注册蓝牙耳机插拔状态广播
		IntentFilter headSetFilter = new IntentFilter(
				Intent.ACTION_HEADSET_PLUG);
		context.registerReceiver(broadCastReceiver, headSetFilter);
	}

	public void stop() {
		// 注销蓝牙耳机插拔状态广播
		context.unregisterReceiver(broadCastReceiver);
		this.onHeadSetEvent = null;
	}

	public void setOnListen(OnHeadSetEvent onHeadSetEvent) {
		this.onHeadSetEvent = onHeadSetEvent;
	}

	// 蓝牙插/拔广播
	private BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
				// 蓝牙耳机插拔广播
				int state = intent.getIntExtra("state", -1);
				if (state == 1) {
					// 蓝牙耳机插拔状态检测:连接
					if (onHeadSetEvent != null) {
						onHeadSetEvent.in();
					}
				} else if (state == 0) {
					if (onHeadSetEvent != null) {
						onHeadSetEvent.out();
					}
				}
			}
		}
	};

	public interface OnHeadSetEvent {
		void in();

		void out();
	}

}
