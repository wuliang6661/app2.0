package com.oliveapp.liveness.sample.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.util.Pair;
import android.util.StringBuilderPrinter;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.FacialActionType;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.OliveappFaceInfo;
import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liveness.sample.R;

import java.util.ArrayList;

/**
 * Created by cli on 1/10/17.
 * 管理活检过程的3个动画效果的实现
 */

public class OliveappAnimationHelper {

    public static final String TAG = OliveappAnimationHelper.class.getSimpleName();
    private Activity mContext;
    private ImageView mLeftBorder;
    private ImageView mRightBorder;
    private TextView mHintText;
    private ImageView mDetectFrameSurround;
    private PercentRelativeLayout mDetectLayout;
    private ImageView mLeftEyeImage;
    private ImageView mRightEyeImage;
    private ImageView mChinImage;
    private ImageView mChinUpImage;
    private ImageView mMouthCloseImage;
    private ImageView mMouthOpenImage;

    private final long MIN_MS = 250;
    private boolean mIsLandscape;

    public OliveappAnimationHelper(Activity context, boolean isLandscape) {
        this.mContext = context;
        this.mIsLandscape = isLandscape;
        init();
    }

    public void init() {
        mLeftBorder = (ImageView) mContext.findViewById(R.id.oliveapp_detected_hint_left_border);
        mRightBorder = (ImageView) mContext.findViewById(R.id.oliveapp_detected_hint_right_border);
        mHintText = (TextView) mContext.findViewById(R.id.oliveapp_detected_hint_text);
        mDetectFrameSurround = (ImageView) mContext.findViewById(R.id.oliveapp_start_frame);
        mDetectLayout = (PercentRelativeLayout) mContext.findViewById(R.id.oliveapp_detected_layout);
    }

    /**
     * 提示正对屏幕的动画
     * 只有在有预检的时候才会触发
     * 文字两边的黄色小边框和检测框的四角发生位移
     * 使用组合动画
     */
    public void playAperture() {

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator leftBorderAnim = ObjectAnimator.ofFloat(mLeftBorder, "translationX", 0f, -25f, 0f);
        ObjectAnimator rightBorderAnim = ObjectAnimator.ofFloat(mRightBorder, "translationX", 0f, 25f, 0f);
        ObjectAnimator detectFrameSurroundAnimX = ObjectAnimator.ofFloat(mDetectFrameSurround, "scaleX", 1, 1.2f, 1f);
        ObjectAnimator detectFrameSurroundAnimY = ObjectAnimator.ofFloat(mDetectFrameSurround, "scaleY", 1, 1.2f, 1f);

        animatorSet.setDuration(1000);
        animatorSet.playTogether(leftBorderAnim, rightBorderAnim, detectFrameSurroundAnimX, detectFrameSurroundAnimY);
        animatorSet.start();
    }

    /**
     * 提示语的动画
     */
    public void playHintTextAnimation(String hintText) {
        //前半段动画
        AnimatorSet formerBlinkAnimatorSet = new AnimatorSet();
        ObjectAnimator formerHintTextAnim = ObjectAnimator.ofFloat(mHintText, "scaleX", 1f, 0f);
        ObjectAnimator formerLeftBorderAnim = ObjectAnimator.ofFloat(mLeftBorder, "translationX", 0f, 100f);
        ObjectAnimator formerRightBorderAnim = ObjectAnimator.ofFloat(mRightBorder, "translationX", 0f, -100f);
        formerBlinkAnimatorSet.playTogether(formerHintTextAnim, formerLeftBorderAnim, formerRightBorderAnim);
        formerBlinkAnimatorSet.setDuration(250);
        formerBlinkAnimatorSet.start();
        final String finalHintText = hintText;
        formerBlinkAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mHintText.setText(finalHintText);
                //后半段动画
                AnimatorSet latterBlinkAnimatorSet = new AnimatorSet();
                ObjectAnimator latterHintTextAnim = ObjectAnimator.ofFloat(mHintText, "scaleX", 0f, 1f);
                ObjectAnimator latterLeftBorderAnim = ObjectAnimator.ofFloat(mLeftBorder, "translationX", 100f, 0f);
                ObjectAnimator latterRightBorderAnim = ObjectAnimator.ofFloat(mRightBorder, "translationX", -100f, 0f);
                latterBlinkAnimatorSet.playTogether(latterHintTextAnim, latterLeftBorderAnim, latterRightBorderAnim);
                latterBlinkAnimatorSet.setDuration(250);
                latterBlinkAnimatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void playActionAnimation(int actionType, ArrayList<Pair<Double, Double>> locationArray) {
        mDetectLayout.removeView(mMouthCloseImage);
        mDetectLayout.removeView(mMouthOpenImage);
        mDetectLayout.removeView(mLeftEyeImage);
        mDetectLayout.removeView(mRightEyeImage);
        mDetectLayout.removeView(mChinImage);
        mDetectLayout.removeView(mChinUpImage);
        switch (actionType) {
            case FacialActionType.EYE_CLOSE:{
                startEyeAnim(locationArray);
                break;
            }
            case FacialActionType.MOUTH_OPEN: {
                startMouthAnim(locationArray);
                break;
            }
            case FacialActionType.HEAD_UP: {
                startChinAnim(locationArray);
            }
        }
    }

    public void startMouthAnim(ArrayList<Pair<Double, Double>> locationArray) {
        Pair<Double, Double> mouthLocation = locationArray.get(0);

        if (mDetectLayout == null) {
            mDetectLayout = (PercentRelativeLayout) mContext.findViewById(R.id.oliveapp_detected_layout);
        }

        /**
         * 创建要显示的ImageView对象
         */
        mMouthCloseImage = new ImageView(mContext);
        mMouthOpenImage = new ImageView(mContext);
        mMouthCloseImage.setImageResource(R.mipmap.oliveapp_mouth_close);
        mMouthOpenImage.setImageResource(R.mipmap.oliveapp_mouth_open);
        mMouthCloseImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mMouthOpenImage.setScaleType(ImageView.ScaleType.FIT_XY);

        PercentRelativeLayout.LayoutParams detectFrameParams = (PercentRelativeLayout.LayoutParams) mDetectFrameSurround.getLayoutParams();
        float detectFrameWidthPercent = detectFrameParams.getPercentLayoutInfo().widthPercent;
        float detectFrameHeightPercent = detectFrameParams.getPercentLayoutInfo().heightPercent;
        float detectFrameLeftMarginPercent = detectFrameParams.getPercentLayoutInfo().leftMarginPercent;
        float detectFrameTopMarginPercent = detectFrameParams.getPercentLayoutInfo().topMarginPercent;

        /**
         * 布局闭嘴动画
         */
        PercentRelativeLayout.LayoutParams lpMouth = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        PercentLayoutHelper.PercentLayoutInfo mouthInfo= lpMouth.getPercentLayoutInfo();
        if (mIsLandscape) {
            mouthInfo.widthPercent = detectFrameWidthPercent / 10 ;
        } else {
            mouthInfo.widthPercent = detectFrameWidthPercent / 4 ;
        }
        mouthInfo.heightPercent = detectFrameHeightPercent / 8;
        if (mIsLandscape) {
            mouthInfo.leftMarginPercent = (1.f - (float) mouthLocation.first.doubleValue()) - 0.05f;
        } else {
            mouthInfo.leftMarginPercent = (1.f - (float) mouthLocation.first.doubleValue()) - 0.1f;
        }
        if (mIsLandscape) {
            mouthInfo.topMarginPercent = ((float) mouthLocation.second.doubleValue()) - 0.02f;
        } else {
            mouthInfo.topMarginPercent = ((float) mouthLocation.second.doubleValue());
        }

        mMouthCloseImage.requestLayout();
        mMouthOpenImage.requestLayout();
        mMouthCloseImage.setLayoutParams(lpMouth);
        mMouthOpenImage.setLayoutParams(lpMouth);
        mDetectLayout.addView(mMouthCloseImage);


        /**
         * 定义一个总的AnimatorSet
         */
        AnimatorSet mouthAnim = new AnimatorSet();
        /**
         * 闭嘴的动画
         */
        ObjectAnimator mouthCloseXAnimator = ObjectAnimator.ofFloat(mMouthCloseImage, "scaleX", 2.f, 1.0f);
        final ObjectAnimator mouchCloseYAnimator = ObjectAnimator.ofFloat(mMouthCloseImage, "scaleY", 2.f, 1.0f);
        ObjectAnimator mouthCloseAlphaAnimator = ObjectAnimator.ofFloat(mMouthCloseImage, "alpha", 0.f, 1.0f);
        AnimatorSet mouthCloseAnimation = new AnimatorSet();
        mouthCloseAnimation.setDuration(MIN_MS * 2);
        mouthCloseAnimation.playTogether(mouthCloseXAnimator, mouchCloseYAnimator, mouthCloseAlphaAnimator);
        mouthCloseAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mDetectLayout.removeView(mMouthCloseImage);
                mDetectLayout.addView(mMouthOpenImage);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        /**
         * 张嘴的动画
         */
        ObjectAnimator mouthOpenXAnimator = ObjectAnimator.ofFloat(mMouthOpenImage, "scaleX", 1.f, 2.0f);
        ObjectAnimator mouchOpenYAnimator = ObjectAnimator.ofFloat(mMouthOpenImage, "scaleY", 1.f, 2.0f);
        ObjectAnimator mouthOpenAlphaAnimator = ObjectAnimator.ofFloat(mMouthOpenImage, "alpha", 1.f, 0.f);
        AnimatorSet mouthOpenAnimation = new AnimatorSet();
        mouthOpenAnimation.setDuration(MIN_MS * 2);
        mouthOpenAnimation.playTogether(mouthOpenXAnimator, mouchOpenYAnimator, mouthOpenAlphaAnimator);

        mouthAnim.play(mouthCloseAnimation).before(mouthOpenAnimation);
        mouthAnim.start();
    }

    public void startChinAnim(ArrayList<Pair<Double, Double>> locationArray) {

        Pair<Double, Double> chinLocation = locationArray.get(0);

        if (mDetectLayout == null) {
            mDetectLayout = (PercentRelativeLayout) mContext.findViewById(R.id.oliveapp_detected_layout);
        }

        /**
         * 创建要显示的ImageView对象
         */
        mChinImage = new ImageView(mContext);
        mChinUpImage = new ImageView(mContext);
        mChinImage.setImageResource(R.mipmap.oliveapp_chin);
        mChinUpImage.setImageResource(R.mipmap.oliveapp_chin_up);
        mChinImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mChinUpImage.setScaleType(ImageView.ScaleType.FIT_XY);

        PercentRelativeLayout.LayoutParams detectFrameParams = (PercentRelativeLayout.LayoutParams) mDetectFrameSurround.getLayoutParams();
        float detectFrameWidthPercent = detectFrameParams.getPercentLayoutInfo().widthPercent;
        float detectFrameHeightPercent = detectFrameParams.getPercentLayoutInfo().heightPercent;
        float detectFrameLeftMarginPercent = detectFrameParams.getPercentLayoutInfo().leftMarginPercent;
        float detectFrameTopMarginPercent = detectFrameParams.getPercentLayoutInfo().topMarginPercent;

        /**
         * 布局下巴提示图片
         */
        PercentRelativeLayout.LayoutParams lpChin = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        PercentLayoutHelper.PercentLayoutInfo chinInfo= lpChin.getPercentLayoutInfo();
        if (mIsLandscape) {
            chinInfo.widthPercent = detectFrameWidthPercent / 3;
        } else {
            chinInfo.widthPercent = detectFrameWidthPercent / 4 * 3;
        }
        chinInfo.heightPercent = detectFrameHeightPercent / 2;
        lpChin.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.oliveapp_start_frame);
        lpChin.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mChinImage.requestLayout();

        /**
         * 布局箭头
         */
        PercentRelativeLayout.LayoutParams lpChinUp = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        PercentLayoutHelper.PercentLayoutInfo chinUpInfo= lpChinUp.getPercentLayoutInfo();
        if (mIsLandscape) {
            chinUpInfo.widthPercent = detectFrameWidthPercent / 14;
        } else {
            chinUpInfo.widthPercent = detectFrameWidthPercent / 7;
        }
        chinUpInfo.heightPercent = detectFrameHeightPercent / 5;
        chinUpInfo.topMarginPercent = detectFrameTopMarginPercent + detectFrameHeightPercent / 4 * 3;
        lpChinUp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mChinUpImage.requestLayout();

        /**
         * 设置布局参数, 添加到布局中
         */
        mChinImage.setLayoutParams(lpChin);
        mChinUpImage.setLayoutParams(lpChinUp);
        mDetectLayout.addView(mChinImage);
        mDetectLayout.addView(mChinUpImage);

        /**
         * 定义一个总的AnimatorSet
         */
        AnimatorSet headUpAnim = new AnimatorSet();
        /**
         * 缩放的动画
         */
        ObjectAnimator chinXAnimator = ObjectAnimator.ofFloat(mChinImage, "scaleX", 1.5f, 1.0f);
        ObjectAnimator chinYAnimator = ObjectAnimator.ofFloat(mChinImage, "scaleY", 1.5f, 1.0f);
        ObjectAnimator chinAlphaAnimator = ObjectAnimator.ofFloat(mChinImage, "alpha", 0.f, 1.0f);
        AnimatorSet chinAnimation = new AnimatorSet();
        chinAnimation.setDuration(MIN_MS * 2);
        chinAnimation.playTogether(chinXAnimator, chinYAnimator, chinAlphaAnimator);

        ObjectAnimator chinUpAnimator = ObjectAnimator.ofFloat(mChinUpImage, "alpha", 0.f, 1.f);
        chinUpAnimator.setDuration(MIN_MS * 2);
        chinUpAnimator.setRepeatCount(-1);
        chinUpAnimator.setRepeatMode(ValueAnimator.REVERSE);


        /**
         * 设置缩放的顺序
         */
        headUpAnim.play(chinAnimation).before(chinUpAnimator);
        headUpAnim.start();
    }
    /**
     * 检测到眼睛的定位点的动画
     * 眼睛部位有icon的位移
     * 1，创建ImageView对象，根据坐标赋值并添加到布局中
     * 2，创建动画
     */
    public void startEyeAnim(ArrayList<Pair<Double, Double>> locationArray) {
        /**
         * 拿到眼睛位置距离左和上的百分比
         */
        Pair<Double, Double> leftPair = locationArray.get(0);
        Pair<Double, Double> rightPair = locationArray.get(1);

        if (mDetectLayout == null) {
            mDetectLayout = (PercentRelativeLayout) mContext.findViewById(R.id.oliveapp_detected_layout);
        }

        /**
         * 创建要显示的ImageView对象
         */
        mLeftEyeImage = new ImageView(mContext);
        mRightEyeImage = new ImageView(mContext);
        mLeftEyeImage.setImageResource(R.mipmap.oliveapp_detect_eye_location);
        mRightEyeImage.setImageResource(R.mipmap.oliveapp_detect_eye_location);
        mLeftEyeImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mRightEyeImage.setScaleType(ImageView.ScaleType.FIT_XY);

        /**
         * 布局左眼
         */
        PercentRelativeLayout.LayoutParams lpLeft = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        PercentLayoutHelper.PercentLayoutInfo leftInfo= lpLeft.getPercentLayoutInfo();
        leftInfo.widthPercent = 0.15f;
        leftInfo.heightPercent = 0.1f;
        leftInfo.leftMarginPercent = (1.f - (float) leftPair.first.doubleValue()) - 0.1f;
        leftInfo.topMarginPercent =  ((float) leftPair.second.doubleValue()) - 0.05f ;
        mLeftEyeImage.requestLayout();

        /**
         * 布局右眼
         */
        PercentRelativeLayout.LayoutParams lpRight = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        PercentLayoutHelper.PercentLayoutInfo rightInfo= lpRight.getPercentLayoutInfo();
        rightInfo.widthPercent = 0.15f;
        rightInfo.heightPercent = 0.1f;
        rightInfo.leftMarginPercent = (1.f - (float) rightPair.first.doubleValue()) - 0.1f;
        rightInfo.topMarginPercent = ((float) rightPair.second.doubleValue()) - 0.05f;
        mRightEyeImage.requestLayout();

        /**
         * 设置布局参数, 添加到布局中
         */
        mLeftEyeImage.setLayoutParams(lpLeft);
        mRightEyeImage.setLayoutParams(lpRight);
        mDetectLayout.addView(mLeftEyeImage);
        mDetectLayout.addView(mRightEyeImage);

        /**
         * 定义一个总的AnimatorSet
         */
        AnimatorSet eyeAnim = new AnimatorSet();

        /**
         * 管理缩放的AnimatorSet
         */
        AnimatorSet scaleAnim = new AnimatorSet();

        /**
         * 管理透明度的AnimatorSet
         */
        AnimatorSet alphaAnim = new AnimatorSet();

        /**
         * 缩放动画的具体实现
         * 第一阶段：眼部图片的缩放
         */
        ObjectAnimator leftScaleAnimatorX1 = ObjectAnimator.ofFloat(mLeftEyeImage, "scaleX", 1f, 0.35f);
        ObjectAnimator leftScaleAnimatorY1 = ObjectAnimator.ofFloat(mLeftEyeImage, "scaleY", 1f, 0.35f);
        ObjectAnimator rightScaleAnimatorX1 = ObjectAnimator.ofFloat(mRightEyeImage, "scaleX", 1f, 0.35f);
        ObjectAnimator rightScaleAnimatorY1 = ObjectAnimator.ofFloat(mRightEyeImage, "scaleY", 1f, 0.35f);

        AnimatorSet scaleAnimatorSet1 = new AnimatorSet();
        scaleAnimatorSet1.setDuration(MIN_MS * 2);
        scaleAnimatorSet1.playTogether(leftScaleAnimatorX1, leftScaleAnimatorY1, rightScaleAnimatorX1, rightScaleAnimatorY1);


        /**
         * 第二阶段的缩放
         */
        ObjectAnimator leftScaleAnimatorX2 = ObjectAnimator.ofFloat(mLeftEyeImage, "scaleX", 0.35f, 0.6f);
        ObjectAnimator leftScaleAnimatorY2 = ObjectAnimator.ofFloat(mLeftEyeImage, "scaleY", 0.35f, 0.6f);
        ObjectAnimator rightScaleAnimatorX2 = ObjectAnimator.ofFloat(mRightEyeImage, "scaleX", 0.35f, 0.6f);
        ObjectAnimator rightScaleAnimatorY2 = ObjectAnimator.ofFloat(mRightEyeImage, "scaleY", 0.35f, 0.6f);

        AnimatorSet scaleAnimatorSet2 = new AnimatorSet();
        scaleAnimatorSet2.setDuration(MIN_MS * 2);
        scaleAnimatorSet2.playTogether(leftScaleAnimatorX2, leftScaleAnimatorY2, rightScaleAnimatorX2, rightScaleAnimatorY2);

        /**
         * 第三阶段的缩放
         */
        ObjectAnimator leftScaleAnimatorX3 = ObjectAnimator.ofFloat(mLeftEyeImage, "scaleX", 0.6f, 0.3f);
        ObjectAnimator leftScaleAnimatorY3 = ObjectAnimator.ofFloat(mLeftEyeImage, "scaleY", 0.6f, 0.3f);
        ObjectAnimator rightScaleAnimatorX3 = ObjectAnimator.ofFloat(mRightEyeImage, "scaleX", 0.6f, 0.3f);
        ObjectAnimator rightScaleAnimatorY3 = ObjectAnimator.ofFloat(mRightEyeImage, "scaleY", 0.6f, 0.3f);

        AnimatorSet scaleAnimatorSet3 = new AnimatorSet();
        scaleAnimatorSet3.setDuration(MIN_MS * 2);
        scaleAnimatorSet3.playTogether(leftScaleAnimatorX3, leftScaleAnimatorY3, rightScaleAnimatorX3, rightScaleAnimatorY3);

        /**
         * 设置缩放的顺序
         */
        scaleAnim.play(scaleAnimatorSet1).before(scaleAnimatorSet2).before(scaleAnimatorSet3);

        /**
         * 透明度动画具体实现
         * 第一阶段
         */
        ObjectAnimator leftAlphaAnimator1 = ObjectAnimator.ofFloat(mLeftEyeImage, "alpha", 0f, 1f);
        ObjectAnimator rightAlphaAnimator1 = ObjectAnimator.ofFloat(mRightEyeImage, "alpha", 0f, 1f);
        AnimatorSet alphaAnimatorSet1 = new AnimatorSet();
        alphaAnimatorSet1.setDuration(MIN_MS * 2);
        alphaAnimatorSet1.playTogether(leftAlphaAnimator1, rightAlphaAnimator1);

        /**
         * 第二阶段
         */
        ObjectAnimator leftAlphaAnimator2 = ObjectAnimator.ofFloat(mLeftEyeImage, "alpha", 1f, 0f);
        ObjectAnimator rightAlphaAnimator2 = ObjectAnimator.ofFloat(mRightEyeImage, "alpha", 1f, 0f);

        AnimatorSet alphaAnimatorSet2 = new AnimatorSet();
        alphaAnimatorSet2.setDuration(MIN_MS * 2);
        alphaAnimatorSet2.playTogether(leftAlphaAnimator2, rightAlphaAnimator2);

        /**
         * 设置透明度变化顺序
         */
        alphaAnim.play(alphaAnimatorSet2).after(MIN_MS * 2).after(alphaAnimatorSet1);

        /**
         * 开启动画
         */
        eyeAnim.playTogether(scaleAnim, alphaAnim);
        eyeAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                sweepUp();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        eyeAnim.start();

    }

    /**
     * 清理动画相关的资源
     */
    public void sweepUp() {
        if (mDetectLayout != null) {
            if (mLeftEyeImage != null) {
                mDetectLayout.removeView(mLeftEyeImage);
                mLeftEyeImage = null;
            }
            if (mRightEyeImage != null) {
                mDetectLayout.removeView(mRightEyeImage);
                mRightEyeImage = null;
            }
        }
    }
}
