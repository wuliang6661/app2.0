package com.oliveapp.liveness.sample;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liveness.sample.liveness.SampleLivenessActivity;
import com.oliveapp.liveness.sample.liveness.SampleLivenessPortraitActivity;
import com.oliveapp.liveness.sample.utils.OliveappOrientationListener;
import com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper;

import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.ORIENTATION_TYPE_NAME;


public class SampleStartActivity extends Activity {
    private static final String TAG = SampleStartActivity.class.getSimpleName();

    private Button mStartLivenessButton;
    private ImageButton mCloseLivenessButton;
    private View mAnimCircleView;
    private RelativeLayout mAnimLayout;
    private OliveappOrientationListener mOliveappOrientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(decideWhichLayout());
        mOliveappOrientationListener = new OliveappOrientationListener(this);

        if (SampleScreenDisplayHelper.ifThisIsPhone(this)) { //手机默认竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mOliveappOrientationListener.enable();
        }

        initViews();
    }

    private void initViews() {
        mStartLivenessButton = (Button) findViewById(R.id.oliveappStartLivenessButton);
        mCloseLivenessButton = (ImageButton) findViewById(R.id.oliveappCloseLivenessButton);
        mStartLivenessButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        //判断手指抬起时，是否还在按钮范围内
                        float diffX = motionEvent.getRawX() - mStartLivenessButton.getX();
                        float diffY = motionEvent.getRawY() - mStartLivenessButton.getY();
//                        if ((diffX >= 0) && (diffX < mStartLivenessButton.getWidth()) && (diffY >= 0) && (diffY < mStartLivenessButton.getHeight())) {
                        startAnimation(motionEvent.getRawX(), motionEvent.getRawY(), mStartLivenessButton.getHeight() * 0.6f, 300);
                        enableStartButton(false);
//                        }
                        break;
                }
                return false;
            }
        });

        mCloseLivenessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        requestPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次进入，先设置button是否可点击
        enableStartButton(true);
    }

    //=======================================================设置控件====================================================//
    public void enableStartButton(boolean enable) {
        mStartLivenessButton.setEnabled(enable);
    }

    //========================================================请求权限===================================================//
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 101;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 102;
    private static final int PERMISSION_CAMERA = 103;
    private static final int PERMISSION_RECORD = 104;

    private boolean requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case PERMISSION_CAMERA: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mStartLivenessButton.callOnClick();
                    } else {
                        Toast.makeText(this, "没有摄像头权限我什么都做不了哦!", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case PERMISSION_READ_EXTERNAL_STORAGE: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mStartLivenessButton.callOnClick();
                    } else {
                        Toast.makeText(this, "请打开存储读写权限，确保APP正常运行", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case PERMISSION_WRITE_EXTERNAL_STORAGE: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mStartLivenessButton.callOnClick();
                    } else {
                        Toast.makeText(this, "请打开存储读写权限，确保APP正常运行", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Failed to request Permission", e);
        }
    }

    //==============================================决定采用的布局==============================================//
    private int decideWhichLayout() {
        int layout = -1;
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    layout = R.layout.oliveapp_sample_start_portrait_phone;
                } else {
                    layout = R.layout.oliveapp_sample_start_portrait_tablet;
                }
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    layout = R.layout.oliveapp_sample_start_landscape;
                } else {
                    layout = R.layout.oliveapp_sample_start_portrait_phone;
                }
                break;
        }
        return layout;
    }

    //================================================动画相关==============================================//

    /**
     * 点击“开始检测”按钮的动画调用
     *
     * @param endX     点击位置的距左边框的距离 单位是像素
     * @param endY     点击位置距上边框的距离 单位是像素
     * @param radius   需要显示的效果圆型的半径 单位是像素
     * @param duration 动画的持续时间，单位毫秒
     */
    public void startAnimation(float endX, float endY, float radius, long duration) {

        /**
         * 1,直接用画圆环的方法
         * 2,资源清理
         * 3,界面跳转
         */
        mAnimLayout = (RelativeLayout) findViewById(R.id.oliveapp_start_button_layout);
        mAnimCircleView = new CircleView(this, Color.YELLOW, endX, endY, radius, duration);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAnimLayout.addView(mAnimCircleView, lp);

    }

    /**
     * 动画结束之后的界面跳转
     */
    public void navToDetection() {

        if (requestPermission()) {
            Intent i;
            if (SampleScreenDisplayHelper.ifThisIsPhone(this)) { //手机默认选择竖屏的活检界面
                i = new Intent(SampleStartActivity.this, SampleLivenessPortraitActivity.class);
            } else {
                i = new Intent(SampleStartActivity.this, SampleLivenessActivity.class);
            }
            i.putExtra(ORIENTATION_TYPE_NAME, mOliveappOrientationListener.getOrientationType()); // 设置屏幕方向
            startActivity(i);
        }
    }

    /**
     * 清理动画用到的资源
     */
    public void sweepUp() {
        if (mAnimCircleView != null) {
            mAnimLayout.removeView(mAnimCircleView);
        }
    }

    /**
     * 具体负责点击动画的实现
     */
    class CircleView extends View {

        private Paint mPaint;
        private float mRadius;
        //圆心坐标
        private float x;
        private float y;
        private long mDuration;
        private float currentRadius = Float.MAX_VALUE;

        public CircleView(Context context, int color, float cx, float cy, float radius, long duration) {
            super(context);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeWidth(2);
            mPaint.setColor(color);
            x = cx;
            y = cy;
            mRadius = radius;
            mDuration = duration;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (currentRadius == Float.MAX_VALUE) {
                startAnimation();
            } else {
                RectF rect = new RectF();

                rect.top = y - (currentRadius + mRadius + 2) / 2;
                rect.left = x - (currentRadius + mRadius + 2) / 2;
                rect.bottom = y + (currentRadius + mRadius + 2) / 2;
                rect.right = x + (currentRadius + mRadius + 2) / 2;

                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(mRadius - currentRadius);
                canvas.drawArc(rect, 0, 360, false, mPaint);

            }

        }

        public void startAnimation() {
            ValueAnimator anim = ValueAnimator.ofFloat(0f, mRadius);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    currentRadius = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    sweepUp();
                    navToDetection();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            anim.setDuration(mDuration);
            anim.start();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int layout = -1;
        if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layout = R.layout.oliveapp_sample_start_landscape;
            } else {
                layout = R.layout.oliveapp_sample_start_portrait_tablet;
            }
            setContentView(layout);
            initViews();
        }
    }
}
