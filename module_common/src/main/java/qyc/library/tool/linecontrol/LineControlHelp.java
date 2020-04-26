package qyc.library.tool.linecontrol;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;

import qyc.library.tool.linecontrol.bluetooth.LineControl_BlueTooth;
import qyc.library.tool.linecontrol.headsetline.LineControl_HeadSet;


/**
 *
 */
public class LineControlHelp {

    /**
     * 看耳机线/或蓝牙耳机有没有插上手机,其中一个插着就算
     */
    public static final boolean isHeadSetIn(Context context) {
        AudioManager audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        return LineControl_HeadSet.isHeadSetIn(audioManager)
                || LineControl_BlueTooth.isHeadSetIn(audioManager);
    }

    /**
     * 看耳机线有没有插上手机
     */
    public static final boolean isHeadSetIn_Line(Context context) {
        AudioManager audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        return LineControl_HeadSet.isHeadSetIn(audioManager);
    }

    /**
     * 看蓝牙耳机有没有连上手机
     */
    public static final boolean isHeadSetIn_BlueTooth(Context context) {
        AudioManager audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        return LineControl_BlueTooth.isHeadSetIn(audioManager);
    }

    /**
     * 线控有没有开启
     */
    public static final boolean isLineControlEnabled(Context context) {
        return LineControlSave.readLineControlEnabled(context);
    }

    /**
     * 音量键有没有开启
     */
    public static final boolean isVolumeKeyEnabled(Context context) {
        return LineControlSave.readVolumeKeyEnabled(context);
    }


    private Context context = null;
    //观察者集合
    private ArrayList<OnLineControlListen> onLineControlListenList = new ArrayList<>();
    //线控按下的监听
    private MediaButtonListener mediaButtonListener = null;

    //是否已经开启了线控
    private boolean isLineControlEnabled = false;
    //是否已经开启了音量键
    private boolean isVolumeControlEnabled = false;
    //是否暂停了
    private boolean isPause = false;

    /**
     * true=当前已经按过录了,下次按要发 false=当前已经按过发了,下次按要录(停)
     */
    private boolean recordState = false;
    private int autoFinishTime = 0;
    private Handler autoFinishHandler = new Handler();


    /**
     * @param context
     * @param autoFinishTime 录音自动完成的时间,单位毫秒. <=0表示不自动结束
     */
    public LineControlHelp(Context context, int autoFinishTime) {
        this.context = context;
        this.autoFinishTime = autoFinishTime;
        this.mediaButtonListener = new MediaButtonListener();
        this.mediaButtonListener.setOnListen(onMediaButtonListen);

        this.isLineControlEnabled = LineControlSave.readLineControlEnabled(context);
        this.isVolumeControlEnabled = LineControlSave.readVolumeKeyEnabled(context);
    }

    /**
     * 设置是否开启线控操作,包含蓝牙和耳机线,不包括音量键.该设置是本地化的,设置后即使重启APP仍然有效.
     *
     * @param isEnable 是否开启
     */
    public void setLineControlEnabled(boolean isEnable) {
        if (this.isLineControlEnabled != isEnable) {
            this.isLineControlEnabled = isEnable;
            LineControlSave.saveLineControlEnabled(context, isEnable);

            //在录制的过程中停用
            if (!isEnable && recordState) {
                recordState = false;
                autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);
                sendCancel();
            }
        }
    }

    /**
     * 设置是否开启线控操作,包含蓝牙和耳机线,不包括音量键.该设置是本地化的,设置后即使重启APP仍然有效.
     *
     * @param isEnable 是否开启,但是不保存到sharedPreference里面
     */
    public void setLineControlEnabledWithoutSave(boolean isEnable) {
        if (this.isLineControlEnabled != isEnable) {
            this.isLineControlEnabled = isEnable;
            //LineControlSave.saveLineControlEnabled(context, isEnable);

            //在录制的过程中停用
            if (!isEnable && recordState) {
                recordState = false;
                autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);
                sendCancel();
            }
        }
    }


    /**
     * 由于耳机线控广播只发送给最后一个注册该广播的APP,因此在界面出现切换后,最好重新注册一下以防被其他APP抢走.
     * 通常可以将它写在界面的onResume()里面
     */
    public void reRegist() {
        if (isLineControlEnabled) {
            mediaButtonListener.stop();
            mediaButtonListener.start(context);
        }
    }

    /**
     * 设置是否开启音量键控制
     *
     * @param isEnable 是否开启
     */
    public void setVolumeKeyEnabled(boolean isEnable) {
        if (this.isVolumeControlEnabled != isEnable) {
            this.isVolumeControlEnabled = isEnable;
            LineControlSave.saveVolumeKeyEnabled(context, isEnable);
        }

        //在录制的过程中停用
        if (!isEnable && recordState) {
            recordState = false;

            autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);

            sendCancel();

        }
    }

    /**
     * 设置是否开启音量键控制
     *
     * @param isEnable 是否开启,但是不保存到sharedPreference里面
     */
    public void setVolumeKeyEnabledWithoutSave(boolean isEnable) {
        if (this.isVolumeControlEnabled != isEnable) {
            this.isVolumeControlEnabled = isEnable;
            //LineControlSave.saveVolumeKeyEnabled(context, isEnable);
        }

        //在录制的过程中停用
        if (!isEnable && recordState) {
            recordState = false;

            autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);

            sendCancel();

        }
    }

    /**
     * 暂时停止线控,不会保存到本地
     */
    public void pause() {
        this.isPause = true;

        //在录制的过程中暂停
        if (recordState) {
            recordState = false;

            autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);

            sendCancel();
        }
    }

    /**
     * 继续线控. 但仍受开关控制.
     */
    public void resume() {
        this.isPause = false;
    }


    /**
     * 堆栈式反馈：后进先反馈
     */
    public void addListen(OnLineControlListen onLineControlListen) {
        this.onLineControlListenList.add(onLineControlListen);
    }

    /**
     * 堆栈式反馈：后进先反馈
     */
    public void removeListen(OnLineControlListen onLineControlListen) {
        this.onLineControlListenList.remove(onLineControlListen);
    }

    private void sendRecord() {
        int size = onLineControlListenList.size();
        if (size > 0) {
            onLineControlListenList.get(size - 1).onRecord();
        }
    }

    private void sendCancel() {
        int size = onLineControlListenList.size();
        if (size > 0) {
            onLineControlListenList.get(size - 1).onCancel();
        }
    }

    private void sendFinish() {
        Log.d("QYC","sendFinish");
        int size = onLineControlListenList.size();
        if (size > 0) {
            onLineControlListenList.get(size - 1).onFinish();
        }
    }

    private Runnable autoFinishHandlerCallBack = new Runnable() {

        @Override
        public void run() {
            recordState = false;
            sendFinish();
        }
    };

    /**
     * 捕获音量键,可以在界面的 OnKeyDown 里面进行调用,最后会统一从线控接口里反馈出来
     */
    public boolean catchVolumeKey(int keyEvent) {
        if (!isVolumeControlEnabled || isPause) {
            return false;
        }

        if (keyEvent == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (recordState) {
                recordState = false;

                sendFinish();

                autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);
            } else {
                recordState = true;

                sendRecord();

                if (autoFinishTime > 0) {
                    autoFinishHandler.postDelayed(autoFinishHandlerCallBack, autoFinishTime);
                }
            }
            return true;
        } else if (keyEvent == KeyEvent.KEYCODE_VOLUME_UP) {
            if (recordState) {
                recordState = false;

                autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);

                sendCancel();
            } else {
                // 无视
            }
            return true;
        }
        return false;
    }

    private MediaButtonListener.OnMediaButtonListen onMediaButtonListen = new MediaButtonListener.OnMediaButtonListen() {

        @Override
        public void onMediaButtonDoubleClick() {
            if (!isLineControlEnabled || isPause) {
                return;
            }

            if (recordState) {
                recordState = false;

                autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);

                sendCancel();
            } else {
                // 无视
            }
        }

        @Override
        public void onMediaButtonClick() {
            if (!isLineControlEnabled || isPause) {
                return;
            }

            if (recordState) {
                recordState = false;

                sendFinish();

                autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);
            } else {
                recordState = true;

                sendRecord();

                if (autoFinishTime > 0) {
                    autoFinishHandler.postDelayed(autoFinishHandlerCallBack, autoFinishTime);
                }
            }
        }

//        @Override
//        public void onBlueToothShutDown() {
//            //蓝牙耳机出现了挂断动作,挂断动作=之前蓝牙耳机处于通话动作,但现在被挂断了
//            if (!isLineControlEnabled || isPause) {
//                return;
//            }
//
//            if (recordState) {
//                recordState = false;
//
//                sendFinish();
//
//                autoFinishHandler.removeCallbacks(autoFinishHandlerCallBack);
//            }
//
//            reRegist();
//        }
    };

    public interface OnLineControlListen {
        void onRecord();

        void onFinish();

        void onCancel();

    }
}
