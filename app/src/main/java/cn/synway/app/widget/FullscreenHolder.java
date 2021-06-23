package cn.synway.app.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2021/6/2314:34
 * desc   :
 * version: 1.0
 */
public class FullscreenHolder extends FrameLayout {

    public FullscreenHolder(Context ctx) {
        super(ctx);
        setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        return true;
    }
}