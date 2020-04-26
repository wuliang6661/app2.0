package synway.common.ReadAloud;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * 文字朗读,也包含语音播放<br>
 * 对象生命周期为: {@link ReadAloud#init(Context)}--- {@link ReadAloud#destory()}<br>
 *
 * @author 钱园超 2016年1月12日下午3:00:52
 */
public class ReadAloud {


    /**
     * 是否已经暂停,如果已经暂停,则允许将播放实体加入播放列表,但不启动播放.直到暂停状态被取消
     */
    private volatile boolean isPaused = false;
    /**
     * 是否已经销毁
     */
    private volatile boolean isDestoryed = false;
    /**
     * 语音文件播放声道 0=音乐声道 1=听筒声道
     */
    private int voiceChannel = 0;
    /**
     * 语音播放器
     */
    private MediaPlayer mediaPlayer = null;
    /**
     * 文本朗读器
     */
    private TextToSpeech textToSpeech = null;
    /**
     * 朗读列表
     */
    private ArrayList<ReadAloudObj> readAloudObjList = new ArrayList<>();
    /**
     * 朗读状态/播放状态监听接口
     */
    private OnReadAloundListener onReadAloundListener = null;
    /**
     * 监听接口执行Handler
     */
    private Handler handler = new Handler();
    /**
     * 等待初始化完成的锁
     */
    private InitLock initLock = new InitLock();


    /**
     * 当前正在朗读/播放的任务,也用来标识当前是否有任务正在播放
     */
    private volatile CurrentObjRequest currentObjRequest = null;


    public ReadAloud(OnReadAloundListener onReadAloundListener) {
        this.onReadAloundListener = onReadAloundListener;
    }

    /**
     * 初始化朗读模块.(无论处于什么生命周期,都只有朗读/播放过程会占用系统声道,)
     *
     * @param context context
     */
    public synchronized void init(Context context) {
        voiceChannel = context.getSharedPreferences("ReadAloudConfig", Context.MODE_PRIVATE).getInt("AUDIO_TYPE", 0);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(onCompletionListener);

        try {
            textToSpeech = new TextToSpeech(context, new OnInitListener() {

                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        textToSpeech.setLanguage(Locale.CHINESE);
                        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
                    }
                    initLock.setFinish();
                }
            }, "com.iflytek.vflynote");
        } catch (Exception e) {
            textToSpeech = new TextToSpeech(context, new OnInitListener() {

                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        textToSpeech.setLanguage(Locale.CHINESE);
//                        指定为不可在4.0以下使用本包.
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                            //小于4.0的安卓版本才使用这个方法来指定包名
//                            textToSpeech.setEngineByPackageName("com.iflytek.vflynote");
//                        }
                        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
                    }
                }
            });
        }
    }

    /**
     * 立即停止所有朗读,释放所有资源.
     */
    public synchronized void destory() {
        clear();
        isDestoryed = true;
        //if (mediaPlayer != null) {
        mediaPlayer.release();
        //}
        textToSpeech.shutdown();
    }


    /**
     * 暂停朗读，但仍然可以添加朗读任务
     */
    public synchronized void pause() {
        isPaused = true;
        stopRead();
    }


    /**
     * 恢复朗读
     */
    public synchronized void resume() {
        isPaused = false;
        readNext();
    }

    /**
     * 将朗读任务添加到朗读队列,并开始朗读整个朗读队列<br>
     * 以下情况仅添加朗读任务,但不朗读:{@link #pause()}
     *
     * @param speakMsgObj 朗读实体
     */
    public synchronized void add(ReadAloudObj speakMsgObj) {
        if (!canAdd) {
            return;
        }
        readAloudObjList.add(speakMsgObj);
        readNext();
    }

    /**
     * 将朗读任务添加到朗读队列,并开始朗读整个朗读队列<br>
     * 以下情况仅添加朗读任务,但不朗读:{@link #pause()}
     *
     * @param speakMsgObj 朗读实体
     */
    public synchronized void add(ReadAloudObj speakMsgObj, boolean isFirst) {
        if (!canAdd) {
            return;
        }
        if (isFirst) {
            readAloudObjList.add(0, speakMsgObj);
        } else {
            readAloudObjList.add(speakMsgObj);
        }
        readNext();
    }

    /**
     * 从队列里移除一条语音,如果该语音正在播放,则停止并可以选择是否播放下一条.
     *
     * @param id       根据携id来找到这条语音.
     * @param readNext 该语音移除后,如果它当前正在播放,就会被停止.停止后是否继续播放下一条.
     */
    public synchronized void remove(String id, boolean readNext) {
        if (id == null) {
            return;
        }

        stop:
        {
            if (currentObjRequest != null) {
                if (id.equals(currentObjRequest.readAloudObj().getId())) {
                    stopRead();
                    break stop;
                }
            }

            for (ReadAloudObj readAloudObj : readAloudObjList) {
                if (id.equals(readAloudObj.getId())) {
                    readAloudObjList.remove(readAloudObj);
                    break stop;
                }
            }
        }
        if (readNext) {
            readNext();
        }
    }


    /**
     * 立即停止当前朗读,并清空朗读队列.
     */
    public synchronized void clear() {
        readAloudObjList.clear();
        stopRead();
    }

    public synchronized ReadAloudObj getCurrentReadAloudObj() {
        if (currentObjRequest != null) {
            return currentObjRequest.readAloudObj();
        } else {
            return null;
        }
    }

    private boolean canAdd = true;

    /**
     * 停止当前正在朗读的任务,并清空朗读对列.然后立即朗读当前任务<br>
     * 在该任务朗读完成之前,{@link #add(ReadAloudObj)}是无效的.
     *
     * @param speakMsgObj 朗读实体
     */
    public synchronized void addAtOnce(ReadAloudObj speakMsgObj) {

        speakMsgObj.canAdd = false;
        clear();
        add(speakMsgObj);
        canAdd = false;
    }


    private synchronized void readNext() {
        Log.e("语音播放", "执行第一步:条件是否符合"+String.valueOf(readAloudObjList.size()+String.valueOf(currentObjRequest != null )+String.valueOf(isPaused)+String.valueOf(isDestoryed)) );
        if (readAloudObjList.size() == 0 || currentObjRequest != null || isPaused || isDestoryed) {
            return;
        }
        if (Looper.getMainLooper() != Looper.myLooper()) {
            startReadNextJob();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startReadNextJob();
                }
            }).start();
        }
    }


    /**
     * 如果当前正在朗读/播放,则立即停止朗读
     */
    private synchronized void stopRead() {
        if (currentObjRequest != null) {
            if (!currentObjRequest.isFinish()) {
                currentObjRequest.finish();
                if (!currentObjRequest.readAloudObj().canAdd) {
                    canAdd = true;
                }
            }
            currentObjRequest = null;

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            textToSpeech.stop();
        }
    }


    /**
     * 开始朗读下一条实体.并且将该实体从队列里移除.(方法执行完时仅仅开始).
     * 如果队列里没有朗读实体,或已经在朗读过程中,或已经暂停,则直接return.
     */
    private void startReadNextJob() {
        initLock.waitUntilFinish();
        CurrentObjRequest currentObjRequesttemp = null;
        synchronized (this) {
            if (readAloudObjList.size() == 0 || currentObjRequest != null || isPaused || isDestoryed) {
                return;
            }
            ReadAloudObj readAloudObj = readAloudObjList.remove(0);
            currentObjRequest = new CurrentObjRequest(readAloudObj, onReadAloundListener, handler);
            currentObjRequesttemp = currentObjRequest;
        }


        boolean result = currentObjRequesttemp.prepare();//防止这里忽然被多线程置为null

        lock:
        {
            synchronized (this) {
                if (currentObjRequesttemp.isFinish()) {
                    return;
                }
                if (!result) {
                    currentObjRequesttemp.finish();
                    if (!currentObjRequesttemp.readAloudObj().canAdd) {
                        canAdd = true;
                    }
                    if (currentObjRequesttemp.equals(currentObjRequest)) {
                        //播放对象还是当前对象,则继续播放
                        currentObjRequest = null;
                        break lock;
                    }
                    return;
                }


                currentObjRequest.start();
                HashMap<String, String> params = new HashMap<String, String>();
                if (currentObjRequest.readAloudObj().getReadText() != null && currentObjRequest.readAloudObj().getPlayVoiceFilePath() == null) {
                    //只有文本,没有语音
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, currentObjRequest.readAloudObj().getIdPackage());
                    textToSpeech.speak(currentObjRequest.readAloudObj().getReadText(), TextToSpeech.QUEUE_ADD, params);
                    Log.e("语音播放", "执行第二步:语音播放" );
                } else if (currentObjRequest.readAloudObj().getReadText() != null && currentObjRequest.readAloudObj().getPlayVoiceFilePath() != null) {
                    //既有文本,又有语音
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, currentObjRequest.readAloudObj().getIdPackage());
                    textToSpeech.speak(currentObjRequest.readAloudObj().getReadText(), TextToSpeech.QUEUE_ADD, params);
                } else if (currentObjRequest.readAloudObj().getReadText() == null && currentObjRequest.readAloudObj().getPlayVoiceFilePath() != null) {
                    startPlayVoiceFile();
                }

                return;
            }
        }
        startReadNextJob();
    }


    //TTS朗读状态
    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {


        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            // !该回调无论如何都不是在主线程
            readFinish(utteranceId);
        }

        @Override
        public void onError(String utteranceId) {
            // !该回调无论如何都不是在主线程
            readFinish(utteranceId);
        }

        private void readFinish(String utteranceId) {
            // 看朗读完的内容是否含有声音
            // 如果不含声音则终止该朗读周期,允许speakList()再次执行
            // 如果含有声音则播放声音
            synchronized (ReadAloud.this) {
                //当触发朗读结束时,已经被停止
                if (isDestoryed || isPaused || currentObjRequest == null) {
                    return;
                }
                //当触发朗读结束时,已经换了一个朗读任务
                if (!currentObjRequest.readAloudObj().getIdPackage().equals(utteranceId)) {
                    return;
                }

                if (TextUtils.isEmpty(currentObjRequest.readAloudObj().getPlayVoiceFilePath())) {
                    // 说明不附带语音,直接进入下一个朗读周期
                    //反馈停止
                    if (!currentObjRequest.isFinish()) {
                        currentObjRequest.finish();
                        if (!currentObjRequest.readAloudObj().canAdd) {
                            canAdd = true;
                        }
                    }
                    currentObjRequest = null;
                    Log.e("语音播放", "执行第三步:语音播放结束" );
                    readNext();
                } else {
                    startPlayVoiceFile();
                }
            }
        }
    };


    /**
     * 开始播放语音.(方法执行完时仅仅开始)
     * 如果播放失败,则直接
     */
    private synchronized void startPlayVoiceFile() {
        if (isDestoryed || isPaused || currentObjRequest == null) {
            return;
        }
        if (TextUtils.isEmpty(currentObjRequest.readAloudObj().getPlayVoiceFilePath())) {
            return;
        }
        try {
            // 开始播放声音
//            if (mediaPlayer != null) {
            mediaPlayer.reset();
//            }
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setOnCompletionListener(onCompletionListener);
            if (voiceChannel == 1) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mediaPlayer.setDataSource(currentObjRequest.readAloudObj().getPlayVoiceFilePath());
            mediaPlayer.prepare();//播放器开始准备,准备是异步的
            mediaPlayer.start();

            //避免在prepare()后直接调用start()，要用onPrepared来调用
            //这可以避免error(-38,0)
//                    mediaPlayer.start();
        } catch (Exception e) {
            // 播放失败则进入下一个朗读周期
            //反馈停止
            if (!currentObjRequest.isFinish()) {
                currentObjRequest.finish();
                if (!currentObjRequest.readAloudObj().canAdd) {
                    canAdd = true;
                }
            }

            currentObjRequest = null;
            readNext();
        }

    }

    //语音播放完毕
    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

            //!该回调无论如何都在主线程
            //!stop被调用后是不会触发onCompletion的
            synchronized (ReadAloud.this) {
                if (isDestoryed || isPaused) {
                    return;
                }

                //反馈结束
                if (currentObjRequest != null) {
                    if (!currentObjRequest.isFinish()) {
                        currentObjRequest.finish();
                        if (!currentObjRequest.readAloudObj().canAdd) {
                            canAdd = true;
                        }
                    }
                    currentObjRequest = null;
                }

                // 播放完成,进入下一个朗读周期
                readNext();
            }
        }
    };


}
