package com.oliveapp.liveness.sample.uicomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.oliveapp.liveness.sample.R;
import com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper;

public class FixedAspectLayout extends FrameLayout {

    private float mHWAspect = 1.0f;
    private final static int FIX_HEIGHT = 1;
    private final static int FIX_WIDTH = 2;
    private int mFixMode = 1;

    // .. alternative constructors omitted

    public FixedAspectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @SuppressWarnings("ResourceType")
    private void init(Context context, AttributeSet attrs) {
        int aspectRatioId = R.attr.oliveapp_aspectRatio;
        int fixModeId = R.attr.oliveapp_fixMode;

        int attrArray[] = {aspectRatioId, fixModeId};
        TypedArray array = context.obtainStyledAttributes(attrs, attrArray);
        /**
         * 改用动态获取比例来适应不同屏幕
         */
        mHWAspect = (float) SampleScreenDisplayHelper.getScreenScale(context);
        mFixMode = array.getInt(1, 1); // First Parameter represents FixMode, cannot get styleableId dynamically

        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        if (w == 0) {
            h = 0;
        } else {
            switch (mFixMode) {
                case FIX_HEIGHT:
                    w = (int) (h / mHWAspect);
                    break;
                case FIX_WIDTH:
                    h = (int) (w * mHWAspect);
                    break;
                default:
                    w = (int) (h / mHWAspect);
            }
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(w,
                        MeasureSpec.getMode(widthMeasureSpec)),
                MeasureSpec.makeMeasureSpec(h,
                        MeasureSpec.getMode(heightMeasureSpec)));
    }

}
