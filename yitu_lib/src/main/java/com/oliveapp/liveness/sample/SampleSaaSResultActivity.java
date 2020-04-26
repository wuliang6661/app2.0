package com.oliveapp.liveness.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liveness.sample.utils.OliveappOrientationListener;
import com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.ORIENTATION_TYPE_NAME;

public class SampleSaaSResultActivity extends Activity implements View.OnClickListener {

    public static final String TAG = SampleSaaSResultActivity.class.getSimpleName();

    private ListView mListview;
    private ImageView mImageViewClose;
    private ImageView mImageViewOpen;

    private Button mFinishBtn;
    private Button mRetryBtn;
    private Button mReturnBtn;

    /**
     * 存储传回的分析数据
     */
    public static String mResultJsonString;

    /**
     * mIfPassed : 每一个项目是否通过
     * mDetail： 每一个项目得到的具体分数
     * ITEMS ： 显示的所有项目名称
     * IFPASSKEY ： 用于从json中获取“是否通过”数据的key
     * DETAILKEY ： 用于从json获取“具体分数”的key
     * mImages ： 存储传回的两张照片
     * mIsFailed ： 存储本次检测是否通过
     */
    private static final String[] ITEMS = {"大礼包照片是否同一个人", "是否通过防屏幕拍摄", "是否通过防照片翻拍", "是否通过防眼部遮挡", "是否通过防孔洞面具"};
    private String[] DETAILKEY = {"is_anti_screen_check_score", "is_anti_picture_check_score", "is_anti_eye_blockage_check_score", "is_anti_hole_check_score"};
    private String[] IFPASSKEY = {"is_same_person", "is_anti_screen_check_passed", "is_anti_picture_check_passed", "is_anti_eye_blockage_check_passed", "is_anti_hole_check_passed"};
    private boolean[] mIfPassed = new boolean[IFPASSKEY.length];
    private String[] mDetail = new String[DETAILKEY.length];

    /**
     * 大礼包回传两张照片
     */
    private static final int BITMAPCOUNT = 2;
    private Bitmap[] mImages = new Bitmap[BITMAPCOUNT];

    private boolean mIsFailed = false;
    private boolean mParseJsonFailed = false;
    private OliveappOrientationListener mOliveappOrientationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 先解析json数据，若有一项不通过就表示失败
         */
        try {
            JSONObject json = new JSONObject(mResultJsonString);
            JSONObject queryResult = json.getJSONObject("query_image_package_result");

            for (int i = 0; i < IFPASSKEY.length; i++) {
                mIfPassed[i] = queryResult.getBoolean(IFPASSKEY[i]);
                if (!mIfPassed[i]) {
                    mIsFailed = true;
                }
                if (i != DETAILKEY.length) {
                    mDetail[i] = new DecimalFormat("#.#####").format(queryResult.getDouble(DETAILKEY[i]));
                }
            }

            /**
             * 解析返回的照片，并设置界面
             */
            JSONArray images = queryResult.getJSONArray("query_image_contents");

            if (images.length() >= BITMAPCOUNT) {
                String image1Base64 = (String) images.get(0);
                byte[] image1 = Base64.decode(image1Base64, Base64.NO_WRAP);
                mImages[0] = BitmapFactory.decodeByteArray(image1, 0, image1.length);

                String image2Base64 = (String) images.get(1);
                byte[] image2 = Base64.decode(image2Base64, Base64.NO_WRAP);
                mImages[1] = BitmapFactory.decodeByteArray(image2, 0, image2.length);
            }
        } catch (JSONException e) {
            LogUtil.e(TAG, e.toString());
            mParseJsonFailed = true;
        } finally {

        }

        /**
         * 设置全屏
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mOliveappOrientationListener = new OliveappOrientationListener(this);
        mOliveappOrientationListener.enable();
        /**
         * 决定显示哪个页面
         */
        LogUtil.e(TAG, "setContentView");
        setContentView(decideWhichLayout());
        LogUtil.e(TAG, "after setContentView");
        initViews();

        /**
         * 数据解析没有异常，就尝试显示数据
         */
        if (!mParseJsonFailed) {
            /**
             * 显示分析数据
             */
            OliveappSaasResultAdapter adapter = new OliveappSaasResultAdapter(this);
            mListview.setAdapter(adapter);
        }

    }

    //=================================初始化===============================//
    public void initViews() {

        mListview = (ListView) findViewById(R.id.oliveapp_liveness_saas_list);
        mImageViewClose = (ImageView) findViewById(R.id.oliveapp_liveness_result_close);
        mImageViewOpen = (ImageView) findViewById(R.id.oliveapp_liveness_result_open);

        if (mIsFailed) {
            mRetryBtn = (Button) findViewById(R.id.oliveapp_liveness_saas_result_retry);
            mReturnBtn = (Button) findViewById(R.id.oliveapp_liveness_saas_result_return);
            mRetryBtn.setOnClickListener(this);
            mReturnBtn.setOnClickListener(this);
        } else {
            mFinishBtn = (Button) findViewById(R.id.oliveapp_liveness_saas_result_finish);
            mFinishBtn.setOnClickListener(this);
        }

        mImageViewClose.setImageBitmap(mImages[0]);
        mImageViewOpen.setImageBitmap(mImages[1]);
    }

    //==============================选择布局=====================================//
    private int decideWhichLayout() {

        int layout = -1;
        LogUtil.e(TAG, "choose layout");

        SampleScreenDisplayHelper.OrientationType orientationType = (SampleScreenDisplayHelper.OrientationType)
                getIntent().getSerializableExtra(ORIENTATION_TYPE_NAME);// 获取屏幕方向

        //选择布局文件
        switch (orientationType) {
            case PORTRAIT:
                layout = setPortraitLayout();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case LANDSCAPE:
                if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    layout = setLandscapeLayout();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case PORTRAIT_REVERSE:
                layout = setPortraitLayout();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case LANDSCAPE_REVERSE:
                if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
                    layout = setLandscapeLayout();
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
        if (SampleScreenDisplayHelper.ifThisIsPhone(this) && mIsFailed) { //手机竖屏失败页面
            return R.layout.oliveapp_sample_saas_result_fail_portrait_phone;
        } else if (mIsFailed) {//平板竖屏失败页面
            return R.layout.oliveapp_sample_saas_result_fail_portrait_tablet;
        } else if (SampleScreenDisplayHelper.ifThisIsPhone(this)) {//手机竖屏通过页面
            return R.layout.oliveapp_sample_saas_result_success_portrait_phone;
        } else {//平板通过竖屏界面
            return R.layout.oliveapp_sample_saas_result_success_portrait_tablet;
        }
    }

    /**
     * 决定横屏使用什么布局
     */
    private int setLandscapeLayout() {
        if (mIsFailed) {//失败横屏
            return R.layout.oliveapp_sample_saas_result_fail_landscape;
        } else {//成功横屏
            return R.layout.oliveapp_sample_saas_result_success_landscape;
        }
    }

    /**
     * 此处界面跳转的时候要考虑到Activity的跳转和后台的回退栈
     * 所以有的地方是finish()
     * 而有的地方是startActivity()
     */
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        int i = view.getId();
        if (i == R.id.oliveapp_liveness_saas_result_finish) {
            intent.setClass(SampleSaaSResultActivity.this, SampleLaunchActivity.class);

        } else if (i == R.id.oliveapp_liveness_saas_result_retry) {
            finish();
            return;
        } else if (i == R.id.oliveapp_liveness_saas_result_return) {
            intent.setClass(SampleSaaSResultActivity.this, SampleLaunchActivity.class);

        }
        startActivity(intent);
        finish();
    }

    //===========================================用来显示比对数据的ListView的数据填充===========================================//

    class ViewHolder {
        TextView item;
        TextView detail;
        ImageView result;
    }

    class OliveappSaasResultAdapter extends BaseAdapter {

        private Context mContext;

        public OliveappSaasResultAdapter(Context context) {

            mContext = context;
        }

        @Override
        public int getCount() {
            return IFPASSKEY.length;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;

            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.oliveapp_sample_saas_result_item, null);
                holder = new ViewHolder();
                holder.item = (TextView) view.findViewById(R.id.oliveapp_liveness_result_item);
                holder.detail = (TextView) view.findViewById(R.id.oliveapp_liveness_result_detail);
                holder.result = (ImageView) view.findViewById(R.id.oliveapp_liveness_result_yesorno);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


            if (mIfPassed[i]) {
                holder.result.setImageResource(R.mipmap.oliveapp_liveness_result_right);
            } else {
                holder.result.setImageResource(R.mipmap.oliveapp_liveness_result_wrong);
                holder.detail.setTextColor(Color.RED);
            }


            holder.item.setText(ITEMS[i]);
            if (i == 0) {
                holder.detail.setText("");
            } else {
                holder.detail.setText(mDetail[i - 1]);
            }

            view.setLayoutParams(new ListView.LayoutParams(mListview.getWidth(), mListview.getHeight() / getCount()));
            holder.item.setTextSize(TypedValue.COMPLEX_UNIT_PX, mListview.getWidth() / 18);
            holder.detail.setTextSize(TypedValue.COMPLEX_UNIT_PX, mListview.getWidth() / 27);
            return view;
        }
    }

    /**
     * 横竖屏切换布局也要跟着换
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!SampleScreenDisplayHelper.ifThisIsPhone(this)) {
            int layout = -1;
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                layout = setLandscapeLayout();
            } else {
                if (mIsFailed) {//失败横屏
                    layout = R.layout.oliveapp_sample_saas_result_fail_portrait_tablet;
                } else {//成功横屏
                    layout = R.layout.oliveapp_sample_saas_result_success_portrait_tablet;
                }
            }
            setContentView(layout);
            initViews();
            /**
             * 数据解析没有异常，就尝试显示数据
             */
            if (!mParseJsonFailed) {
                OliveappSaasResultAdapter adapter = new OliveappSaasResultAdapter(this);
                mListview.setAdapter(adapter);
            }
        }
    }
}
