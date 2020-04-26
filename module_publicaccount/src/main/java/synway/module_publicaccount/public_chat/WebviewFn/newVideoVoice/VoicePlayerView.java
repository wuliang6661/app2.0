package synway.module_publicaccount.public_chat.WebviewFn.newVideoVoice;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import synway.module_publicaccount.R;
import synway.module_publicaccount.until.TimeToDate;


/**
 * Created by admin on 2016/12/14.
 */
public class VoicePlayerView extends RelativeLayout {
    /* 控件 */
    private Context context;
    private View view = null;
    private ImageView btnPlay_Pause,back,forward,markword;
    private TextView txtPlayInfo, txtPlayState,txtPlayStartTime;
    private SeekBar seekBar = null;
    private View playView = null;
    private Animation taShow, taHide;
    private URLMediaPlayer streamPlayer = null;
    private String mediiaur="";
    private Handler handler = null;
    private WebView webView=null;
    private int allPlayTime;
    public VoicePlayerView(Context context,AttributeSet attributeSet) {
        super(context,attributeSet);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.model_newvoice_layout_voice, this);
        handler=new Handler();
        init();
//        setAnimotion();
//        // 重置播放界面,防止XML布局中的默认字
//        playView_Reset();
//        // 初始化工具
//        streamPlayer = new URLMediaPlayer();
//        streamPlayer.setOnPlayerListen(onStreamPlayerListen);
//        playView_Show();

    }
    //设置Webview监听
    public void setWebviewListener(WebView webView){
        this.webView=webView;
    }
    //設置播放地址
    public  void  setmediaurl(String mediaurl){
     this.mediiaur=mediaurl;
    }
    //開始播放
    public  void  start(){
        setAnimotion();
        // 重置播放界面,防止XML布局中的默认字
        playView_Reset();
        if(streamPlayer!=null) {
            streamPlayer.destory();
        }
        // 初始化工具
        txtPlayStartTime.setText("00:00");
        markword.setEnabled(false);
        streamPlayer = new URLMediaPlayer();
        streamPlayer.setOnPlayerListen(onStreamPlayerListen);
        playView_Show();
        VoiceReportObj voiceReportObj=new VoiceReportObj();
        voiceReportObj.mediaujrl=mediiaur;
        streamPlayer.startPlay(voiceReportObj);
        btnPlay_Pause.setImageResource(R.drawable.reporthistory_player_pause_st);
    }
   //停止播放
    public  void  destory(){
        playView_Reset_Hide();
        if(streamPlayer!=null) {
            streamPlayer.destory();
        }
//        pausePlayReport();
    }
    public void init() {
        // 初始化控件
        txtPlayInfo = findViewById(R.id.title);
        txtPlayStartTime= findViewById(R.id.starttime);
        txtPlayState = findViewById(R.id.textView2);
        playView = findViewById(R.id.linearLayout1);
        playView.setVisibility(View.GONE);
        seekBar = findViewById(R.id.seekBar1);
        btnPlay_Pause = findViewById(R.id.button1);
//        btnStop = (ImageView) findViewById(R.id.button2);
        txtPlayInfo.setOnClickListener(onClickListener);
        btnPlay_Pause.setOnClickListener(onClickListener);
//        btnStop.setOnClickListener(onClickListener);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        markword= findViewById(R.id.markword);
        markword.setOnClickListener(onClickListener);
        markword.setEnabled(false);

    }

    /**
     * 显示播放界面
     */
    private void playView_Show() {
        if (playView.getVisibility() == View.GONE) {
            playView.setVisibility(View.VISIBLE);
            playView.startAnimation(taShow);
        }
    }

    /***
     * 动画
     */
    public void setAnimotion() {
        // 播放器动画
        taShow = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        taShow.setDuration(500);
        taHide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        taHide.setDuration(500);
        taHide.setAnimationListener(taHideListener);
    }

    /**
     * 重置播放界面
     */
    private void playView_Reset() {
        btnPlay_Pause.setImageResource(R.drawable.reporthistory_player_start_st);
        txtPlayState.setText(null);
        seekBar.setEnabled(false);
    }

    /**
     * 按钮监听
     */
    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.equals(btnPlay_Pause)) {
                // 暂停和继续按钮
                if (streamPlayer.isPlaying()) {
                    pausePlayReport();
                } else {
                    resumePlayReport();
                }
            }
//            else if (v.equals(btnStop)) {
//                streamPlayer.stopPlay();
//                playView_Reset_Hide();
//            }
            else if (v.equals(txtPlayInfo)) {
                VoiceReportObj voiceItemObj = streamPlayer.getRunningReport();
                if (voiceItemObj != null) {
//                    Toast.makeText(context, "VoiceReportInfo:" + voiceItemObj.callNumber, Toast.LENGTH_LONG).show();
                }
            }else if(v.equals(markword)){
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:markFn()");
                    }
                });
            }

        }
    };
    private void pausePlayReport() {
        if (streamPlayer.getRunningReport() != null && !streamPlayer.isPrepareing()) {
            streamPlayer.pausePlay();
            btnPlay_Pause.setImageResource(R.drawable.reporthistory_player_start_st);
        }
    }

    private void resumePlayReport() {
        if (streamPlayer.getRunningReport() != null && !streamPlayer.isPrepareing()) {
            streamPlayer.resumePlay();
            btnPlay_Pause.setImageResource(R.drawable.reporthistory_player_pause_st);
//            txtPlayState.setText(streamPlayer.getAllTime() / 60 + "分" + streamPlayer.getAllTime() % 60 + "秒");
//            txtPlayState.setText(streamPlayer.getAllTime() + "秒");
            txtPlayState.setText( TimeToDate.secToTime(streamPlayer.getAllTime()));
        }
    }
    public int playtime;
    // 播放器反馈接口
    private OnPlayerStateListen onStreamPlayerListen = new OnPlayerStateListen() {

        @Override
        public void onPlayerStart(int allTime) {
            allPlayTime=allTime;
            // 播放器开始播放
            txtPlayInfo.setText("语音播放");
//            String time= TimeToDate.secToTime(allTime);
            txtPlayState.setText(TimeToDate.secToTime(allTime));
            seekBar.setEnabled(true);
            markword.setEnabled(true);
        }

        @Override
        public void onPlayerProgress(int progress, int playTime) {
             playtime=playTime;
            seekBar.setProgress(progress);
            new Thread(){
                public void run(){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            txtPlayStartTime.setText( TimeToDate.secToTime(playtime));
                        }
                    });

                }
            }.start();
        }




        @Override
        public void onPlayerLoading(int progress) {
            seekBar.setSecondaryProgress(progress);
        }

        @Override
        public void onPlayerComplete() {
//            txtPlayState.setText("播放完成");
            txtPlayStartTime.setText("播放完成");
            seekBar.setProgress(100);
            btnPlay_Pause.setImageResource(R.drawable.reporthistory_player_start_st);
            webView.loadUrl("javascript:nextRecordFn()");

        }

        @Override
        public void onPlayerError(String error, VoiceReportObj voiceReportObj) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            markword.setEnabled(false);
//            playView_Reset_Hide();

        }
    };

    /**
     * 隐藏并且重置播放界面
     */
    private void playView_Reset_Hide() {
        if (playView.getVisibility() == View.VISIBLE) {
            playView.startAnimation(taHide);
            playView.setVisibility(View.GONE);
            btnPlay_Pause.setImageResource(R.drawable.reporthistory_player_start_st);
            txtPlayState.setText(null);
            seekBar.setEnabled(false);
        }
    }

    // 进度条拖动
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (streamPlayer.getRunningReport() != null && !streamPlayer.isPrepareing()) {
                streamPlayer.setProgress(seekBar.getProgress());
                //设置拖动条拖动时间
//            txtPlayStartTime.setText( TimeToDate.secToTime(playtime));
            } else {
                seekBar.setProgress(0);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            txtPlayStartTime.setText( TimeToDate.secToTime((int)(((double) progress / 100)*allPlayTime)));
//            Log.i("testy", "得到的进度条进度是" + seekBar.getProgress());
//            Log.i("testy","得到的进度时间是"+((double) seekBar.getProgress() / 100)*allPlayTime);

        }
    };
    // 隐藏动画结束监听,因为要先执行动画,执行完毕再隐藏播放器.不像显示那样可以先出来播放器再播放动画
    private Animation.AnimationListener taHideListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            playView.setVisibility(View.GONE);
            seekBar.setProgress(0);
            seekBar.setSecondaryProgress(0);
            txtPlayState.setText("");
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };
}
