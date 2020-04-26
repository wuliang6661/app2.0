package com.oliveapp.liveness.sample.liveness.view_controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oliveapp.camerasdk.CameraManager;
import com.oliveapp.camerasdk.MediaRecordConfig;
import com.oliveapp.camerasdk.PhotoModule;
import com.oliveapp.camerasdk.utils.CameraUtil;
import com.oliveapp.face.livenessdetectionviewsdk.event_interface.ViewUpdateEventHandlerIf;
import com.oliveapp.face.livenessdetectionviewsdk.verification_controller.VerificationController;
import com.oliveapp.face.livenessdetectionviewsdk.verification_controller.VerificationControllerFactory;
import com.oliveapp.face.livenessdetectionviewsdk.verification_controller.VerificationControllerIf;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.IIndependentLivenessStatusListener;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.FacialActionType;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.ImageProcessParameter;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.LivenessDetectionFrames;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.LivenessDetectorConfig;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.OliveappFaceInfo;
import com.oliveapp.face.livenessdetectorsdk.prestartvalidator.datatype.PrestartDetectionFrame;
import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liveness.sample.R;
import com.oliveapp.liveness.sample.SampleUnusualResultActivity;
import com.oliveapp.liveness.sample.utils.AudioModule;
import com.oliveapp.liveness.sample.utils.OliveappAnimationHelper;
import com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper;

import java.util.ArrayList;
import java.util.Arrays;

import static com.oliveapp.camerasdk.utils.CameraUtil.CAMERA_RATIO_16_9;
import static com.oliveapp.camerasdk.utils.CameraUtil.broadcastNewPicture;
import static com.oliveapp.libcommon.utility.LogUtil.ENABLE_LOG;
import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.ORIENTATION_TYPE_NAME;
import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.OrientationType.LANDSCAPE;
import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.OrientationType.LANDSCAPE_REVERSE;
import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.OrientationType.PORTRAIT;
import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.OrientationType.PORTRAIT_REVERSE;

/**
 * ViewController 实现了主要的界面逻辑
 * 如果需要定义界面，请继承此类编写自己的Activity，并自己实现事件响应函数
 * 可参考SampleAPP里的ExampleLivenessActivity
 */

public abstract class LivenessDetectionMainActivity extends Activity implements ViewUpdateEventHandlerIf,
        IIndependentLivenessStatusListener, CameraManager.MediaRecordCallback {

    public static final String TAG = LivenessDetectionMainActivity.class.getSimpleName();


    /**
     * 以下4个变量定义了界面中人脸框的位置，采用百分比形式，要根据UI和屏幕比例动态调整，可参考下面代码。
     * 比如一张宽高为900 * 1600的图片，以下变量定义的位置为
     * 左上角(x, y) => (900 * 0.15, 1600 * 0.25) = (135, 400)
     * 人脸框的宽和高(width,height) => (900 * 0.7, 1600 * 0.5) = （630,800)
     */
    private static float mXPercent = 0.271f;
    private static float mYPercent = 0.274f;
    private static float mWidthPercent = 0.735f;
    private static float mHeightPercent = 0.414f;

    private PhotoModule mPhotoModule; // 摄像头模块
    private AudioModule mAudioModule; // 音频播放模块

    private TextView mOliveappDetectedDirectText; //头像上方的指示图像“请眨眼”

    private ImageButton mCloseImageBtn; //关闭按钮
    private ImageView mCapFrame;
    private TextView mCountDownTextView;
    private MediaRecordConfig mMediaRecordConfig;
    private boolean mIsLivenessSuccess = false;
    private LivenessOrientationListener mLivenessOrientationListener;
    private SampleScreenDisplayHelper.OrientationType mOrientationType;
    private boolean mIsLandscape = false;

    /**
     * 默认无预检动作
     * WITHOUT_PRESTART,无预检过程
     * WITH_PRESTART,有预检过程 (推荐)
     */
    private VerificationControllerFactory.VCType mVerificationControllerType = VerificationControllerFactory.VCType.WITH_PRESTART;

    private OliveappAnimationHelper mAnimController; //控制动画播放
    private static Handler mAnimationHanlder = null; //播放动画的Handler
    private ArrayList<Pair<Double, Double>> mLocationArray; //五官位置
    private int mCurrentActionType;
    private boolean mIsPrestart = false;

    /**
     * ====================================生命周期相关函数====================================
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ENABLE_LOG = true;
        LogUtil.MIN_LOG_LEVEL = 0;
        LogUtil.i(TAG, "[BEGIN] LivenessDetectionMainActivity::onCreate()");
        increaseClassObjectCount();
        super.onCreate(savedInstanceState);
        mLivenessOrientationListener = new LivenessOrientationListener(this);
        mLivenessOrientationListener.enable();

        /**
         *  以下代码是为了配合设置功能，集成时可以删除
         */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(LivenessDetectionMainActivity.this);
        boolean isDebug = sharedPrefs.getBoolean("pref_debug_mode", false);
        if (isDebug) {
            if (sharedPrefs.getBoolean("pref_with_prestart", false)) {
                mVerificationControllerType = VerificationControllerFactory.VCType.WITH_PRESTART;
            } else {
                mVerificationControllerType = VerificationControllerFactory.VCType.WITHOUT_PRESTART;
            }
        }
        /**
         *  以上代码是为了配合设置功能，集成时可以删除
         */

        // 初始化界面元素
        initViews();
        // 初始化摄像头
        initCamera();
        // 初始化检测逻辑控制器(VerificationController)
        initControllers();

        /**
         * 如果有预检的话播放预检相关动画和音频
         */
        if (mVerificationControllerType == VerificationControllerFactory.VCType.WITH_PRESTART) {
            mAnimationHanlder.post(mPreHintAnimation);
            mIsPrestart = true;
        } else {
            mIsPrestart = false;
        }

        LogUtil.i(TAG, "[END] LivenessDetectionMainActivity::onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "[BEGIN] LivenessDetectionMainActivity::onResume()");
        super.onResume();
        if (mPhotoModule != null) {
            mPhotoModule.onResume();
            // 设置摄像头回调，自此之后VerificationController.onPreviewFrame函数就会源源不断的收到摄像头的数据
        }
        try {
            mPhotoModule.setPreviewDataCallback(mVerificationController, mCameraHandler);
            mPhotoModule.setMediaRecordCallback(this, mMediaRecordConfig);
        } catch (NullPointerException e) {
            LogUtil.e(TAG, "PhotoModule set callback failed", e);
        }

        if (mAnimationHanlder != null) {
            if (mIsPrestart) {
                mAnimationHanlder.post(mPreHintAnimation);
            } else {
                mAnimationHanlder.post(mActionAnimationTask);
            }
        }
        LogUtil.i(TAG, "[END] LivenessDetectionMainActivity::onResume()");
    }

    @Override
    protected void onPause() {
        LogUtil.i(TAG, "[BEGIN] LivenessDetectionMainActivity::onPause()");
        super.onPause();

        if (mPhotoModule != null) {
            mPhotoModule.onPause();
        }

        if (mAnimationHanlder != null) {
            mAnimationHanlder.removeCallbacksAndMessages(null);
        }
        LogUtil.i(TAG, "[END] LivenessDetectionMainActivity::onPause()");
    }

    @Override
    protected void onStop() {
        LogUtil.i(TAG, "[BEGIN] LivenessDetectionMainActivity::onStop()");
        super.onStop();
        LogUtil.i(TAG, "[END] LivenessDetectionMainActivity::onStop()");
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "[BEGIN] LivenessDetectionMainActivity::onDestroy()");
        super.onDestroy();
        // 关闭摄像头
        if (mPhotoModule != null)
            mPhotoModule.onStop();
        CameraUtil.sContext = null;
        mPhotoModule = null;

        // 关闭音频播放
        if (mAnimationHanlder != null) {
            mAnimationHanlder.removeCallbacksAndMessages(null);
            mAnimationHanlder = null;
        }
        if (mAudioModule != null) {
            mAudioModule.release();
            mAudioModule = null;
        }

        // 退出摄像头处理线程
        if (mCameraHandlerThread != null) {
            try {
                mCameraHandlerThread.quit();
                mCameraHandlerThread.join();
            } catch (InterruptedException e) {
                LogUtil.e(TAG, "Fail to join CameraHandlerThread", e);
            }
        }
        mCameraHandlerThread = null;

        // 销毁检测逻辑控制器
        if (mVerificationController != null) {
            mVerificationController.uninit();
            mVerificationController = null;
        }
        if (mLivenessOrientationListener != null) {
            mLivenessOrientationListener.disable();
        }
        LogUtil.i(TAG, "[END] LivenessDetectionMainActivity::onDestroy()");
    }

    /**
     * ====================================初始化相关函数====================================
     **/

    private VerificationControllerIf mVerificationController; // 逻辑控制器
    private Handler mCameraHandler; // 摄像头回调所在的消息队列
    private HandlerThread mCameraHandlerThread; // 摄像头回调所在的消息队列线程

    /**
     * 初始化并打开摄像头
     */
    private void initCamera() {
        LogUtil.i(TAG, "[BEGIN] initCamera");

        // 寻找设备上的前置摄像头
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int expectCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);

            LogUtil.i(TAG, "camera id: " + camIdx + ", facing: " + cameraInfo.facing + ", expect facing: " + expectCameraFacing);
            if (cameraInfo.facing == expectCameraFacing) {
                getIntent().putExtra(CameraUtil.EXTRAS_CAMERA_FACING, camIdx); // 设置需要打开的摄像头ID
                getIntent().putExtra(CameraUtil.TARGET_PREVIEW_RATIO, CAMERA_RATIO_16_9); // 设置Preview长宽比,默认是16:9
            }
        }
        mPhotoModule = new PhotoModule();
        mPhotoModule.init(this, findViewById(R.id.oliveapp_cameraPreviewView)); // 参考layout XML文件里定义的cameraPreviewView对象
        mPhotoModule.setPlaneMode(false, false); // 取消拍照和对焦功能
        // 打开摄像头预览
        mPhotoModule.onStart();
        // 初始化摄像头处理消息队列
        mCameraHandlerThread = new HandlerThread("CameraHandlerThread");
        mCameraHandlerThread.start();
        mCameraHandler = new Handler(mCameraHandlerThread.getLooper());

        LogUtil.i(TAG, "[END] initCamera");
    }

    /**
     * 初始化界面元素
     */
    private void initViews() {

        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(decideWhichLayout());

        //音频和动画的Handler
        mAudioModule = new AudioModule();
        mAnimationHanlder = new Handler();
        // DEBUG: 调试相关
        mFrameRateText = (TextView) findViewById(R.id.oliveapp_frame_rate_text);
        mOliveappDetectedDirectText = (TextView) findViewById(R.id.oliveapp_detected_hint_text);
        mOliveappDetectedDirectText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mOliveappDetectedDirectText.getPaint().setFakeBoldText(true);
        // 倒计时的TextView
        mCountDownTextView = (TextView) findViewById(R.id.oliveapp_count_time_textview);
        //设置关闭按钮
        mCloseImageBtn = (ImageButton) findViewById(R.id.oliveapp_close_image_button);
        mCloseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /**
         * 设置脸部捕捉框的位置，请根据自己的UI进行调整
         * 因为手机和平板的屏幕比例不同，并且适配不同比例的背景图也不同导致需要相应的适配
         */
        mCapFrame = (ImageView) findViewById(R.id.oliveapp_start_frame);
        if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
            PercentRelativeLayout.LayoutParams layoutParams = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            PercentLayoutHelper.PercentLayoutInfo layoutInfo = layoutParams.getPercentLayoutInfo();
            if (mOrientationType == LANDSCAPE || mOrientationType == LANDSCAPE_REVERSE) {
                layoutInfo.topMarginPercent = 0.2f;
                layoutInfo.heightPercent = 0.6f;
                layoutInfo.widthPercent = layoutInfo.heightPercent / (float) SampleScreenDisplayHelper.getScreenScale(this);
                layoutInfo.leftMarginPercent = (1 - layoutInfo.widthPercent) / 2;
                mIsLandscape = true;
            } else {
                layoutInfo.leftMarginPercent = 0.13f;
                layoutInfo.widthPercent = 0.74f;
                layoutInfo.heightPercent = layoutInfo.widthPercent / (float) SampleScreenDisplayHelper.getScreenScale(this);
                layoutInfo.topMarginPercent = (1 - layoutInfo.heightPercent) / 2 - 0.022f;
                mIsLandscape = false;
            }
            mCapFrame.setLayoutParams(layoutParams);
        }
        /**
         * UI相关
         * 提示文字的布局要动态调整
         * 只有平板竖屏的时候要调整
         */
        if ((mOrientationType == PORTRAIT || mOrientationType == PORTRAIT_REVERSE)
                && (!SampleScreenDisplayHelper.ifThisIsPhone(this))) {
            PercentRelativeLayout hintLayout = (PercentRelativeLayout) findViewById(R.id.oliveapp_detected_hint_text_layout);
            PercentRelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(0, 0);
            PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
            info.widthPercent = 1f;
            info.heightPercent = 0.052f;
            info.topMarginPercent = mYPercent - info.heightPercent;
            info.leftMarginPercent = 0;
            hintLayout.setLayoutParams(params);
        }
        //进入活检页面就开启动画
        mAnimController = new OliveappAnimationHelper(this, mIsLandscape);
    }

    /**
     * 重启活体检测，可以多次调用
     */
    private void restartDetection() {
        // 隐藏动画界面上的眼睛/下巴/嘴
        mCurrentActionType = FacialActionType.UNKNOWN;
        mAnimController.playActionAnimation(mCurrentActionType, mLocationArray);

        // 释放音频动画资源
        releaseAudioAnimination();

        // 清空倒计时
        mCountDownTextView.setText("");

        // 人脸框上方黄色提示语隐藏
        mOliveappDetectedDirectText.setText(getString(R.string.oliveapp_step_hint_focus));

        // 注销
        mVerificationController.uninit();

        // 重新初始化音频动画
        mAudioModule = new AudioModule();
        mAnimationHanlder = new Handler();

        // 初始化
        initControllers();

        // 是否需要预检
        if (mVerificationControllerType == VerificationControllerFactory.VCType.WITH_PRESTART) {
            mAnimationHanlder.post(mPreHintAnimation);
            mIsPrestart = true;
        }

        // 预检callback
        try {
            mPhotoModule.setPreviewDataCallback(mVerificationController, mCameraHandler);
        } catch (NullPointerException e) {
            LogUtil.e(TAG, "PhotoModule set callback failed", e);
        }

        // 播放预检动画
        if (mAnimationHanlder != null) {
            if (mIsPrestart) {
                mAnimationHanlder.post(mPreHintAnimation);
            } else {
                mAnimationHanlder.post(mActionAnimationTask);
            }
        }
    }

    // 图片预处理参数
    private ImageProcessParameter mImageProcessParameter;
    // 活体检测参数
    private LivenessDetectorConfig mLivenessDetectorConfig;

    /**
     * 设置图片处理参数和活体检测参数
     */
    private void setDetectionParameter() throws Exception {
        /**
         * 注意: 默认参数适合手机，一般情况下不需要修改这些参数。如需修改请联系依图工程师
         *
         * 设置从preview图片中截取人脸框的位置，调用doDetection前必须调用本函数。
         * @param shouldFlip 是否左右翻转。一般前置摄像头为false
         * @param cropWidthPercent　截取的人脸框宽度占帧宽度的比例
         * @param verticalOffsetPercent　截取的人脸框上边缘到帧上边缘的距离占帧高度的比例
         * @param preRotationDegree　逆时针旋转角度，只允许0 90 180 270，大部分手机应当是90
         */
        mImageProcessParameter = new ImageProcessParameter(false, 1.0f, 0.0f, 90);

        // 使用预设配置: 满足绝大多数常见场景
        mLivenessDetectorConfig = new LivenessDetectorConfig();
        mLivenessDetectorConfig.usePredefinedConfig(0);
        mMediaRecordConfig = new MediaRecordConfig();
        /**
         * 注意，以下代码是配合SampleApp的设置工程，请在集成时请删除
         */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDebug = sharedPrefs.getBoolean("pref_debug_mode", false);
        // free style mLivenessDetectorConfig, 自定义活检的config
        if (isDebug) {
            int actionOne = Integer.valueOf(sharedPrefs.getString("pref_action_one_list", "3"));
            int actionTwo = Integer.valueOf(sharedPrefs.getString("pref_action_two_list", "3"));
            int actionThree = Integer.valueOf(sharedPrefs.getString("pref_action_three_list", "3"));
            int actionFour = Integer.valueOf(sharedPrefs.getString("pref_action_four_list", "3"));
            mLivenessDetectorConfig.totalActions = Integer.valueOf(sharedPrefs.getString("pref_action_counts_list", "3"));
            mLivenessDetectorConfig.minPass = mLivenessDetectorConfig.totalActions;
            mLivenessDetectorConfig.maxFail = 0;
            mLivenessDetectorConfig.timeoutMs = Integer.valueOf(sharedPrefs.getString("pref_liveness_detection_overtime_list", "10000"));
            mLivenessDetectorConfig.fanapaiClsImageNumber = Integer.valueOf(sharedPrefs.getString("pref_fanpaicls_counts_list", "10000"));
            mLivenessDetectorConfig.fixedActions = sharedPrefs.getBoolean("pref_fix_action", false);
            mLivenessDetectorConfig.fixedActionList = Arrays.asList(actionOne, actionTwo, actionThree, actionFour);
            mLivenessDetectorConfig.saveRgb = sharedPrefs.getBoolean("pref_save_rgb", false);
            mLivenessDetectorConfig.saveOriginImage = sharedPrefs.getBoolean("pref_save_origin_image", false);
            mLivenessDetectorConfig.savePackage = sharedPrefs.getBoolean("pref_save_package", false);
            mLivenessDetectorConfig.saveJPEG = sharedPrefs.getBoolean("pref_jpeg_image", false);
            mLivenessDetectorConfig.saveFanpaiCls = sharedPrefs.getBoolean("pref_fanpaicls_image", false);
            mLivenessDetectorConfig.newPackage = sharedPrefs.getBoolean("pref_new_package", true);
            //此处默认值为0, 代表无昏暗检测

            mLivenessDetectorConfig.darkLevel = getDarkLevelFromDesc(sharedPrefs.getString("pref_dark_detect_list", "0"));

            //视频录制相关配置
            mMediaRecordConfig.isEnable = sharedPrefs.getBoolean("pref_video", false);
            mMediaRecordConfig.videoPath = Environment.getExternalStorageDirectory().getPath() + "/video";
        }
        LogUtil.d(TAG, "action list fixedActionList" + mLivenessDetectorConfig.fixedActionList.toString());
        LogUtil.d(TAG, "action list candidateActionList " + mLivenessDetectorConfig.candidateActionList.toString());
        /** 注意，以上代码集成时请删除 **/

        if (mLivenessDetectorConfig != null) {
            mLivenessDetectorConfig.validate();
        }

        //如果超时时间太长的话便不显示倒计时
        if (mLivenessDetectorConfig.timeoutMs >= 1000000) {
            mCountDownTextView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 初始化检测逻辑控制器
     * 请先调用setDetectionParameter()设置参数
     */
    private void initControllers() {
        try {
            setDetectionParameter();
        } catch (Exception e) {
            LogUtil.e(TAG, "初始化参数失败", e);
        }

        //这里设置字段,用于防重放攻击
        mLivenessDetectorConfig.setUserDefinedContent("user_defined_content");
        //初始化算法模块
        mVerificationController = VerificationControllerFactory.createVerificationController(mVerificationControllerType, LivenessDetectionMainActivity.this,
                mImageProcessParameter,
                mLivenessDetectorConfig,
                LivenessDetectionMainActivity.this,
                new Handler(Looper.getMainLooper()));
        //设置人脸框位置
        mVerificationController.SetFaceLocation(mXPercent, mYPercent, mWidthPercent, mHeightPercent);

    }

    /**===========================算法相关函数==============================**/

    /**
     * 调用此函数后活体检测即开始
     */
    public void startVerification() {
        try {
            if (mVerificationController.getCurrentStep() == VerificationController.STEP_READY) {
                mVerificationController.nextVerificationStep();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "无法开始活体检测...", e);
        }
    }

    /** ============================== 算法相关的回调函数 ============================**/

    /**
     * 预检阶段成功
     * 请在里面调用mVerificationController.enterLivenessDetection()进入活体检测阶段
     */
    @Override
    public void onPrestartSuccess(LivenessDetectionFrames livenessDetectionFrames, OliveappFaceInfo faceInfo) {
        LogUtil.i(TAG, "[BEGIN] onPrestartSuccess");
        mAnimationHanlder.removeCallbacks(mPreHintAnimation);
        mIsPrestart = false;
        /**
         * 调用此函数进入活体检测过程
         */
        mVerificationController.enterLivenessDetection();
        LogUtil.i(TAG, "[END] onPrestartSuccess");
    }


    /**
     * 预检阶段每一帧的回调函数
     *
     * @param frame                    每一帧
     * @param remainingTimeMillisecond 剩余时间
     * @param faceInfo                 人脸信息
     * @param errorCodeOfInAction      动作不过关的可能原因，可以用来做提示语
     */
    @Override
    public void onPrestartFrameDetected(PrestartDetectionFrame frame, int remainingTimeMillisecond, OliveappFaceInfo faceInfo, ArrayList<Integer> errorCodeOfInAction) {
        mFrameRate += 1;
        long currentTimestamp = System.currentTimeMillis();
        if ((currentTimestamp - mLastTimestamp) > 1000) {
            mLastTimestamp = currentTimestamp;
            mFrameRateText.setText("FrameRate: " + mFrameRate + " FPS");
            mFrameRate = 0;
        }
    }

    /**
     * 预检阶段失败，不可能进入此回调
     */
    @Override
    public void onPrestartFail(int result) {
        LogUtil.wtf(TAG, "[END] onPrestartFail");

        Intent intent = new Intent(LivenessDetectionMainActivity.this, SampleUnusualResultActivity.class);
        intent.putExtra(SampleUnusualResultActivity.keyToGetExtra, SampleUnusualResultActivity.PRESTART_FAIL);
        intent.putExtra(ORIENTATION_TYPE_NAME, mOrientationType); // 设置屏幕方向
        startActivity(intent);
    }

    /**
     * 活体检测成功的回调
     *
     * @param livenessDetectionFrames 活体检测抓取的图片
     * @param faceInfo                捕获到的人脸信息
     */
    @Override
    public void onLivenessSuccess(LivenessDetectionFrames livenessDetectionFrames, OliveappFaceInfo faceInfo) {
        releaseAudioAnimination();
        mIsLivenessSuccess = true;
    }

    // 关闭音频&动画
    private void releaseAudioAnimination() {
        if (mAnimationHanlder != null) {
            mAnimationHanlder.removeCallbacksAndMessages(null);
            mAnimationHanlder = null;
        }
        if (mAudioModule != null) {
            mAudioModule.release();
            mAudioModule = null;
        }
    }

    /**
     * 活检阶段失败
     */
    @Override
    public void onLivenessFail(int result, LivenessDetectionFrames livenessDetectionFrames) {

    }

    /**
     * 每一帧结果的回调方法
     *
     * @param currentActionType           当前是什么动作
     * @param actionState                 当前动作的检测结果
     * @param sessionState                整个Session是否通过
     * @param remainingTimeoutMilliSecond 剩余时间，以毫秒为单位
     * @param faceInfo                    检测到的人脸信息，可以用来做动画
     * @param errorCodeOfInAction         动作不过关的可能原因，可以用来做提示语
     */
    @Override
    public void onFrameDetected(int currentActionType, int actionState, int sessionState, int remainingTimeoutMilliSecond, OliveappFaceInfo faceInfo, ArrayList<Integer> errorCodeOfInAction) {

        LogUtil.i(TAG, "[BEGIN] onFrameDetected " + remainingTimeoutMilliSecond);
        mCountDownTextView.setText("" + (remainingTimeoutMilliSecond / 1000 + 1));
        mLocationArray = getFaceInfoLocation(mCurrentActionType, faceInfo);
        mFrameRate += 1;
        long currentTimestamp = System.currentTimeMillis();
        if ((currentTimestamp - mLastTimestamp) > 1000) {
            mLastTimestamp = currentTimestamp;
            mFrameRateText.setText("FrameRate: " + mFrameRate + " FPS");
            mFrameRate = 0;
        }
        LogUtil.i(TAG, "[END] onFrameDetected");
    }

    /**
     * 切换到下一个动作时的回调方法
     *
     * @param lastActionType     上一个动作类型
     * @param lastActionResult   上一个动作的检测结果
     * @param newActionType      当前新生成的动作类型
     * @param currentActionIndex 当前是第几个动作
     * @param faceInfo           人脸的信息
     */
    public void onActionChanged(int lastActionType, int lastActionResult, int newActionType, int currentActionIndex, OliveappFaceInfo faceInfo) {
        try {
            // 更新提示文字
            String hintText;
            switch (newActionType) {
                case FacialActionType.MOUTH_OPEN:
                    hintText = getString(R.string.oliveapp_step_hint_mouthopen);
                    break;
                case FacialActionType.EYE_CLOSE:
                    hintText = getString(R.string.oliveapp_step_hint_eyeclose);
                    break;
                case FacialActionType.HEAD_UP:
                    hintText = getString(R.string.oliveapp_step_hint_headup);
                    break;
                case FacialActionType.HEAD_SHAKE_SIDE_TO_SIDE:
                    hintText = getString(R.string.oliveapp_step_hint_headshake);
                    break;
                default:
                    hintText = getString(R.string.oliveapp_step_hint_focus);
                    break;
            }
            mAnimController.playHintTextAnimation(hintText);
            mLocationArray = getFaceInfoLocation(newActionType, faceInfo);
            mCurrentActionType = newActionType;
            mAnimationHanlder.removeCallbacksAndMessages(null);
            mAnimationHanlder.post(mActionAnimationTask);
        } catch (Exception e) {
            LogUtil.i(TAG, "changeToNextAction interrupt");
        }

    }

    /**==========================================一些辅助函数==============================**/

    /**
     * 拿到当前动作对应的脸部坐标，用于做动画
     *
     * @param actionType 动作类型
     * @param faceInfo   对应的人脸信息
     * @return 脸部坐标数组
     */
    private ArrayList<Pair<Double, Double>> getFaceInfoLocation(int actionType, OliveappFaceInfo faceInfo) {
        ArrayList<Pair<Double, Double>> result = new ArrayList<Pair<Double, Double>>();
        switch (actionType) {
            case FacialActionType.EYE_CLOSE: {
                result.add(faceInfo.leftEye);
                result.add(faceInfo.rightEye);
                break;
            }
            case FacialActionType.MOUTH_OPEN: {
                result.add(faceInfo.mouthCenter);
                break;
            }
            case FacialActionType.HEAD_UP: {
                result.add(faceInfo.chin);
                break;
            }
        }
        return result;
    }

    //========================根据设置决定本Activity要采用哪个layout=======================//
    private int decideWhichLayout() {

        int layout = R.layout.oliveapp_sample_liveness_detection_main_portrait_phone;
        mOrientationType = PORTRAIT;// 获取屏幕方向

        //选择布局文件
        switch (mOrientationType) {
            case PORTRAIT:
                layout = setPortraitLayout();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case LANDSCAPE:
                if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    layout = R.layout.oliveapp_sample_liveness_detection_main_landscape;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case PORTRAIT_REVERSE:
                layout = setPortraitLayout();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case LANDSCAPE_REVERSE:
                if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    layout = R.layout.oliveapp_sample_liveness_detection_main_landscape;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                break;
        }
        return layout;
    }

    /**
     * 决定竖屏使用什么布局
     */
    private int setPortraitLayout() {
        if (SampleScreenDisplayHelper.ifThisIsPhone(this)) {
            return R.layout.oliveapp_sample_liveness_detection_main_portrait_phone;
        } else {
            return R.layout.oliveapp_sample_liveness_detection_main_portrait_tablet;
        }
    }

    //从描述泌尿数文字转换成为昏暗阈值
    public int getDarkLevelFromDesc(String desc) {
        //对应关系如下：
        //0：无
        //1：低
        //2：中
        //3：高
        switch (desc) {
            case "无":
                return 0;
            case "低":
                return 1;
            case "中":
                return 2;
            case "高":
                return 3;
            default:
                return 0;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {

            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            mXPercent = mCapFrame.getX() / width - 0.1f;
            mYPercent = mCapFrame.getY() / height - 0.1f;
            mWidthPercent = (float) mCapFrame.getWidth() / width + 0.1f;
            mHeightPercent = (float) mCapFrame.getHeight() / height + 0.1f;
        }
    }

    /////////////////// FOR DEBUG //////////////////////
    private TextView mFrameRateText;
    private long mLastTimestamp = System.currentTimeMillis();
    private int mFrameRate = 0;
    private static int classObjectCount = 0;

    private void increaseClassObjectCount() {
        // DEBUG: 检测是否有Activity泄漏
        classObjectCount++;
        LogUtil.i(TAG, "LivenessDetectionMainActivity classObjectCount onCreate: " + classObjectCount);

        // 预期现象是classObjectCount会在1~2之间抖动，如果classObjectCount一直在增长，很可能有内存泄漏
        if (classObjectCount == 10) {
            System.gc();
        }

//        Assert.assertTrue(classObjectCount < 10);
    }

    @Override
    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable e) {
            LogUtil.e(TAG, "无法完成finalize...", e);
        }
        // DEBUG: 检测是否有Activity泄漏。与increaseClassObjectCount对应
        classObjectCount--;
        LogUtil.i(TAG, "LivenessDetectionMainActivity classObjectCount finalize: " + classObjectCount);
    }


    /**
     * 播放动作动画和音频的Runnable
     */
    private Runnable mActionAnimationTask = new Runnable() {
        @Override
        public void run() {
            if (mAudioModule != null && mAnimController != null) {
                mAudioModule.playAudio(LivenessDetectionMainActivity.this, FacialActionType.getStringResourceName(mCurrentActionType));
                mAnimController.playActionAnimation(mCurrentActionType, mLocationArray);
                mAnimationHanlder.postDelayed(this, 2500);
            }
        }
    };

    /**
     * 播放预检动画和音频的Runnable
     */
    private Runnable mPreHintAnimation = new Runnable() {
        @Override
        public void run() {
            if (mAudioModule != null && mAnimController != null) {
                mAudioModule.playAudio(LivenessDetectionMainActivity.this, FacialActionType.getStringResourceName(FacialActionType.CAPTURE));
                mAnimController.playAperture();
                mAnimationHanlder.postDelayed(this, 2500);
            }
        }
    };

    /**
     * 算法初始化成功
     */
    public void onInitializeSucc() {
    }

    public void onInitializeFail(Throwable e) {

    }

    /**
     * 获取大礼包
     *
     * @return 大礼包
     */
    protected LivenessDetectionFrames getLivenessDetectionPackage() {
        return mVerificationController.getLivenessDetectionPackage();
    }

    @Override
    public void onMediaSaveSuccess(String s) {
        LogUtil.d(TAG, "onMediaSaveSuccess path = " + s + ", IsLivenessSuccess " + mIsLivenessSuccess);
        //活检失败将视频删除
        if (!mIsLivenessSuccess) {
            try {
                CameraUtil.deleteMediaRecord(s);
            } catch (Exception e) {
                LogUtil.e(TAG, "视频删除失败", e);
            }
        }
    }

    class LivenessOrientationListener extends OrientationEventListener {
        private Context mContext;

        LivenessOrientationListener(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) { //设置竖屏
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    restartDetection();
                    mOrientationType = PORTRAIT;
                }
            } else if (!SampleScreenDisplayHelper.ifThisIsPhone(mContext)
                    && orientation > 225 && orientation < 315) { //只有pad支持设置横屏
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    restartDetection();
                    mOrientationType = LANDSCAPE;
                }
            } else if (!SampleScreenDisplayHelper.ifThisIsPhone(mContext)
                    && orientation > 45 && orientation < 135) { //只有pad支持设置反向横屏
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    restartDetection();
                    mOrientationType = LANDSCAPE_REVERSE;
                }
            } else if (orientation > 135 && orientation < 225) {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    restartDetection();
                    mOrientationType = PORTRAIT_REVERSE;
                }
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
