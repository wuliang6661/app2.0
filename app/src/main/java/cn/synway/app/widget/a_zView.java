package cn.synway.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;

import synway.cameraNew.editimage.utils.DensityUtil;


public class a_zView extends View {
    // 26个字母
    public static String[] A_Z = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    private Paint mPaint;
    private MoveListener mMoveListener;
    private int letterIndex = -1;
    private Context context;

    public a_zView(Context context) {
        super(context);
        init(context);
    }

    public a_zView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        mPaint = new Paint();
        mPaint.setColor(Color.rgb(100, 154, 255));  //设置字体颜色
        mPaint.setTypeface(Typeface.DEFAULT);  //设置字体
        mPaint.setAntiAlias(true);  //设置抗锯齿
        mPaint.setTextSize(DensityUtil.dip2px(context, 12));  //设置字母字体大小
    }

    public a_zView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        float singleHeight = getHeight() / (Float.valueOf(A_Z.length + ""));
        for (int i = 0; i < A_Z.length; i++) {
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - mPaint.measureText(A_Z[i]) / 2;
            float yPos = singleHeight * i + singleHeight;

            canvas.drawText(A_Z[i], xPos, yPos, mPaint);  //绘制所有的字母
            //  canvas.drawLine(0, yPos, width, yPos, mPaint);

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int c2 = 0;
        float y = 0F;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                LogUtils.e("ACTION_MOVE");
                y = event.getY();
                c2 = (int) (y / getHeight() * A_Z.length);
                if (c2 != letterIndex && c2 < A_Z.length && c2 >= 0) {
                    letterIndex = c2;

                    if (mMoveListener != null) {
                        mMoveListener.onLetter(A_Z[letterIndex]);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mMoveListener != null) {
                    mMoveListener.hiddeLetter();
                    letterIndex = -1;
                }
                break;

        }

        return true;
    }

    public void addOnMoveListener(MoveListener lister) {
        this.mMoveListener = lister;
    }

    public interface MoveListener {
        void onLetter(String letter);


        void hiddeLetter();
    }
}
