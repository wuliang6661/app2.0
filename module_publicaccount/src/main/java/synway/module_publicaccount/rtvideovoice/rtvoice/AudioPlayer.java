package synway.module_publicaccount.rtvideovoice.rtvoice;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;

//import com.pili.pldroid.player.AVOptions;
//import com.pili.pldroid.player.SharedLibraryNameHelper;
//import com.pili.pldroid.player.common.Util;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkLibLoader;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import synway.module_publicaccount.rtvideovoice.rtvoice.IMediaController.Proxy;

public class AudioPlayer implements IMediaController.MediaPlayerControl {
    private static final String TAG = "AudioPlayer";
    private Uri mUri;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private int mCurrentState = 0;
    private int mTargetState = 0;
    private IMediaPlayer mMediaPlayer = null;
    private Proxy mMediaController;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
    private int mCurrentBufferPercentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private long mSeekWhenPrepared;
    private boolean mCanPause;
    private boolean mCanSeekBack;
    private boolean mCanSeekForward;
    private float mBufferTime = 3000.0F;
    private AVOptions mAVOptions;
    private View mMediaBufferingIndicator;
    private Context mContext;
    private static volatile boolean mIsLibLoaded = false;
    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            Log.i("AudioPlayer", "onPrepared");
            AudioPlayer.this.mCurrentState = 2;
            AudioPlayer.this.mCanPause = AudioPlayer.this.mCanSeekBack = AudioPlayer.this.mCanSeekForward = true;
            if (AudioPlayer.this.mOnPreparedListener != null) {
                AudioPlayer.this.mOnPreparedListener.onPrepared(AudioPlayer.this.mMediaPlayer);
            }

            if (AudioPlayer.this.mMediaController != null) {
                AudioPlayer.this.mMediaController.setEnabled(true);
            }

            long seekToPosition = AudioPlayer.this.mSeekWhenPrepared;
            if (seekToPosition != 0L) {
                AudioPlayer.this.seekTo(seekToPosition);
            }

            if (AudioPlayer.this.mTargetState == 3) {
                AudioPlayer.this.start();
                Log.i("AudioPlayer", "mMediaController.show");
                if (AudioPlayer.this.mMediaController != null) {
                    AudioPlayer.this.mMediaController.show();
                }
            } else if (!AudioPlayer.this.isPlaying() && (seekToPosition != 0L || AudioPlayer.this.getCurrentPosition() > 0L) && AudioPlayer.this.mMediaController != null) {
                AudioPlayer.this.mMediaController.show(0);
            }

        }
    };
    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        public void onSeekComplete(IMediaPlayer mp) {
            Log.d("AudioPlayer", "onSeekComplete");
            if (AudioPlayer.this.mOnSeekCompleteListener != null) {
                AudioPlayer.this.mOnSeekCompleteListener.onSeekComplete(mp);
            }

        }
    };
    private IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
        public void onCompletion(IMediaPlayer mp) {
            Log.i("AudioPlayer", "onCompletion");
            AudioPlayer.this.mCurrentState = 5;
            AudioPlayer.this.mTargetState = 5;
            if (AudioPlayer.this.mMediaController != null) {
                AudioPlayer.this.mMediaController.hide();
            }

            if (AudioPlayer.this.mOnCompletionListener != null) {
                AudioPlayer.this.mOnCompletionListener.onCompletion(AudioPlayer.this.mMediaPlayer);
            }

        }
    };
    private IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (AudioPlayer.this.mOnInfoListener != null) {
                AudioPlayer.this.mOnInfoListener.onInfo(mp, what, extra);
            } else if (AudioPlayer.this.mMediaPlayer != null) {
                if (what == 701) {
                    Log.i("AudioPlayer", "onInfo: (MEDIA_INFO_BUFFERING_START)");
                    if (AudioPlayer.this.mMediaBufferingIndicator != null) {
                        AudioPlayer.this.mMediaBufferingIndicator.setVisibility(View.VISIBLE);
                    }
                } else if (what == 702) {
                    Log.i("AudioPlayer", "onInfo: (MEDIA_INFO_BUFFERING_END)");
                    if (AudioPlayer.this.mMediaBufferingIndicator != null) {
                        AudioPlayer.this.mMediaBufferingIndicator.setVisibility(View.GONE);
                    }
                }
            }

            return true;
        }
    };
    private IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            Log.d("AudioPlayer", "Error: " + what + "," + extra);
            AudioPlayer.this.mCurrentState = -1;
            AudioPlayer.this.mTargetState = -1;
            if (AudioPlayer.this.mMediaController != null) {
            }

            return AudioPlayer.this.mOnErrorListener != null && AudioPlayer.this.mOnErrorListener.onError(AudioPlayer.this.mMediaPlayer, what, extra) ? true : true;
        }
    };
    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            Log.i("AudioPlayer", "onBufferingUpdate");
            AudioPlayer.this.mCurrentBufferPercentage = percent;
        }
    };

    public AudioPlayer(Context context) {
        this.mContext = context;
        this.mCurrentState = 0;
        this.mTargetState = 0;
    }

    public void setAudioPath(String path) {
        this.setURI(Uri.parse(path));
    }

    public void setURI(Uri uri) {
        this.mUri = uri;
        this.mSeekWhenPrepared = 0L;
        this.openAudio();
    }

    public void setMediaController(IMediaController controller) {
        if (this.mMediaController != null) {
            this.mMediaController.hide();
        }

        this.mMediaController = new Proxy(controller);
        this.mMediaController.setAnchorView(null);
    }

    public void stopPlayback() {
        if (this.mMediaPlayer != null) {
            this.release(true);
        }

    }

    private void setLiveStreamingOptions(IjkMediaPlayer ijkMediaPlayer) {
        Log.i("AudioPlayer", "setLiveStreamingOptions");
        if (this.mAVOptions != null && this.mAVOptions.containsKey("fflags")) {
            ijkMediaPlayer.setOption(1, "fflags", this.mAVOptions.getString("fflags"));
        }

        ijkMediaPlayer.setOption(1, "analyzeduration", 1000L);
        ijkMediaPlayer.setOption(1, "probesize", 4096L);
        ijkMediaPlayer.setOption(1, "rtmp_live", 1L);
        ijkMediaPlayer.setOption(1, "rtmp_buffer", this.mAVOptions != null && this.mAVOptions.containsKey("rtmp_buffer") ? (long) this.mAVOptions.getInteger("rtmp_buffer") : 100L);
    }

    private void setOptions(IjkMediaPlayer ijkMediaPlayer) {
        Log.i("AudioPlayer", "setOptions");
        ijkMediaPlayer.setOption(1, "analyzeduration", 2000000L);
        ijkMediaPlayer.setOption(1, "probesize", 4096L);
        ijkMediaPlayer.setOption(4, "live-streaming", 0L);
    }

    private void openAudio() {
        if (this.mUri != null) {
            this.release(false);
            AudioManager am = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
            am.requestAudioFocus(null, 3, 1);

            try {
                this.mCurrentBufferPercentage = 0;
                IjkMediaPlayer ex = null;
                if (this.mUri != null) {
                    ex = new IjkMediaPlayer(new IjkLibLoader() {
                        public void loadLibrary(String libName) throws UnsatisfiedLinkError, SecurityException {
                            String newLibName = "libpldroidplayer";//SharedLibraryNameHelper.getInstance().getSharedLibraryName();
                            if (!AudioPlayer.mIsLibLoaded) {
                                Log.i("AudioPlayer", "newLibName:" + newLibName);
                                AudioPlayer.mIsLibLoaded = true;
                                System.loadLibrary(newLibName);
                            }

                        }
                    });
                    ex.setOption(4, "framedrop", 12L);
                    ex.setOption(1, "http-detect-range-support", 0L);
                    Log.i("AudioPlayer", "mUri.getPath:" + this.mUri.toString());
                    boolean isLiveStreaming = false;
                    if (this.mAVOptions != null && this.mAVOptions.containsKey("live-streaming") && this.mAVOptions.getInteger("live-streaming") != 0) {
                        isLiveStreaming = true;
                    }

                    if (isLiveStreaming) {
                        this.setLiveStreamingOptions(ex);
                    } else {
                        this.setOptions(ex);
                    }

                    ex.setOption(4, "live-streaming", (long) (isLiveStreaming ? 1 : 0));
                    ex.setOption(4, "get-av-frame-timeout", this.mAVOptions != null && this.mAVOptions.containsKey("get-av-frame-timeout") ? (long) (this.mAVOptions.getInteger("get-av-frame-timeout") * 1000) : 10000000L);
                    ex.setOption(4, "mediacodec", this.mAVOptions != null && this.mAVOptions.containsKey("mediacodec") ? (long) this.mAVOptions.getInteger("mediacodec") : 1L);
                    ex.setOption(2, "skip_loop_filter", 48L);
                    ex.setOption(4, "start-on-prepared", 1L);
                    ex.setKeepInBackground(true);
                }

                this.mMediaPlayer = ex;
                this.mMediaPlayer.setOnPreparedListener(this.mPreparedListener);
                this.mMediaPlayer.setOnCompletionListener(this.mCompletionListener);
                this.mMediaPlayer.setOnErrorListener(this.mErrorListener);
                this.mMediaPlayer.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
                this.mMediaPlayer.setOnInfoListener(this.mInfoListener);
                this.mMediaPlayer.setOnSeekCompleteListener(this.mSeekCompleteListener);
                if (this.mUri != null) {
                    this.mMediaPlayer.setDataSource(this.mUri.toString());
                }

                this.mMediaPlayer.setDisplay(null);
                this.mMediaPlayer.setScreenOnWhilePlaying(true);
                this.mMediaPlayer.prepareAsync();
                this.mCurrentState = 1;
            } catch (IOException var4) {
                Log.e("AudioPlayer", "Unable to open content: " + this.mUri, var4);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            } catch (IllegalArgumentException var5) {
                Log.e("AudioPlayer", "Unable to open content: " + this.mUri, var5);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            }
        }
    }

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        this.mOnPreparedListener = l;
    }

    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        this.mOnCompletionListener = l;
    }

    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        this.mOnInfoListener = l;
    }

    private void release(boolean cleartargetstate) {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            if (cleartargetstate) {
                this.mTargetState = 0;
            }

            AudioManager am = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != 4 && keyCode != 24 && keyCode != 25 && keyCode != 164 && keyCode != 82 && keyCode != 5 && keyCode != 6;
        if (this.isInPlaybackState() && isKeyCodeSupported && this.mMediaController != null) {
            if (keyCode == 79 || keyCode == 85) {
                if (this.mMediaPlayer.isPlaying()) {
                    this.pause();
                    this.mMediaController.show();
                } else {
                    this.start();
                }

                return true;
            }

            if (keyCode == 126) {
                if (!this.mMediaPlayer.isPlaying()) {
                    this.start();
                }

                return true;
            }

            if (keyCode == 86 || keyCode == 127) {
                if (this.mMediaPlayer.isPlaying()) {
                    this.pause();
                    this.mMediaController.show();
                }

                return true;
            }

            this.toggleMediaControlsVisiblity();
        }

        return false;
    }

    private void toggleMediaControlsVisiblity() {
        if (this.mMediaController.isShowing()) {
            this.mMediaController.hide();
        } else {
            this.mMediaController.show();
        }

    }

    public void setMediaBufferingIndicator(View mediaBufferingIndicator) {
        if (this.mMediaBufferingIndicator != null) {
            this.mMediaBufferingIndicator.setVisibility(View.GONE);
        }

        this.mMediaBufferingIndicator = mediaBufferingIndicator;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.setVolume(leftVolume, rightVolume);
        }

    }

    public void start() {
        if (this.mCurrentState == 5 && !Util.isUrlLocalFile(this.mUri.toString())) {
            this.setURI(this.mUri);
            this.mTargetState = 3;
        } else {
            if (this.isInPlaybackState()) {
                this.mMediaPlayer.start();
                this.mCurrentState = 3;
            }

            this.mTargetState = 3;
        }
    }

    public void pause() {
        if (this.isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            this.mCurrentState = 4;
        }

        this.mTargetState = 4;
    }

    public long getDuration() {
        return this.isInPlaybackState() ? this.mMediaPlayer.getDuration() : -1L;
    }

    public long getCurrentPosition() {
        return this.isInPlaybackState() ? this.mMediaPlayer.getCurrentPosition() : 0L;
    }

    public void seekTo(long msec) {
        if (this.isInPlaybackState()) {
            this.mMediaPlayer.seekTo(msec);
            this.mSeekWhenPrepared = 0L;
        } else {
            this.mSeekWhenPrepared = msec;
        }

    }

    public boolean isPlaying() {
        return this.isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return this.mMediaPlayer != null ? this.mCurrentBufferPercentage : 0;
    }

    public boolean canPause() {
        return this.mCanPause;
    }

    public boolean canSeekBackward() {
        return this.mCanSeekBack;
    }

    public boolean canSeekForward() {
        return this.mCanSeekForward;
    }

    public void setBufferTime(float bufferTime) {
        this.mBufferTime = bufferTime;
    }

    public float getBufferTime() {
        return this.mBufferTime;
    }

    private boolean isInPlaybackState() {
        return this.mMediaPlayer != null && this.mCurrentState != -1 && this.mCurrentState != 0 && this.mCurrentState != 1;
    }

    public void setAVOptions(AVOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("Illegal options:" + options);
        } else {
            this.mAVOptions = options;
        }
    }

    public AVOptions getAVOptions() {
        return this.mAVOptions;
    }
}

