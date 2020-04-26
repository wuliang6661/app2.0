package com.oliveapp.liveness.sample.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liveness.sample.R;

import java.lang.reflect.Field;

/**
 * Created by jthao on 1/13/16.
 */
public class AudioModule {
    private static final String TAG = AudioModule.class.getSimpleName();

    private MediaPlayer mAudioPlayer = new MediaPlayer();
    public void playAudio(Context context, String audioResourceName)
    {
        int rawId = getResId(audioResourceName, R.raw.class);
        try {
            if (mAudioPlayer != null) {
                if (mAudioPlayer.isPlaying()) {
                    mAudioPlayer.stop();
                }
                mAudioPlayer.reset();
                mAudioPlayer = MediaPlayer.create(context, rawId);
                mAudioPlayer.start();
            } else {
                LogUtil.e(TAG, "mAudioPlayer is null, fail to play audio.");
            }
        } catch (IllegalStateException e) {
            LogUtil.e(TAG, "fail to play audio type: ", e);
        }
    }

    public boolean isPlaying() {
        if (mAudioPlayer != null) {
            return mAudioPlayer.isPlaying();
        }
        return false;
    }

    public void release() {
        try {
            mAudioPlayer.stop();
            mAudioPlayer.release();
            mAudioPlayer = null;
        } catch (Exception e) {
            LogUtil.e(TAG, "Fail to release", e);
        }
    }

    private int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            LogUtil.e(TAG, "Fail to getResId", e);
            return -1;
        }
    }
}
