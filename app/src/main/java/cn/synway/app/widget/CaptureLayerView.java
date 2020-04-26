package cn.synway.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.ScreenUtils;


public class CaptureLayerView extends View {

    private Bitmap bitmap;
    private Canvas cnvs;
    private Paint p = new Paint();
    private Paint transparentPaint = new Paint();
    private Paint semiTransparentPaint = new Paint();
    private int parentWidth;
    private int parentHeight;
    private int radius;

    public CaptureLayerView(Context context) {
        super(context);
        init();
    }

    public CaptureLayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        radius = ScreenUtils.getScreenWidth() / 3;

        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        transparentPaint.setAntiAlias(true);

        semiTransparentPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        bitmap = Bitmap.createBitmap(parentWidth, parentHeight, Bitmap.Config.ARGB_8888);
        cnvs = new Canvas(bitmap);

        cnvs.drawRect(0, 0, cnvs.getWidth(), cnvs.getHeight(), semiTransparentPaint);
        cnvs.drawCircle(parentWidth / 2, parentHeight / 2 - parentHeight / 6, radius, transparentPaint);
        canvas.drawBitmap(bitmap, 0, 0, p);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
