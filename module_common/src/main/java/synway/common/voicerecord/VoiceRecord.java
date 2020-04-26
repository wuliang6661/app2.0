package synway.common.voicerecord;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.List;
//import android.util.Log;

/**
 * 语音录制工具类
 *
 * @author hdt
 */
@SuppressWarnings("deprecation")
class VoiceRecord {

    private MediaRecorder mediaRecorder;
    private String path = null;
    private String fileName = null;
    private boolean isRecording = false;
    private long startRecordTime = 0;
    private static final String fileSuffix = ".amr";
    private String VOICE_FOLDER_PATH = null;
    private AudioManager mAudioManager;

    private boolean isBlueToothMicOpen = false;
    private Context mContext;
    private BluetoothHeadset bluetoothHeadset = null;
    private BluetoothAdapter blueToothAdapter = null;

    public VoiceRecord(Context context, String folderPath) {
        VOICE_FOLDER_PATH = folderPath;
        mediaRecorder = new MediaRecorder();
        this.mContext = context.getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        mAudioManager.stopBluetoothSco();
//        mAudioManager.setSpeakerphoneOn(true);
//        mAudioManager.setBluetoothScoOn(false);
//        mAudioManager.setMode(AudioManager.MODE_NORMAL);

        initBlueToothHeadset();
        //Log.d("QYC", "initBlueToothHeadset  finish");
    }

    // String userID,String userName,String userArea,String toID,String
    // toName,String toArea


    public void start() {

        //如果连了蓝牙耳机,则打开蓝牙
        boolean result = openBlueToothMic2();

        startRecordTime = System.currentTimeMillis();
        path = VOICE_FOLDER_PATH + "/" + startRecordTime;
        fileName = startRecordTime+"";

        mediaRecorder.reset();
        if (result) {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        } else {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        }
        mediaRecorder.setOutputFile(path);

        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioChannels(1);
        // mediaRecorder.setAudioEncodingBitRate(8);
        // mediaRecorder.setAudioSamplingRate(16);


        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 取消录音
     */
    public void cancelRecord() {
        long recordTime = System.currentTimeMillis() - startRecordTime;
        if (recordTime <= 150) {
            try {
                Thread.sleep(150);
            } catch (Exception e) {
                //
            }
        }

        try {
            mediaRecorder.stop();
        } catch (Exception e) {

        }

        closeBlueToothMic2();
        if (isRecording) {
            try {
                File f = new File(path);
                boolean s = f.delete();
            } catch (Exception e) {
                //
            }
        }

//        if (mAudioManager != null && mAudioManager.isBluetoothA2dpOn()) {
////            mAudioManager.stopBluetoothSco();
////            mAudioManager.setMode(AudioManager.MODE_NORMAL);
////            mAudioManager.setSpeakerphoneOn(true);
////            mAudioManager.setBluetoothScoOn(false);
//        }
        isRecording = false;
    }

    /**
     * 结束录音
     *
     * @return 录音时长
     */
    public int finish() {
        long recordTime = System.currentTimeMillis() - startRecordTime;
        try {
            mediaRecorder.stop();
        } catch (Exception e) {
            //
        }

        closeBlueToothMic2();

        isRecording = false;
        return Math.round(recordTime / 1000);
    }

    public String getFileName() {
        return fileName;
    }

    public long getCurrentRecordTime() {
        return System.currentTimeMillis() - startRecordTime;
    }

    public double getAmplitude() {
        if (mediaRecorder != null)
            // 获取在前一次调用此方法之后录音中出现的最大振幅
            return (mediaRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;
    }

    public boolean isPlaying() {
        return isRecording;
    }

    /*
     * 此外，还有和MediaRecorder有关的几个参数与方法，我们一起来看一下： sampleRateInHz
     * :音频的采样频率，每秒钟能够采样的次数，采样率越高，音质越高。
     * 给出的实例是44100、22050、11025但不限于这几个参数。例如要采集低质量的音频就可以使用4000、8000等低采样率
     *
     * channelConfig ：声道设置：android支持双声道立体声和单声道。MONO单声道，STEREO立体声
     *
     * recorder.stop();停止录音 recorder.reset(); 重置录音 ，会重置到setAudioSource这一步
     * recorder.release(); 解除对录音资源的占用
     */
    public void destory() {

        mediaRecorder.release();
        mediaRecorder = null;
        if (isRecording) {
            try {
                File f = new File(path);
                boolean s = f.delete();
            } catch (Exception e) {
                //
            }
        }
        closeBlueToothMic2();

        if (blueToothAdapter != null && bluetoothHeadset != null) {
            blueToothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset);
            //Log.d("QYC", "closeProfileProxy");
        }
    }

    //尝试打开蓝牙耳机的录音声道
    //当该声道打开时,系统会标记为当前APP正在通话,此时无法使用其他APP进行播放,录音等.
    //但APP自身仍然可以播放,录音,且录音的音源来自蓝牙耳机.
    //蓝牙耳机的声音会降低,直到该通道关闭后又重新升高.
    //打开时不会中断其他正在播放的声音,只是音量会降低.
    //关闭时不会中断其他正在播放的声音,只是音量会还原.
    //通道开启状态下蓝牙耳机断开,仍需要关闭动作!
    //通道开启状态下蓝牙耳机断开,播放会中断.但录音会从另外的声道继续.
    private void openBlueToothMic() {
        //Log.d("QYC", "openBlueToothMic1");
        if (mAudioManager != null && mAudioManager.isBluetoothA2dpOn()) {


            //defaultAudioMode = mAudioManager.getMode();
            isBlueToothMicOpen = true;

            //Log.d("QYC", "openBlueToothMic2");
            //已接入蓝牙(不确定是不是耳机)

            mAudioManager.setBluetoothScoOn(true);
            mAudioManager.startBluetoothSco();
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            //mAudioManager.setSpeakerphoneOn(false);

            //mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);


        }
    }

    private boolean openBlueToothMic2() {
        //Log.d("QYC", "openBlueToothMic-->1");
        if (bluetoothHeadset != null) {
            //Log.d("QYC", "openBlueToothMic-->2");
            List<BluetoothDevice> list = bluetoothHeadset.getConnectedDevices();
            if (list.size() > 0) {
                //Log.d("QYC", "openBlueToothMic-->3");
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                bluetoothHeadset.startVoiceRecognition(list.get(0));
                return true;
            }
        }
        return false;
    }

    private void closeBlueToothMic() {
        //Log.d("QYC", "closeBlueToothMic1");
        if (mAudioManager != null && isBlueToothMicOpen) {
            //Log.d("QYC", "closeBlueToothMic2");
            mAudioManager.stopBluetoothSco();
            //mAudioManager.setSpeakerphoneOn(true);
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            isBlueToothMicOpen = false;
        }
    }


    private void closeBlueToothMic2() {
        //Log.d("QYC", "closeBlueToothMic-->1");
        if (bluetoothHeadset != null) {
            //Log.d("QYC", "closeBlueToothMic-->2");
            List<BluetoothDevice> list = bluetoothHeadset.getConnectedDevices();
            if (list.size() > 0) {
                //Log.d("QYC", "closeBlueToothMic-->3");
                bluetoothHeadset.stopVoiceRecognition(list.get(0));
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
            }
        }

    }


    private void initBlueToothHeadset() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {//android4.3之前直接用BluetoothAdapter.getDefaultAdapter()就能得到BluetoothAdapter
            blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            BluetoothManager bm = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bm != null) {
                blueToothAdapter = bm.getAdapter();
            }
        }
        if (blueToothAdapter == null) {
            return;
        }

        blueToothAdapter.getProfileProxy(mContext, new ServiceListener() {
            @Override
            public void onServiceDisconnected(int profile) {

                if (profile == BluetoothProfile.HEADSET) {
//                    if (last != null) {
//                        bluetoothHeadset.stopVoiceRecognition(last);
//                    }
                    //blueToothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset);
                    bluetoothHeadset = null;
                    mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
//                    mAudioManager.setSpeakerphoneOn(false);
//                    mAudioManager.setBluetoothScoOn(false);

                    //Log.d("QYC", "onServiceDisconnected-->HEAD_SET");
                }
            }

            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    bluetoothHeadset = (BluetoothHeadset) proxy;

                    //mAudioManager.setBluetoothScoOn(true);

                    //Log.d("QYC", "onServiceConnected-->HEAD_SET");
                }
            }
        }, BluetoothProfile.HEADSET);
    }
}
