package qyc.library.control.view_voicerecord;

import android.content.Context;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import synway.common.R;


/**
 * <p>
 * 负责录音，以及录音的提示音，特效。 <br>
 * 将这个View直接拖到XML布局中，在界面中通过findViewByID进行初始化。
 * <p>
 * 控件的生命周期{@link VoiceRecordView#onCreat(String folderPath, int streamType)}--
 * {@link VoiceRecordView#onDestory()}<br>
 * 录音的生命周期{@link VoiceRecordView#startRecord(boolean isDiDa)}--
 * {@link VoiceRecordView#stopRecord(boolean isXiu)}或 {@link VoiceRecordView#cancleRecord()}
 * </br> 其他特效 {@link VoiceRecordView#showCancelTip()} --
 * {@link VoiceRecordView#showRecording(boolean isShowCancle)}
 *
 * @author 钱园超 2016年1月11日下午4:44:52
 */
public class VoiceRecordView extends RelativeLayout {


    // 控件
    private LinearLayout includ_LayoutRecord = null;// 特效图像
    private ImageView includ_ImgVolume = null;// 特效图像
    private ImageView includ_ImgCancel = null;// 特效图像
    private TextView includ_TxtRecording = null;// 特效图像

    // 录音工具
    private VoiceRecord voiceRecorder = null;

    // 提示音工具
    /**
     * @第一个参数 第一个参数为音频池最多支持装载多少个音频
     * @第二个参数 指定声音的类型
     * @第三个参数 音频的质量，默认为0
     */
    private SoundPool soundPool = null;
    private SparseIntArray spMap = null;

    // 主线程工具
    private Handler handler = null;

    // 观察者
    private OnVoiceViewListen onVoiceViewListen = null;

    // 控件防重复控制
    private int viewState = 0;
    // 延时录制控制（是否正在延时录制）
    private volatile boolean isDelayRecording = false;
    // 延时销毁控制（是否已销毁）
    private boolean isDestoryed = false;

    public VoiceRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            inflate(getContext(), R.layout.lib_voicerecordview_cv, this);
        } else {
            voiceView(context);
        }
    }

    public VoiceRecordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            inflate(getContext(), R.layout.lib_voicerecordview_cv, this);
        } else {
            voiceView(context);
        }
    }

    public VoiceRecordView(Context context) {
        super(context);
        if (isInEditMode()) {
            inflate(getContext(), R.layout.lib_voicerecordview_cv, this);
        } else {
            voiceView(context);
        }
    }

    public void voiceView(Context context) {
        handler = new Handler();
        inflate(getContext(), R.layout.lib_voicerecordview_cv, this);
        includ_LayoutRecord = findViewById(R.id.lib_linearLayout1);
        includ_ImgVolume = findViewById(R.id.lib_imageView2);
        includ_ImgCancel = findViewById(R.id.lib_imageView3);
        includ_TxtRecording = findViewById(R.id.lib_textView6);

        setVisibility(View.GONE);
    }

    /**
     * @param streamType 如 AudioManager.STREAM_RING
     */
    public void setStreamType(int streamType) {
        if (soundPool != null) {
            soundPool.release();
        }
        soundPool = new SoundPool(1, streamType, 0);// 自由选择声道

        if (spMap != null) {
            spMap.clear();
        } else {
            spMap = new SparseIntArray();
        }

        // 将语音加载到 soundpool ，这是一个异步的过程，
        // 也就是说，在播放音频的时候，很可能此段音频还没有装载到音频池中，
        spMap.put(1, soundPool.load(getContext(), R.raw.lib_startrecord,1));//播放时长为575ms
        spMap.put(2, soundPool.load(getContext(), R.raw.lib_sendover, 1));//播放时长为287ms
    }

    public void setOnVoiceViewListen(OnVoiceViewListen onVoiceViewListen) {
        this.onVoiceViewListen = onVoiceViewListen;
    }

    /**
     * 生命周期：对应界面的onCreat
     *
     * @param folderPath
     * @param streamType 如 AudioManager.STREAM_RING
     */
    public void onCreat(String folderPath, int streamType) {
        voiceRecorder = new VoiceRecord(getContext(), folderPath);
        setStreamType(streamType);
    }


    /**
     * 生命周期：对应界面的onDestory
     */
    public void onDestory() {
        isDestoryed = true;
        if(run_DelayRecord!=null) {
            handler.removeCallbacks(run_DelayRecord);
        }
        handler.removeCallbacks(run_VoiceAmplitude);// 停止录音音量监视
        voiceRecorder.destory();
        soundPool.release();
    }


    /**
     * 开始录音。开启滴答声则默认不显示取消文字，反之则显示取消文字
     *
     * @param isDiDa 是否开启滴答声
     */
    public void startRecord(boolean isDiDa) {
        startRecord(isDiDa, !isDiDa);
    }


    /**
     * 开始录音，开始“录音”特效 <br>
     * 如已经在录音，则仅开始“录音”特效
     *
     * @param isDiDa           开启滴答声
     * @param isShowCancleText 显示取消文字
     */
    public void startRecord(boolean isDiDa, final boolean isShowCancleText) {

        while (isDelayRecording) {
            // 结束增加了延时，如果在延时结束的时间里又启动了，这里就要等！
            sleep(100);
        }
        if (onVoiceViewListen != null) {
            onVoiceViewListen.onRecordStart();
        }

        if (isDiDa) {
            /***
             * 播放声音
             *
             * @para1 soundID参数为资源ID
             * @para2 左声道的音量
             * @para3 右声道的音量
             * @para4 音频质量，暂时没有实际意义，传0即可
             * @para5 循环次数，0为播放一次，-1为无线循环，其他正数+1为播放次数，如传递3，循环播放4次
             * @para6 播放速率，从大到小取0.0f~2.0f，1.0f为正常速率播放
             */
            soundPool.play(spMap.get(1), 1.0f, 1.0f, 0, 0, 1.0f);// 发出开始提示音,这是异步的,声音播放过程中代码会继续往下走
            this.run_DelayRecord=new Runnable() {
                @Override
                public void run() {
                    viewRecording(isShowCancleText);// 控件样式
                    //这段音频的播放时长为575ms
                    voiceRecorder.start();// 开始录音
                    handler.postDelayed(run_VoiceAmplitude, 100);// 录音音量监视
                }
            };
            handler.postDelayed(run_DelayRecord,600);
        }else
        {
            viewRecording(isShowCancleText);// 控件样式
            voiceRecorder.start();// 开始录音
            handler.postDelayed(run_VoiceAmplitude, 100);// 录音音量监视
        }

    }

    private Runnable run_DelayRecord=null;

    /**
     * 退出录音，已录部分作废，并且隐藏特效
     */
    public void cancleRecord() {
        if(run_DelayRecord!=null) {
            handler.removeCallbacks(run_DelayRecord);
        }
        handler.removeCallbacks(run_VoiceAmplitude);// 停止录音音量监视
        voiceRecorder.cancelRecord();// 取消录音
        viewHide();// 控件样式
    }

    /**
     * 完成录音，并且隐藏特效
     */
    public void stopRecord(boolean isXiu) {
        if(run_DelayRecord!=null) {
            handler.removeCallbacks(run_DelayRecord);
        }
        handler.removeCallbacks(run_VoiceAmplitude);// 停止录音音量监视
        // 计算500毫秒延时后，是否达到1秒的录音时间
        long recordTime = voiceRecorder.getCurrentRecordTime();
        if (recordTime + 300 < 1000) {
            // 时间没到
            voiceRecorder.cancelRecord();
            viewTooShort();
        } else {
            viewHide();// 时间到了，隐藏界面

            if (isXiu) {
                soundPool.play(spMap.get(2), 1.0f, 1.0f, 0, 0, 1.0f);// 发出完成提示音
            }
            isDelayRecording = true;// 表示进入延时录制状态
            // 启动延时线程
            new Thread(new Runnable() {

                @Override
                public void run() {
                    sleep(300);
                    if (isDestoryed) {
                        return;
                    }

                    final int recordTime = voiceRecorder.finish();// 停止录音
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (onVoiceViewListen != null) {
                                onVoiceViewListen.onRecordFinish(voiceRecorder.getFileName(), recordTime);
                            }
                        }
                    });

                    isDelayRecording = false;
                }
            }).start();
        }
    }

    /**
     * 显示“取消”特效
     */
    public void showCancelTip() {
        viewCancelTip();
    }

    /**
     * 显示“录音”特效
     */
    public void showRecording(boolean isShowCancle) {
        viewRecording(isShowCancle);
    }

    private Runnable run_VoiceAmplitude = new Runnable() {
        @Override
        public void run() {
            int amplitude = (int) voiceRecorder.getAmplitude();
            switch (amplitude) {
                case 0:
                    includ_ImgVolume.setImageResource(R.drawable.lib_voicerecordview_amp1_png);
                    break;
                case 1:
                    includ_ImgVolume.setImageResource(R.drawable.lib_voicerecordview_amp1_png);
                    break;
                case 2:
                    includ_ImgVolume.setImageResource(R.drawable.lib_voicerecordview_amp2_png);
                    break;
                case 3:
                case 4:
                    includ_ImgVolume.setImageResource(R.drawable.lib_voicerecordview_amp3_png);
                    break;
                case 5:
                case 6:
                    includ_ImgVolume.setImageResource(R.drawable.lib_voicerecordview_amp4_png);
                    break;
                case 7:
                case 8:
                    includ_ImgVolume.setImageResource(R.drawable.lib_voicerecordview_amp5_png);
                    break;
                case 9:
                case 10:
                    includ_ImgVolume.setImageResource(R.drawable.lib_voicerecordview_amp6_png);
                    break;
                default:
                    includ_ImgVolume.setImageResource(R.drawable.lib_voicerecordview_amp1_png);
                    break;
            }

            handler.postDelayed(run_VoiceAmplitude, 100);
        }
    };

    /**
     * 控件：正在录音
     */
    private void viewRecording(boolean isShowCancle) {
        if (viewState == 1) {
            return;
        }
        viewState = 1;
        setVisibility(View.VISIBLE);

        includ_LayoutRecord.setVisibility(View.VISIBLE);
        includ_ImgCancel.setVisibility(View.INVISIBLE);

        includ_TxtRecording.setBackgroundColor(Color.TRANSPARENT);
        if (isShowCancle) {
            includ_TxtRecording.setText("手指上滑,取消发送");
        } else {
            includ_TxtRecording.setText("正在录制");
        }

    }

    /**
     * 控件：隐藏
     */
    private void viewHide() {
        if (viewState == 2) {
            return;
        }
        viewState = 2;
        setVisibility(View.GONE);
    }

    /**
     * 控件：松开手指取消发送
     */
    private void viewCancelTip() {
        if (viewState == 3) {
            return;
        }
        viewState = 3;
        setVisibility(View.VISIBLE);

        includ_LayoutRecord.setVisibility(View.INVISIBLE);
        includ_ImgCancel.setImageResource(R.drawable.lib_voicerecordview_cancel_png);
        includ_ImgCancel.setVisibility(View.VISIBLE);

        // #964B4B
        includ_TxtRecording.setBackgroundColor(Color.rgb(150, 75, 75));
        includ_TxtRecording.setText("松开手指,取消发送");
    }

    /**
     * 控件：录音时间太短
     */
    private void viewTooShort() {
        if (viewState == 4) {
            return;
        }
        viewState = 4;
        setVisibility(View.VISIBLE);

        includ_LayoutRecord.setVisibility(View.INVISIBLE);

        includ_ImgCancel.setImageResource(R.drawable.lib_voicerecordview_tooshort);
        includ_ImgCancel.setVisibility(View.VISIBLE);

        includ_TxtRecording.setVisibility(View.VISIBLE);
        includ_TxtRecording.setText("录音时间太短");

        // #964B4B
        includ_TxtRecording.setBackgroundColor(Color.rgb(150, 75, 75));

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (viewState == 4) {
                    // 如果界面没有被切走
                    viewHide();
                }
            }
        }, 1000);
    }

    private static final void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public interface OnVoiceViewListen {
        /**
         * 开始录制
         */
        void onRecordStart();

        /**
         * 录制完成
         *
         * @param fileName   录制文件的文件名(不含路径)
         * @param recordTime 录制声音的长度(秒)
         */
        void onRecordFinish(String fileName, int recordTime);
    }

    /**
     * 把按住录音那个view传进来，这里替你包办触摸屏事件
     *
     * @param touchView
     * @param voiceView
     * @param soundTip
     */
    public static final void touchViewHelper(View touchView, VoiceRecordView voiceView, boolean soundTip) {
        touchViewHelper(touchView, voiceView, null, soundTip);
    }

    /**
     * 把按住录音那个view传进来，这里替你包办触摸屏事件
     *
     * @param touchView
     * @param voiceView
     * @param onTouchViewHelperListener
     * @param soundTip
     */
    public static final void touchViewHelper(View touchView, VoiceRecordView voiceView,
                                             OnTouchViewHelperListener onTouchViewHelperListener, boolean soundTip) {
        final OnTouchViewHelperListener onTouchViewHelperListenerFinal = onTouchViewHelperListener;
        final VoiceRecordView voiceViewFinal = voiceView;
        final View touchViewFinal = touchView;
        final boolean soundTipFinal = soundTip;
        touchView.setOnTouchListener(new OnTouchListener() {
            private boolean isVioceCancle = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    if (onTouchViewHelperListenerFinal != null) {
                        onTouchViewHelperListenerFinal.onTouchViewDown();
                    }
                    voiceViewFinal.startRecord(soundTipFinal);
                    return false;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (onTouchViewHelperListenerFinal != null) {
                        onTouchViewHelperListenerFinal.onTouchViewUp();
                    }
                    if (isVioceCancle) {
                        voiceViewFinal.cancleRecord();
                    } else {
                        voiceViewFinal.stopRecord(soundTipFinal);
                    }
                    isVioceCancle = false;
                    return false;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    if (Math.abs(event.getX()) > (touchViewFinal.getRight() - touchViewFinal.getLeft())
                            || Math.abs(event.getY()) > (touchViewFinal.getBottom() - touchViewFinal.getTop())) {
                        voiceViewFinal.showCancelTip();
                        isVioceCancle = true;
                    } else {
                        voiceViewFinal.showRecording(true);
                        isVioceCancle = false;
                    }
                    return true;
                }
                return touchViewFinal.performClick();
            }
        });
    }

    public interface OnTouchViewHelperListener {
        /**
         * TouchView按下事件
         */
        void onTouchViewDown();

        /**
         * TouchView弹起事件
         */
        void onTouchViewUp();
    }

}
