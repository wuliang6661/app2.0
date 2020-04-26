package qyc.library.tool.linecontrol;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

/**
 * 音量键按下监听,包括耳机线和蓝牙耳机的音量键
 * <p/>
 * 通过静态注册音量键广播,收到后则发出内部广播.若方法的start和stop就在于是否注册内部广播.
 * <p/>
 * 在ManiFests.xml里面注册静态广播:
 * <br/>
 * < receiver android:name="qyc.MediaButtonListener$MediaButtonReceiver">
 * <br/>
 * < intent-filter>
 * <br/>
 * < action android:name="android.intent.action.MEDIA_BUTTON" / >
 * <br/>
 * < /intent-filter>
 * <br/>
 * < /receiver>
 */
public class MediaButtonListener {

    private static final String ACTION = "qyc.library.tool.linecontrol.myself.MediaButtonListener";

    private AudioManager audioManager = null;
    private ComponentName componentName = null;
    private Context context = null;
    private boolean isStart = false;
    private Handler doubleClickHandler = new Handler();
    private int clickTimes = 0;

    public void start(Context context) {
        if (!isStart) {
            this.context = context;
            this.componentName = new ComponentName(context.getPackageName(),
                    MediaButtonReceiver.class.getName());
            this.audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            this.audioManager.registerMediaButtonEventReceiver(componentName);// 抢注按键监听
            this.audioManager.requestAudioFocus(onAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION);
            //intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
            this.context.registerReceiver(inBroadcastReceiver,
                    intentFilter);
            this.clickTimes = 0;
            this.isStart = true;
        }
    }

    public void stop() {
        if (isStart) {
            audioManager.unregisterMediaButtonEventReceiver(componentName);
            context.unregisterReceiver(inBroadcastReceiver);
            doubleClickHandler.removeCallbacks(doubleClickCallback);
            isStart = false;
        }
    }

    private OnMediaButtonListen onMediaButtonListen = null;

    public void setOnListen(OnMediaButtonListen onMediaButtonListen) {
        this.onMediaButtonListen = onMediaButtonListen;
    }

    public void removeListen() {
        this.onMediaButtonListen = null;
    }

    private BroadcastReceiver inBroadcastReceiver = new BroadcastReceiver() {

        private boolean isScoOn = false;//蓝牙麦是否开启

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION)) {
                KeyEvent keyEvent = intent
                        .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (onMediaButtonListen != null) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        clickTimes++;
                        doubleClickHandler.removeCallbacks(doubleClickCallback);
                        doubleClickHandler.postDelayed(doubleClickCallback, 300);
                    } else if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        //onMediaButtonListen.onMediaButtonUp();
                    }
                }
            }
//            else if (intent.getAction().equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)) {
//                Log.d("QYC","AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED");
//                if (audioManager.isBluetoothScoOn()) {
//                    //当前蓝牙耳机的麦被开启了
//                    isScoOn = true;
//                } else {
//                    //当前蓝牙耳机的麦被关闭了
//                    if (isScoOn) {
//                        //如果之前蓝牙耳机的麦是开启状态,并且现在被关闭了,说明挂断了
//                        if (onMediaButtonListen != null) {
//                            onMediaButtonListen.onBlueToothShutDown();
//                        }
//                    }
//                    isScoOn = false;
//                }
//            }
        }
    };

    private Runnable doubleClickCallback = new Runnable() {
        @Override
        public void run() {
            if (clickTimes > 1) {
                onMediaButtonListen.onMediaButtonDoubleClick();
            } else {
                onMediaButtonListen.onMediaButtonClick();
            }
            clickTimes = 0;
        }
    };

    private OnAudioFocusChangeListener onAudioFocusChangeListener = new OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Pause playback
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    // am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                    // am.abandonAudioFocus(afChangeListener);
                    // // Stop playback
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            audioManager
                                    .registerMediaButtonEventReceiver(componentName);// 抢注按键监听
                        }
                    }, 500);

                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;

                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
            }
        }
    };

    // 由于该MediaButton广播注册方式比较复杂,必须静态注册+动态注册,而静态注册指向内部类则必须是静态类,因此它只能是静态的.
    public static class MediaButtonReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // abortBroadcast();// 这是一个无序广播,谁说TM可以中断的.大片红见给你看!
            Intent mIntent = new Intent(ACTION);
            mIntent.putExtras(intent.getExtras());
            context.sendBroadcast(mIntent);
            Log.d("QYC","MediaButtonReceiver");
        }

    }

    interface OnMediaButtonListen {
        void onMediaButtonDoubleClick();

        void onMediaButtonClick();

        //void onBlueToothShutDown();//蓝牙耳机的麦从录音状态切至非录音状态
    }
}
