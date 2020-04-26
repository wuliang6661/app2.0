package com.oliveapp.liveness.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liveness.sample.utils.OliveappOrientationListener;
import com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper;

import java.util.HashMap;
import java.util.Map;

import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.ORIENTATION_TYPE_NAME;


public class SampleUnusualResultActivity extends Activity implements View.OnClickListener {

    public static final int PARSE_INTENT_FAIL = -1;
    public static final int OPERATION_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIME_OUT = 2;
    public static final int INIT_FAIL = 3;
    public static final int PRESTART_FAIL = 4;
    public static final int LIVENESS_FAIL = 5;

    private final String OPERATION_EXCEPTION_DATA = "操作异常";
    private final String NETWORK_EXCEPTION_DATA = "网络异常，请返回重试";
    private final String TIME_OUT_DATA = "超时，请返回重试";
    private final String INIT_FAIL_DATA = "初始化失败，请检查权限设置和摄像头";
    private final String PRESTART_FAIL_DATA = "预检失败， 请检查设置或重试";
    private final String LIVENESS_FAIL_DATA = "活检失败，请重试";
    private final String PARSE_INTENT_FAIL_DATA = "解析传来的intent失败，请检查";

    private Map<Integer, String> mExceptionData;

    public static final String keyToGetExtra = "REASON";
    public static final String TAG = SampleUnusualResultActivity.class.getSimpleName();

    private TextView mOliveappResultTextView;
    private Button mOliveappResultReturnBtn;
    private Button mOliveappResultRetryBtn;
    private OliveappOrientationListener mOliveappOrientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.e(TAG, "[JUMP] enter unusualActivity");
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //决定显示哪个页面
        setContentView(decideWhichLayout());
        mOliveappOrientationListener = new OliveappOrientationListener(this);
        mOliveappOrientationListener.enable();

        init();
        //反馈信息
        int code = getIntent().getIntExtra(keyToGetExtra, PARSE_INTENT_FAIL);
        mOliveappResultTextView.setText(mExceptionData.get(code));
    }

    //===============================初始化数据=================================//
    public void init() {

        mExceptionData = new HashMap<>();
        mExceptionData.put(PARSE_INTENT_FAIL, PARSE_INTENT_FAIL_DATA);
        mExceptionData.put(OPERATION_EXCEPTION, OPERATION_EXCEPTION_DATA);
        mExceptionData.put(NETWORK_EXCEPTION, NETWORK_EXCEPTION_DATA);
        mExceptionData.put(TIME_OUT, TIME_OUT_DATA);
        mExceptionData.put(INIT_FAIL, INIT_FAIL_DATA);
        mExceptionData.put(PRESTART_FAIL, PRESTART_FAIL_DATA);
        mExceptionData.put(LIVENESS_FAIL, LIVENESS_FAIL_DATA);

        mOliveappResultTextView = (TextView) findViewById(R.id.oliveappResultTextView);
        mOliveappResultReturnBtn = (Button) findViewById(R.id.oliveapp_liveness_result_return);
        mOliveappResultRetryBtn = (Button) findViewById(R.id.oliveapp_liveness_result_retry);

        mOliveappResultRetryBtn.setOnClickListener(this);
        mOliveappResultReturnBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        /**
         * 这里如果点击重试按钮，则不用做任何事情，只要finish掉就好了
         */
        if (R.id.oliveapp_liveness_result_return == view.getId()) {
            Intent intent = new Intent(SampleUnusualResultActivity.this, SampleLaunchActivity.class);
            startActivity(intent);
        }
        finish();
    }

    //==================================选择布局=====================================//
    private int decideWhichLayout() {
        int layout = R.layout.oliveapp_sample_result_unusual_portrait_phone;

        SampleScreenDisplayHelper.OrientationType orientationType = (SampleScreenDisplayHelper.OrientationType)
                getIntent().getSerializableExtra(ORIENTATION_TYPE_NAME);// 获取屏幕方向

        //选择布局文件
        switch (orientationType) {
            case PORTRAIT:
                if (SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    layout = R.layout.oliveapp_sample_result_unusual_portrait_phone;
                } else {
                    layout = R.layout.oliveapp_sample_result_unusual_portrait_tablet;
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case LANDSCAPE:
                if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    layout = R.layout.oliveapp_sample_result_unusual_landscape;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case PORTRAIT_REVERSE:
                if (SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    layout = R.layout.oliveapp_sample_result_unusual_portrait_phone;
                } else {
                    layout = R.layout.oliveapp_sample_result_unusual_portrait_tablet;
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case LANDSCAPE_REVERSE:
                if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    layout = R.layout.oliveapp_sample_result_unusual_landscape;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                break;
        }
        return layout;
    }
}
