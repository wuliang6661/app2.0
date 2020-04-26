package qyc.library.tool.linecontrol.bluetooth;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

/**
 * <p/>
 * 蓝牙耳机是否连接,以及插拔监听
 *
 * @author 钱园超 2015年12月8日下午4:14:36
 */
public class LineControl_BlueTooth {

    /**
     * 蓝牙耳机是否插入
     *
     * @return
     */
    public static final boolean isHeadSetIn(Context context) {
        return isHeadSetIn((AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE));
    }

    /**
     * 蓝牙耳机是否插入
     *
     * @return
     */
    public static final boolean isHeadSetIn(AudioManager localAudioManager) {
        return localAudioManager.isBluetoothA2dpOn();
    }

    private OnBlueToothEvent onBlueToothEvent = null;
    private Context context = null;

    /**
     * 开始监听蓝牙耳机是否插入
     */
    public void start(Context context) {
        this.context = context;

        // 注册蓝牙耳机插拔状态广播
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        context.registerReceiver(broadCastReceiver, bluetoothFilter);
    }

    public void stop() {
        // 注销蓝牙耳机插拔状态广播
        context.unregisterReceiver(broadCastReceiver);
        this.onBlueToothEvent = null;
    }

    public void setOnListen(OnBlueToothEvent onBlueToothEvent) {
        this.onBlueToothEvent = onBlueToothEvent;
    }

    // 蓝牙插/拔广播
    private BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                // 蓝牙耳机插拔广播
                int state = intent
                        .getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
                if (state == BluetoothHeadset.STATE_CONNECTED) {
                    // 蓝牙耳机插拔状态检测:连接
                    if (onBlueToothEvent != null) {
                        onBlueToothEvent.onBlueToothIn();
                    }
                } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    if (onBlueToothEvent != null) {
                        onBlueToothEvent.onBlueToothOut();
                    }
                }
            }
        }
    };

    public interface OnBlueToothEvent {
        void onBlueToothIn();

        void onBlueToothOut();
    }

}
