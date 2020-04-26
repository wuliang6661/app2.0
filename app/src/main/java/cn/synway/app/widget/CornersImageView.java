package cn.synway.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 仅仅裁剪画布，布对图片进行处理
 */
@SuppressLint("AppCompatCustomView")
public class CornersImageView extends ImageView {
    private float corner;

    public CornersImageView(Context context) {
        super(context);
    }

    public CornersImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CornersImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCorners(int dp) {
        this.corner = Resources.getSystem().getDisplayMetrics().density * dp;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (corner > 0) {
            Path path = new Path();
            path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), corner, corner, Path.Direction.CW);
            canvas.clipPath(path);//设置可显示的区域，canvas四个角会被剪裁掉】
        }
        super.onDraw(canvas);
    }
}