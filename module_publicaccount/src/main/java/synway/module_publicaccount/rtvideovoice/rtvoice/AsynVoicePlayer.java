package synway.module_publicaccount.rtvideovoice.rtvoice;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by 13itch on 2016/4/27.
 */
public class AsynVoicePlayer implements IjkMediaPlayer.OnCompletionListener,
        IjkMediaPlayer.OnInfoListener,
        IjkMediaPlayer.OnErrorListener,
        IjkMediaPlayer.OnPreparedListener {

    private MediaController mMediaController;
    private AudioPlayer mAudioPlayer;
    //    private PLVideoView mAudioPlayer;
    private Handler handler = null;
    private OnVociePlayStartLsn onVociePlayStartLsn = null;
    private Context context = null;
    private Thread thread = null;

    private String path = "";


    public AsynVoicePlayer(Context context, String path) {
        this.path = path;
        handler = new Handler();
        this.context = context;
        boolean useFastForward = false;
        boolean disableProgressBar = true;
        mMediaController = new MediaController(context, useFastForward, disableProgressBar);
    }

    public void setListen(OnVociePlayStartLsn onVociePlayStartLsn) {
        this.onVociePlayStartLsn = onVociePlayStartLsn;
    }

    public void start() {
        StartRunnable.run();
    }

    public void blockJion() {
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        mMediaController.hide();
        onVociePlayStartLsn = null;
        thread = new Thread(stopRunnable);
        thread.start();


    }
    public static final String KEY_BUFFER_TIME = "rtmp_buffer";
    public static final String KEY_GET_AV_FRAME_TIMEOUT = "get-av-frame-timeout";
    public static final String KEY_FFLAGS = "fflags";
    public static final String VALUE_FFLAGS_NOBUFFER = "nobuffer";

    private Runnable StartRunnable = new Runnable() {
        @Override
        public void run() {
            mAudioPlayer = new AudioPlayer(context);
//            mAudioPlayer = new PLVideoView(context);
            AVOptions options = new AVOptions();
            options.setInteger(AVOptions.KEY_MEDIACODEC, 1); // 1 -> enable, 0 -> disable
            options.setInteger(KEY_BUFFER_TIME, 1000); // the unit of buffer time is ms
            options.setInteger(KEY_GET_AV_FRAME_TIMEOUT, 8 * 1000); // the unit of timeout is ms
            options.setString(KEY_FFLAGS, VALUE_FFLAGS_NOBUFFER); // "nobuffer"
            options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
            mAudioPlayer.setAVOptions(options);
            mMediaController.setMediaPlayer(mAudioPlayer);
            mMediaController.setVisibility(View.GONE);
            mAudioPlayer.setMediaController(mMediaController);
            mAudioPlayer.setOnErrorListener(AsynVoicePlayer.this);
            mAudioPlayer.setOnCompletionListener(AsynVoicePlayer.this);
            mAudioPlayer.setOnInfoListener(AsynVoicePlayer.this);
            mAudioPlayer.setOnPreparedListener(AsynVoicePlayer.this);
            mAudioPlayer.setAudioPath(path);
//            mAudioPlayer.setVideoPath(path);
            mAudioPlayer.start();
        }
    };

    private Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAudioPlayer != null) {
                mAudioPlayer.pause();
                mAudioPlayer.stopPlayback();
            }
        }
    };

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mAudioPlayer.stopPlayback();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onVociePlayStartLsn != null) {
                    onVociePlayStartLsn.onCompletion();
                }
            }
        });
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        mAudioPlayer.stopPlayback();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onVociePlayStartLsn != null) {
                    onVociePlayStartLsn.onError();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        return true;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {

    }

    public interface OnVociePlayStartLsn {
        /**
         * 错误
         */
        void onError();

        /**
         * 加载完成，可以理解为缓冲完成
         */
        void onPrepared();

        /**
         * 播放完成，播放完时调用
         */
        void onCompletion();
    }

}
