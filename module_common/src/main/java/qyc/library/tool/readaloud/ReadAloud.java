package qyc.library.tool.readaloud;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import qyc.library.tool.xml.SharedPleasure;

/**
 * 文字朗读,也包含语音播放<br>
 * 对象生命周期为: {@link ReadAloud#init(Context)}--- {@link ReadAloud#destory()}<br>
 *
 * @author 钱园超 2016年1月12日下午3:00:52
 */
@SuppressWarnings("deprecation")
public class ReadAloud {

    /**
     * 保存朗读开关的设置,但是不会改变已经初始化的ReadAloud
     *
     * @param context
     * @param enabled
     */
    public static final void saveReadAloudEnabled(Context context, boolean enabled) {
        ReadAloudSave.saveReadAloudEnabled(context, enabled);
    }

    /**
     * 获取朗读开关保存在本地的配置.
     *
     * @param context
     * @return
     */
    public static final boolean getReadAloudEnabled(Context context) {
        return ReadAloudSave.getReadAloudEnabled(context);
    }

    private MediaPlayer mediaPlayer = null;
    private TextToSpeech textToSpeech = null;
    private int audioType = 0;
    private boolean isPausing = false;
    private boolean isDestoryed = false;
    private Object lock_Stop = new Object();
    private boolean isEnabled = true;
    private Context context = null;

    public void init(Context context) {
        this.context = context;
        this.audioType = SharedPleasure.getReadTool(context, "CHAT_CONFIG").getInt("AUDIO_TYPE", 0);
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setOnCompletionListener(onCompletionListener);
        this.isEnabled = ReadAloudSave.getReadAloudEnabled(context);
        try {
            this.textToSpeech = new TextToSpeech(context, ttsInitListener, "com.iflytek.tts");
        } catch (Exception e) {
            this.textToSpeech = new TextToSpeech(context, ttsInitListener);
        }
    }

    /**
     * 清空资源
     */
    public void destory() {
        synchronized (lock_Stop) {
            isSpeaking = false;
            isDestoryed = true;
            mediaPlayer.stop();
            mediaPlayer.release();
            //textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    /**
     * 设置是否启用,不启用的情况下所有朗读内容都会被忽略
     */
    public void setEnabled(boolean enabled) {
        if (this.isEnabled != enabled) {
            this.isEnabled = enabled;
            ReadAloudSave.saveReadAloudEnabled(context, enabled);
        }
    }

    // 语音朗读初始化完成
    private OnInitListener ttsInitListener = new OnInitListener() {

        @Override
        public void onInit(int status) {
            // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.CHINESE);
                textToSpeech.setEngineByPackageName("com.iflytek.tts");
                textToSpeech.setOnUtteranceCompletedListener(onUtteranceCompletedListener);
            }
        }
    };

    private ArrayList<String[]> speakTextList = new ArrayList<String[]>();
    private boolean isSpeaking = false;

    /**
     * 添加到朗读队列,文字.<br>
     * 添加到队列后会自动启动朗读,直到队列朗读完毕或暂停{@link ReadAloud#pause()}
     */
    public void addToSpeakList(String speakText) {
        if (isEnabled) {
            speakTextList.add(new String[]{speakText});
            startSpeakCycle();
        }
    }

    /**
     * 添加到朗读队列,文字+语音<br>
     * 添加到队列后会自动启动朗读,直到队列朗读完毕或暂停{@link ReadAloud#pause()}
     */
    public void addToSpeakList(String speakText, String voicePath) {
        if (isEnabled) {
            speakTextList.add(new String[]{speakText, voicePath});
            startSpeakCycle();
        }
    }

    /**
     * 暂停朗读,读到一半的内容将会被略过<br>
     * 暂停朗读后,通过{@link ReadAloud#addToSpeakList(String)},
     * {@link ReadAloud#addToSpeakList(String, String)},
     * {@link ReadAloud#resume()},都会恢复朗读
     */
    public void pause() {
        synchronized (lock_Stop) {
            isPausing = true;
            isSpeaking = false;
            mediaPlayer.stop();
            textToSpeech.stop();
        }
    }

    /**
     * 继续朗读
     */
    public void resume() {
        isPausing = false;
        startSpeakCycle();
    }

    // 开始一个朗读周期
    // 朗读队列中的内容,在朗读周期内不可能运行第二次
    // 朗读周期:执行此函数,到isSpeaking=false为止
    private synchronized void startSpeakCycle() {
        if (speakTextList.size() > 0 && !isSpeaking && !isPausing) {
            isSpeaking = true;
            String[] speakText = speakTextList.remove(0);
            HashMap<String, String> params = new HashMap<String, String>();
            if (speakText.length > 1) {
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speakText[1]);
            } else {
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TEXT");
            }
            textToSpeech.speak(speakText[0], TextToSpeech.QUEUE_ADD, params);
        }
    }

    private OnUtteranceCompletedListener onUtteranceCompletedListener = new OnUtteranceCompletedListener() {
        @Override
        public void onUtteranceCompleted(String utteranceId) {
            // 看朗读完的内容是否含有声音
            // 如果不含声音则终止该朗读周期,允许speakList()再次执行
            // 如果含有声音则播放声音
            synchronized (lock_Stop) {
                if (isDestoryed) {
                    return;
                }

                if (utteranceId.equals("TEXT")) {
                    // 进入下一个朗读周期
                    isSpeaking = false;
                    startSpeakCycle();
                } else {
                    try {
                        // 开始播放声音
                        mediaPlayer.reset();
                        if (audioType == 1) {
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                        } else {
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        }
                        mediaPlayer.setDataSource(utteranceId);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (Exception e) {
                        // 播放失败则进入下一个朗读周期
                        isSpeaking = false;
                        startSpeakCycle();
                    }
                }
            }
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            synchronized (lock_Stop) {
                // 播放完成则进入下一个朗读周期
                isSpeaking = false;
                startSpeakCycle();
            }
        }
    };
}
