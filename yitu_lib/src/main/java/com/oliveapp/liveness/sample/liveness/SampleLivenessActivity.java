package com.oliveapp.liveness.sample.liveness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.LivenessDetectionFrames;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.LivenessSessionState;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.OliveappFaceInfo;
import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liveness.sample.SampleSaaSResultActivity;
import com.oliveapp.liveness.sample.SampleUnusualResultActivity;
import com.oliveapp.liveness.sample.libsaasclient.client.SaasClient;
import com.oliveapp.liveness.sample.libsaasclient.datatype.AccessInfo;
import com.oliveapp.liveness.sample.libsaasclient.datatype.UserInfo;
import com.oliveapp.liveness.sample.liveness.view_controller.LivenessDetectionMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.ORIENTATION_TYPE_NAME;


/**
 * 样例活体检测Activity
 */
public class SampleLivenessActivity extends LivenessDetectionMainActivity {

    public static final String TAG = SampleLivenessActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;
    private byte[] mPackageByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**=====================启动算法===============**/
    @Override
    public void onInitializeSucc() {
        super.onInitializeSucc();
        super.startVerification();
    }

    @Override
    public void onInitializeFail(Throwable e) {
        super.onInitializeFail(e);
        LogUtil.e(TAG, "无法初始化活体检测...", e);
        Toast.makeText(this, "无法初始化活体检测", Toast.LENGTH_LONG).show();
        Intent i = new Intent(SampleLivenessActivity.this, SampleUnusualResultActivity.class);
        i.putExtra(SampleUnusualResultActivity.keyToGetExtra, SampleUnusualResultActivity.INIT_FAIL);
        i.putExtra(ORIENTATION_TYPE_NAME, getOrientationType()); // 设置屏幕方向
        startActivity(i);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**===================================活体检测算法的回调==================================**/
    /**
     *  活体检测成功的回调，同时获取大礼包
     *  成功回调的接口在1.7.a_hotfix被拆成了2步，不再使用该接口
     * @param livenessDetectionFrames 活体检测抓取的图片
     * @param faceInfo 捕获到的人脸信息
     */
    @Override
    public void onLivenessSuccess(final LivenessDetectionFrames livenessDetectionFrames, OliveappFaceInfo faceInfo) {
        // do nothing
    }

    @Override
    public void onLivenessSuccess(OliveappFaceInfo oliveappFaceInfo) {
        super.onLivenessSuccess(null, oliveappFaceInfo);
        CheckImagePackage checkImagePackage = new CheckImagePackage();
        checkImagePackage.execute();
    }

    /**
     * 活检阶段失败
     */
    @Override
    public void onLivenessFail(int result, LivenessDetectionFrames livenessDetectionFrames) {

        super.onLivenessFail(result, livenessDetectionFrames);

        final int code = result;
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SampleLivenessActivity.this, SampleUnusualResultActivity.class);
                if (code == LivenessSessionState.SESSION_TIMEOUT){//超时
                    i.putExtra(SampleUnusualResultActivity.keyToGetExtra, SampleUnusualResultActivity.TIME_OUT);
                } else {//活检失败
                    i.putExtra(SampleUnusualResultActivity.keyToGetExtra, SampleUnusualResultActivity.OPERATION_EXCEPTION);
                }
                i.putExtra(ORIENTATION_TYPE_NAME, getOrientationType()); // 设置屏幕方向
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * 获取大礼包并访问私有云的样例代码，集成时请不要使用
     */
    private class CheckImagePackage extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(SampleLivenessActivity.this,
                    null, "正在比对，请稍等", true, false);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            // 获取大礼包，耗时操作，不可在UI线程执行
            LivenessDetectionFrames pkg = getLivenessDetectionPackage();
            if (pkg == null) {
                return null;
            }
            mPackageByteArray = pkg.verificationData;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(SampleLivenessActivity.this);
            boolean isDebug = sharedPrefs.getBoolean("pref_debug_mode", false);
            SaasClient saasClient;
            String threshold = null;
            if (isDebug) {
                String url = sharedPrefs.getString("pref_saas_url", "http://staging.yitutech.com");
                String testId = sharedPrefs.getString("pref_test_id", "testid");
                threshold = sharedPrefs.getString("pref_fanpaicls_threshold_list", "low");
                saasClient = new SaasClient(new UserInfo(testId, AccessInfo.getInstance().setAccessInfo(testId, "testid")), url);
            } else {
                saasClient = new SaasClient(new UserInfo("testid", AccessInfo.getInstance().setAccessInfo("testid", "testid")));
            }
            JSONObject result = new JSONObject();
            try {
                result = saasClient.checkImagePackage(mPackageByteArray, threshold);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            Intent intent = new Intent();
            try {
                if ((result != null) && (result.toString().length() != 0)
                        && (result.getInt("rtn") == 0 || result.getInt("rtn") == -6131)) {
                    intent.setClass(SampleLivenessActivity.this, SampleSaaSResultActivity.class);
                    SampleSaaSResultActivity.mResultJsonString = result.toString();
                } else {
                    intent.setClass(SampleLivenessActivity.this, SampleUnusualResultActivity.class);
                    intent.putExtra(SampleUnusualResultActivity.keyToGetExtra, SampleUnusualResultActivity.NETWORK_EXCEPTION);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra(ORIENTATION_TYPE_NAME, getOrientationType()); // 设置屏幕方向

            LogUtil.e(TAG, "[JUMP] intent is NETWORK_EXCEPTION");
            startActivity(intent);
            finish();
        }
    }
}
