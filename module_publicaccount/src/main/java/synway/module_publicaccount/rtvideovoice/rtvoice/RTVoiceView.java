package synway.module_publicaccount.rtvideovoice.rtvoice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ant.liao.GifView;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import synway.module_publicaccount.R;

/**
 * 实时音频播放控件
 * <p/>
 * 该控件生命周期以{@link RTVoiceView#init()}开始 ，以{@link RTVoiceView#destroy()}结束。<br><br>
 * 使用的步骤为：初始化控件{@link RTVoiceView#init()}，设置播放地址{@link RTVoiceView#resetRTMPurl(String)} ,
 * 开始播放{@link RTVoiceView#play()} ，停止播放{@link RTVoiceView#stop()} ,销毁控件{@link RTVoiceView#destroy()} ；<br><br>
 * 如果播放的过程想要更换播放地址，请先调用{@link RTVoiceView#stop()}后再设置新的播放地址，最后再调用{@link RTVoiceView#play()}
 */
public class RTVoiceView extends RelativeLayout {

    private TextView textView = null;
    private ImageButton palyImageView = null;
    private ImageView error_imageView = null;
    private GifImageView playingView = null;
//    private ProgressBar playingView=null;
    private Context context = null;
    private View view = null;
    private ProgressBar progressBar = null;
    private AsynVoicePlayer asynVoicePlayer = null;
    private Animation taShow, taHide;
    /**
     * 播放地址
     */
    private String mAudioPath;
    /**
     * 表示是否正在播放
     */
    private boolean playing = false;

    public RTVoiceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.model_rt_layout_voice, this);
        palyImageView = findViewById(R.id.imageButton);
        textView = findViewById(R.id.textView1);
//        playingView=(ProgressBar)findViewById(R.id.proceimageView);
        playingView = findViewById(R.id.proceimageView);
        initGifView(playingView);
//        palyImageView.setOnClickListener(onClickListener);
        progressBar = findViewById(R.id.progressBar1);
        error_imageView = findViewById(R.id.imageView2);
    }

    /**
     * 初始化播放器
     */
    public void init() {

    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (playing) {
                stop();
            } else {
                play();
            }
        }
    };


    /**
     * 初始化gif动画
     */
//    private void initGifView(GifView playingView) {
//        // 从xml中得到GifView的句柄
//        GifView gf1 = playingView;
//        // 设置Gif图片源
//        gf1.setGifImage(R.drawable.voice);
//        // 设置显示的大小，拉伸或者压缩
//        gf1.setShowDimension(dip2px(context, 67), dip2px(context, 50));
//        // 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
//        gf1.setGifImageType(GifView.GifImageType.COVER);
//        gf1.setVisibility(View.INVISIBLE);
//    }
    private void initGifView(GifImageView gif1) {
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.voice);
            gif1.setImageDrawable(gifDrawable);
            gif1.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置显示文字
     */
    public void setText(String text) {
        textView.setText(text);
    }

    /**
     * 停止播放
     */
    public void stop() {
        playing = false;
        ReSetButtonUI(playing);
        if (asynVoicePlayer != null) {
            asynVoicePlayer.stop();
        }
    }

    /**
     * 开始播放
     */
    public void play() {
        error_imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        playing = true;

        if (asynVoicePlayer != null) {
//            asynVoicePlayer.stop();
            asynVoicePlayer.blockJion();
//            Log.i("lmly3", "blockJion");
        }

        asynVoicePlayer = new AsynVoicePlayer(context, mAudioPath);
        asynVoicePlayer.setListen(onVociePlayStartLsn);
        asynVoicePlayer.start();
        ReSetButtonUI(playing);
    }

    private AsynVoicePlayer.OnVociePlayStartLsn onVociePlayStartLsn = new AsynVoicePlayer.OnVociePlayStartLsn() {

        @Override
        public void onError() {
            if(onPlayListen!=null){
                onPlayListen.onError();
            }
            progressBar.setVisibility(View.GONE);
            destroy();
            playing = false;
            ReSetButtonUI(playing);
            error_imageView.setVisibility(View.VISIBLE);

        }

        @Override
        public void onPrepared() {
            progressBar.setVisibility(View.GONE);
            playingView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCompletion() {
            progressBar.setVisibility(View.GONE);
            stop();
            if (onPlayListen != null) {
                onPlayListen.onPlayCompletion();
            }
        }
    };

    /**
     * 更新播放按钮的样式
     *
     * @param isplay 是否正在播放
     */
    private void ReSetButtonUI(boolean isplay) {
        if (isplay) {
            palyImageView.setBackground(getResources().getDrawable(R.drawable.voice_stop));
            playingView.setVisibility(View.INVISIBLE);
        } else {
            palyImageView.setBackground(getResources().getDrawable(R.drawable.voice_play));
            playingView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置RTMP播放地址
     */
    public void resetRTMPurl(String url) {
        mAudioPath = url;
    }

    /**
     * dp转px
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 销毁播放控件
     */
    public void destroy() {
        stop();
        removeOnPlayListen();
    }


    private OnPlayListen onPlayListen;

    public void setOnPlayListen(OnPlayListen onPlayListen) {
        this.onPlayListen = onPlayListen;
    }

    public void removeOnPlayListen() {
        this.onPlayListen = null;
    }

    public interface OnPlayListen {
        void onPlayCompletion();

        void onError();
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
    // 隐藏动画结束监听,因为要先执行动画,执行完毕再隐藏播放器.不像显示那样可以先出来播放器再播放动画
    private Animation.AnimationListener taHideListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            view.setVisibility(View.GONE);

        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };
    /**
     * 显示播放界面
     */
    public void playView_Show() {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(taShow);
        }
    }
    /**
     * 隐藏并且重置播放界面
     */
    public void playView_Reset_Hide() {
        if (view.getVisibility() == View.VISIBLE) {
            view.startAnimation(taHide);
            view.setVisibility(View.GONE);
        }
    }
}
