package com.oliveapp.liveness.sample.idcard_captor.view_controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oliveapp.camerasdk.CameraManager;
import com.oliveapp.camerasdk.PhotoModule;
import com.oliveapp.camerasdk.utils.CameraUtil;
import com.oliveapp.face.idcardcaptorsdk.captor.CapturedIDCardImage;
import com.oliveapp.face.idcardcaptorsdk.captor.IDCardCaptor;
import com.oliveapp.face.idcardcaptorsdk.captor.IDCardCaptureEventHandlerIf;
import com.oliveapp.face.idcardcaptorsdk.captor.datatype.FrameData;
import com.oliveapp.face.idcardcaptorsdk.captor.datatype.IDCardStatus;
import com.oliveapp.face.idcardcaptorsdk.nativecode.session_manager.ImageForVerifyConf;
import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liblivenesscommon.utility.ApplicationParameters;
import com.oliveapp.liblivenesscommon.utility.ImageUtil;
import com.oliveapp.liveness.sample.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.oliveapp.face.idcardcaptorsdk.captor.IDCardCaptor.CARD_TYPE_FRONT;

public class SampleIdcardCaptorMainActivity extends Activity implements CameraManager.CameraPreviewDataCallback, IDCardCaptureEventHandlerIf, CameraManager.CameraPictureCallback {
    public static final String TAG = SampleIdcardCaptorMainActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 10001;

    // Camera Preview Parameter
    private static final float TARGET_PREVIEW_RATIO = 16 / 9f; // 摄像头Previwe预览界面的长宽比 默认使用16:9(因为要铺满全屏)
    private static final int MAX_PREVIEW_WIDTH = 1920; // 摄像头Preview预览界面的最大宽度，默认使用分辨率1920x1080

    private PhotoModule mPhotoModule; // 摄像头模块

    private IDCardCaptor mCaptor; // IDCARD CAPTOR

    private TextView mVerticalTextView;
    private ImageButton mTakePictureButton;
    private ImageView mIdcardShadeView;
    private View mPreview;
    private ImageView mScanLineImageView;

    private int mIdcardLeft;
    private int mIdcardRight;
    private int mIdcardTop;
    private int mIdcardBottom;
    // 真正发生preview的surface或者texture的位置
    private int mPreviewLeft;
    private int mPreviewRight;
    private int mPreviewTop;
    private int mPreviewBottom;
    private int mPreviewFrameWidth;
    private int mPreviewFrameHeight;

    // 捕获模式
    public static final int CAPTURE_MODE_AUTO = 0x00; // 自动捕获
    public static final int CAPTURE_MODE_MANUAL = 0x01; // 手动拍摄
    public static final int CAPTURE_MODE_MIXED = 0x10; // 混合模式，首先尝试自动捕获，指定时间后，采取手动拍摄

    public int mCaptureMode; // 捕获模式
    public int mCardType; // 身份证类型：正面/反面
    public int mDurationTime; // 尝试自动捕获时间，单位为秒，仅混合模式有效

    public static final String EXTRA_CAPTURE_MODE = "capture_mode";
    public static final String EXTRA_CARD_TYPE = "card_type";
    public static final String EXTRA_DURATION_TIME = "duration_time";

    // 定时器与定时任务，用于混合模式
    private Timer mTimer;
    private TimerTask mTimerTask;

    // 是否处于自动模式/混合模式
    private Boolean bIsAuto = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                LogUtil.e(TAG, "Uncaught exception", ex);
            }
        });

        super.onCreate(savedInstanceState);
	// DEBUG相关: 可以检测Activity内存泄漏
        increaseClassObjectCount();

        initData();
        // 初始化界面元素
        initViews();
        // 初始化摄像头
        initCamera();
        // 如果是自动模式/混合模式，初始化身份证捕获模块，
        if(bIsAuto) {
            initCaptor();
            // 如果是混合模式，启动定时任务
            if(mCaptureMode == CAPTURE_MODE_MIXED) {
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 计时器停止后，析构捕获模块，同时更新界面
                                synchronized (bIsAuto) {
                                    bIsAuto = false;
                                    mTakePictureButton.setVisibility(View.VISIBLE);
                                    mVerticalTextView.setVisibility(View.INVISIBLE);
                                    mScanLineImageView.setAnimation(null);
                                    mScanLineImageView.setVisibility(View.INVISIBLE);
                                    if (mCaptor != null) {
                                        mCaptor.uninit();
                                        mCaptor = null;
                                    }
                                }
                            }
                        });
                    }
                };
                mTimer.schedule(mTimerTask, mDurationTime * 1000);
            }
        } else {
            mTakePictureButton.setVisibility(View.VISIBLE);
            mVerticalTextView.setVisibility(View.INVISIBLE);
            mScanLineImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "[BEGIN] SampleIdcardCaptorMainActivity::onResume()");
        super.onResume();
		
		// 恢复摄像头预览和摄像头实时处理回调
        if (mPhotoModule != null) {
            mPhotoModule.onResume();
            try {
                mPhotoModule.setPreviewDataCallback(this, mCameraHandler);
            } catch (NullPointerException e) {
                LogUtil.e(TAG, "PhotoModule set callback failed", e);
            }
        }
        Log.i(TAG, "[END] SampleIdcardCaptorMainActivity::onResume()");
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "[BEGIN] SampleIdcardCaptorMainActivity::onPause()");
        super.onPause();

		// 暂停摄像头预览
        if (mPhotoModule != null) {
            mPhotoModule.onPause();
        }
        Log.i(TAG, "[END] SampleIdcardCaptorMainActivity::onPause()");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "[BEGIN] SampleIdcardCaptorMainActivity::onDestroy()");
        super.onDestroy();

        // 关闭摄像头
        if (mPhotoModule != null) {
            mPhotoModule.onStop();
        }
        CameraUtil.sContext = null; // 避免出现引用环造成内存泄漏
        mPhotoModule = null;

		// 析构捕获模块
        if (mCaptor != null) {
            mCaptor.uninit();
            mCaptor = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        Log.i(TAG, "[END] SampleIdcardCaptorMainActivity::onDestroy()");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(bIsAuto) {
            // 扫描动画效果
            ImageView skeletonImageView = (ImageView) findViewById(R.id.oliveapp_face_idcardSkeletonImageView);
            int[] location = new int[2];
            skeletonImageView.getLocationInWindow(location);
            int left = skeletonImageView.getLeft();
            int right = skeletonImageView.getRight();
            int top = skeletonImageView.getTop();
            // 平移位置由skeletonImageView在屏幕上的坐标决定
            Animation animation = new TranslateAnimation(left - 20, right - 20, top, top);
            animation.setDuration(3000);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);
            mScanLineImageView.startAnimation(animation);
        }
    }

    private void initData() {
        mCaptureMode = getIntent().getIntExtra(EXTRA_CAPTURE_MODE, CAPTURE_MODE_MIXED);
        if(mCaptureMode == CAPTURE_MODE_AUTO || mCaptureMode == CAPTURE_MODE_MIXED) {
            bIsAuto = true;
        } else {
            bIsAuto = false;
        }
        mCardType = getIntent().getIntExtra(EXTRA_CARD_TYPE, CARD_TYPE_FRONT);
        // 如果是混合模式，则mDurationTime没有作用
        mDurationTime = getIntent().getIntExtra(EXTRA_DURATION_TIME, 10);
    }

    /**
     * 初始化界面元素
     */
    private void initViews() {
        // Fullscreen looks better
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            /*View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);*/

            // Style中已经设置了NoActionBar
//            ActionBar actionBar = getActionBar();
//            actionBar.hide();
        }

        setContentView(R.layout.oliveapp_activity_sample_idcard_captor);

        mVerticalTextView = (TextView) findViewById(R.id.oliveapp_face_hintTextView);
        mTakePictureButton = (ImageButton) findViewById(R.id.oliveapp_face_takePictureButton);
        mIdcardShadeView = (ImageView) findViewById(R.id.oliveapp_face_idcardSkeletonImageView);
        mPreview = findViewById(R.id.oliveapp_face_cameraPreviewView);
        mTakePictureButton.setOnClickListener(mTakePhotoButtonClickListener);
        mScanLineImageView = (ImageView) findViewById(R.id.oliveapp_face_scan_line);

        // DEBUG: 调试相关
        mFrameRateText = (TextView) findViewById(R.id.oliveapp_frame_rate_text);
    }

    private Handler mCameraHandler; // 摄像头回调所在的消息队列
    private HandlerThread mCameraHandlerThread; // 摄像头回调所在的消息队列线程
    /**
     * 初始化并打开摄像头
     */
    private void initCamera() {
        LogUtil.i(TAG, "[BEGIN] initCamera");

        // 寻找设备上的后置摄像头
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int expectCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);


            LogUtil.i(TAG, "camera id: " + camIdx + ", facing: " + cameraInfo.facing + ", expect facing: " + expectCameraFacing);
            if (cameraInfo.facing == expectCameraFacing) {
                getIntent().putExtra(CameraUtil.EXTRAS_CAMERA_FACING, camIdx); // 设置需要打开的摄像头ID
                getIntent().putExtra(CameraUtil.MAX_PREVIEW_WIDTH, MAX_PREVIEW_WIDTH); // 设置最大Preview宽度
                getIntent().putExtra(CameraUtil.TARGET_PREVIEW_RATIO, TARGET_PREVIEW_RATIO); // 设置Preview长宽比
            }
        }
        mPhotoModule = new PhotoModule();
        mPhotoModule.init(this, findViewById(R.id.oliveapp_face_cameraPreviewView)); // 参考layout XML文件里定义的cameraPreviewView对象
        mPhotoModule.setPlaneMode(false, false); // 取消拍照和对焦功能
        mPhotoModule.setShutterRawDataCallback(this);
        // 打开摄像头预览
        mPhotoModule.onStart();

        // 初始化摄像头处理消息队列
        mCameraHandlerThread = new HandlerThread("CameraHandlerThread");
        mCameraHandlerThread.start();
        mCameraHandler = new Handler(mCameraHandlerThread.getLooper());

        LogUtil.i(TAG, "[END] initCamera");
    }

	/**
	 * 初始化捕获模块
	 */
    private void initCaptor() {
        mCaptor = new IDCardCaptor();
        try {
            // 在构造捕获模块时，需要制定身份证正面/反面
            // 正面：IDCardCaptor.CARD_TYPE_FRONT
            // 反面：IDCardCaptor.CARD_TYPE_BACK
            mCaptor.init(this, new Handler(getMainLooper()), this, mCardType);
        } catch (Exception e) {
            LogUtil.e(TAG, "无法初始化身份证翻拍照捕获模块", e);
        }
    }

    private int mCurrentFrameId; // 当前帧ID
    /**
     * 摄像头取帧回调函数
     */
    @Override
    public void onPreviewFrame(byte[] data, CameraManager.CameraProxy camera, int faces) {
        if(bIsAuto && mCaptor != null) {
            mCurrentFrameId++;
            Log.d(TAG, "[BEGIN] onPreviewFrame, frameID: " + mCurrentFrameId);
            Camera.Size size = camera.getParameters().getPreviewSize();
            List<Camera.Size> list = camera.getParameters().getSupportedPreviewSizes();
            for (Camera.Size size1: list)  {
                Log.i(TAG, "size1 w " + size1.width + ", size1 h " + size1.height);
            }
            Log.i(TAG, "preview size w " + size.width + ", preview size h " + size.height);
            prepareImageConfigForVerify(size.width, size.height, 0);

            // 不处理摄像头打开的前几帧(一般是10)。这些帧有可能比较黑，影响比对结果
            if (mCurrentFrameId < ApplicationParameters.CAMERA_DROP_FIRST_DARK_FRAME) {
                LogUtil.i(TAG, "onPreviewFrame, drop frame id: " + mCurrentFrameId);
                return;
            }

            LogUtil.i(TAG, "[BEGIN] onPreviewFrame, frame id: " + mCurrentFrameId);
            boolean isFrameHandled = false;

            // 当前帧
            try {
                mCaptor.doDetection(data, size.width, size.height);
            } catch (Exception e) {
                LogUtil.e(TAG, "doDetection failed with exception", e);
            }

            LogUtil.i(TAG, "[END] onPreviewFrame, 当前帧处理是否处理成功: " + isFrameHandled);
        }
    }

	/**
	 * 准备图像变换参数
	 */
    private void prepareImageConfigForVerify(int width, int height, int preRotateDegree) {
        Log.i(TAG, "prepareImageConfigForVerify");
		// 如果已经
        if (FrameData.sImageConfigForVerify != null)
            return;

        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);

        ImageView skeleton = (ImageView) findViewById(R.id.oliveapp_face_idcardSkeletonImageView);
        int locationOnScreen[] = new int[2]; // 画面中透明的身份证区域在屏幕上的尺寸
        skeleton.getLocationOnScreen(locationOnScreen);
        int skeletonWidth = skeleton.getMeasuredWidth(); // 屏幕的尺寸
        int skeletonHeight = skeleton.getMeasuredHeight();
        Log.i(TAG, "location on screen: " + locationOnScreen[0] + "," + locationOnScreen[1] + "; skeleton size: " + skeletonWidth + "," + skeletonHeight + "; screen size: " + screenSize.x + "," + screenSize.y);
		
		// 需要按照身份证提示框的1.2倍长、宽来截取画面
        FrameData.sImageConfigForVerify = new ImageForVerifyConf(width, height,
                760, 480, 1.0f * skeletonHeight / screenSize.y * 1.2f, (locationOnScreen[0] - skeletonWidth * 0.1f) / screenSize.x, preRotateDegree, 0, false);
    }

	/**
	 * 单帧结果回调
	 */
    @Override
    public void onFrameResult(int status) {
		// IDCardStatus.getHintFromStatus函数可以把状态码变成对应的提示文字
		// 当然，如果需要自定义界面，您也可以修改这个函数
        mVerticalTextView.setText(IDCardStatus.getHintFromStatus(status));
    }

	/**
	 * 翻拍照捕获结果回调
	 */
    @Override
    public void onIDCardCaptured(CapturedIDCardImage data) {
        // data.idcardImageData里保存了身份证翻拍照的JPEG数据
        mVerticalTextView.setText(IDCardStatus.getHintFromStatus(IDCardStatus.SUCCESS));
    }

    /**
     *
     * 点击拍照按钮事件
     *
     */
    private View.OnClickListener mTakePhotoButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        // 保存控件位置数据，用于裁剪图片，详情请参照cutIdcardImage方法
        LogUtil.i(TAG, "用户按了拍摄按钮");
        mIdcardBottom = mIdcardShadeView.getBottom();
        mIdcardLeft = mIdcardShadeView.getLeft();
        mIdcardRight = mIdcardShadeView.getRight();
        mIdcardTop = mIdcardShadeView.getTop();
        Point previewSize = mPhotoModule.getPreviewSize();
        Log.i(TAG, "preview size x " + previewSize.x + ", preview size y " + previewSize.y);
        mPreviewFrameWidth = previewSize.x;
        mPreviewFrameHeight = previewSize.y;
        int midX = (mPreview.getLeft() + mPreview.getRight())/2;
        int midY = (mPreview.getTop() + mPreview.getBottom())/2;
        mPreviewLeft = midX - mPreviewFrameWidth/2;
        mPreviewRight = midX + mPreviewFrameWidth/2;
        mPreviewTop = midY - mPreviewFrameHeight/2;
        mPreviewBottom = midX + mPreviewFrameHeight/2;

        mPhotoModule.captureWithCallBack(false);
        }
    };

    /////////////////// FOR DEBUG //////////////////////
	private TextView mFrameRateText;
    private static int classObjectCount = 0;

    private void increaseClassObjectCount() {
        // DEBUG: 检测是否有Activity泄漏
        classObjectCount++;
        LogUtil.e(TAG, "SampleIdcardCaptorMainActivity classObjectCount onCreate: " + classObjectCount);

        // 预期现象是classObjectCount是<10，如果classObjectCount一直在增长，很可能有内存泄漏
        if (classObjectCount == 10) {
            System.gc();
        } else if (classObjectCount > 10) {
            throw new RuntimeException("classObjectCount超过10个对象"); // 超过10个对象肯定是内存泄漏，不如crash掉直接暴露问题
        }
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
        LogUtil.e(TAG, "SampleIdcardCaptorMainActivity classObjectCount finalize: " + classObjectCount);
    }

    // 手动拍摄到照片后的回调函数
    @Override
    public void onPictureTaken(byte[] data, CameraManager.CameraProxy camera) {
            LogUtil.i(TAG, "[BEGIN] onPictureTaken...");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

           LogUtil.i(TAG, "original size " + bitmap.getWidth() + "x" + bitmap.getHeight());

        bitmap = cutIdcardImage(bitmap);

        // 某些手机设置MAX_PICTURE_SIZE无效
        int imageHeight;
        int imageWidth;
        if (bitmap.getWidth() > ApplicationParameters.ID_CARD_UPLOAD_IMAGE_WIDTH + 5) {
            imageHeight = (int) (ApplicationParameters.ID_CARD_UPLOAD_IMAGE_WIDTH / (float) bitmap.getWidth() * bitmap.getHeight());
            imageWidth = ApplicationParameters.ID_CARD_UPLOAD_IMAGE_WIDTH;
            if (1 == (imageHeight & 1)) {
                imageHeight -= 1;
            }
            if (1 == (imageWidth & 1)) {
                imageWidth -= 1;
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);
        }

        // make sure size of image is even, face detect need that
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
        if ((imageHeight & 1) == 1) {
            imageHeight -= 1;
        }
        if ((imageWidth & 1) == 1) {
            imageWidth -= 1;
        }
        if (imageHeight != bitmap.getHeight() || imageWidth != bitmap.getWidth()) {
            bitmap = ImageUtil.getSubBitmap(bitmap, imageWidth, imageHeight);
                LogUtil.d(TAG, "get sub image, width: " + bitmap.getWidth() + " imageHeight: " + bitmap.getHeight());
        }

            LogUtil.i(TAG, "final image size, width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());

        byte[] imageContent;
        try {
            imageContent = ImageUtil.convertBitmapToJPEGByteArray(bitmap, ApplicationParameters.USER_REGISTRATION_UPLOAD_IMAGE_QUALITY);
        } catch (IOException e) {
            LogUtil.e(TAG, "bitmap 转换为 jpeg失败， " + e.getLocalizedMessage());
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // TODO: 最好能够根据当前图片大小，调整压缩率
        if (imageContent.length > 600 * 1024) {
            Bitmap croppedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4 , bitmap.getHeight() / 4, false);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
            imageContent = byteArrayOutputStream.toByteArray();
        } else if (imageContent.length > 200 * 1024) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
            imageContent = byteArrayOutputStream.toByteArray();
        }

        // 调用onIDCardCaptured方法，跳转至结果界面
        onIDCardCaptured(new CapturedIDCardImage(imageContent));

        LogUtil.d(TAG, "[END] onPictureTaken done");
    }

    public Bitmap cutIdcardImage(Bitmap originalBitmap) {
        // 这个图是横的!
        // 非常复杂的裁剪图片逻辑!
        // 首先获取真实surfaceHolder 或者 textureHolder的位置(使用mPreview的中点,向上下算,可以超出真实Screen), 然后获取ImageView那个身份证框的位置, 这两个是可除的,
        // 都是显示像素作为单位
        // 之后按照这个比例切出身份证在jpeg上的子图
        // 细节可以参考 PhotoUI 的 setTransformMatrix 函数实现

        int imageWidth = originalBitmap.getWidth();
        int imageHeight = originalBitmap.getHeight();
        int targetX = (int)(imageWidth * ((double)(mIdcardTop - mPreviewTop) / mPreviewFrameHeight));
        int targetY = (int)(imageHeight * (((double)(mPreviewRight - mIdcardRight) / mPreviewFrameWidth)));
        int targetW = (int)(imageWidth * ((double)(mIdcardBottom - mIdcardTop)) / mPreviewFrameHeight);
        int targetH = (int)(imageHeight * ((double)(mIdcardRight - mIdcardLeft)) / mPreviewFrameWidth);
        return Bitmap.createBitmap(originalBitmap, targetX, targetY, targetW, targetH);
    }

}
