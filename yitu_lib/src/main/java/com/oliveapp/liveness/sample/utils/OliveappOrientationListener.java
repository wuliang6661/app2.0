package com.oliveapp.liveness.sample.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.OrientationEventListener;

import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.OrientationType.LANDSCAPE;
import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.OrientationType.LANDSCAPE_REVERSE;
import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.OrientationType.PORTRAIT;
import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.OrientationType.PORTRAIT_REVERSE;

public class OliveappOrientationListener extends OrientationEventListener {
    private Activity mContext;
    private SampleScreenDisplayHelper.OrientationType mOrientationType = PORTRAIT;

    public OliveappOrientationListener(Activity context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        int screenOrientation = mContext.getResources().getConfiguration().orientation;
        if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) { //设置竖屏
            if (screenOrientation != Configuration.ORIENTATION_PORTRAIT) {
                mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mOrientationType = PORTRAIT;
            }
        } else if (!SampleScreenDisplayHelper.ifThisIsPhone(mContext)
                && orientation > 225 && orientation < 315) { //只有pad支持设置横屏
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mOrientationType = LANDSCAPE;
        } else if (!SampleScreenDisplayHelper.ifThisIsPhone(mContext)
                && orientation > 45 && orientation < 135) { //只有pad支持设置反向横屏
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            mOrientationType = LANDSCAPE_REVERSE;
        } else if (orientation > 135 && orientation < 225) {
            if (screenOrientation != Configuration.ORIENTATION_PORTRAIT) {
                mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                mOrientationType = PORTRAIT_REVERSE;
            }
        }
    }

    /**
     * 返回当前屏幕方向
     */
    public SampleScreenDisplayHelper.OrientationType getOrientationType() {
        return mOrientationType;
    }
}