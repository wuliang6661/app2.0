package cn.synway.app.ui.recognize;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.LivenessDetectionFrames;
import com.oliveapp.face.livenessdetectorsdk.livenessdetector.datatype.OliveappFaceInfo;
import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liveness.sample.SampleSaaSResultActivity;
import com.oliveapp.liveness.sample.SampleUnusualResultActivity;
import com.oliveapp.liveness.sample.libsaasclient.backend.RequestWithSignatureHelper;
import com.oliveapp.liveness.sample.libsaasclient.backend.SignatureUtil;
import com.oliveapp.liveness.sample.libsaasclient.client.SaasClient;
import com.oliveapp.liveness.sample.libsaasclient.datatype.AccessInfo;
import com.oliveapp.liveness.sample.libsaasclient.datatype.UserInfo;
import com.oliveapp.liveness.sample.liveness.view_controller.LivenessDetectionMainActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PublicKey;
import java.util.concurrent.TimeoutException;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.base.SynApplication;
import cn.synway.app.bean.event.FaceAuthEvent;
import cn.synway.app.bean.event.FaceLoginEvent;

import static com.oliveapp.liveness.sample.utils.SampleScreenDisplayHelper.ORIENTATION_TYPE_NAME;


/**
 * 样例活体检测Activity
 */
public class YiTuLivenessActivity extends LivenessDetectionMainActivity {

    public static final String TAG = YiTuLivenessActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;
    private byte[] mPackageByteArray;

    private static final String KEY_APP_CENTER = "KEY_APP_CENTER";
    public static final String KEY_AUTH = "KEY_AUTH";

    private boolean forApplicationCenter;
    private boolean mForAuth;

    /**
     * 以校验身份为目的
     */
    public static void startForAuth(Context context) {
        Intent starter = new Intent(context, YiTuLivenessActivity.class);
        starter.putExtra(KEY_AUTH, true);
        context.startActivity(starter);
    }

    /**
     * 应用中心校验
     */
    public static void startForApplicationCenter(Context context) {
        Intent starter = new Intent(context, YiTuLivenessActivity.class);
        starter.putExtra(KEY_APP_CENTER, true);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forApplicationCenter = getIntent().getBooleanExtra(KEY_APP_CENTER, false);
        mForAuth = getIntent().getBooleanExtra(KEY_AUTH, false);
    }

    /**
     * =====================启动算法===============
     **/
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
        Intent i = new Intent(YiTuLivenessActivity.this, SampleUnusualResultActivity.class);
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
     * 活体检测成功的回调，同时获取大礼包
     * 成功回调的接口在1.7.a_hotfix被拆成了2步，不再使用该接口
     *
     * @param livenessDetectionFrames 活体检测抓取的图 片
     * @param faceInfo                捕获到的人脸信息
     */
    @Override
    public void onLivenessSuccess(final LivenessDetectionFrames livenessDetectionFrames, OliveappFaceInfo faceInfo) {
        // do nothing
    }

    @Override
    public void onLivenessSuccess(OliveappFaceInfo oliveappFaceInfo) {
        super.onLivenessSuccess(null, oliveappFaceInfo);
        LivenessDetectionFrames pkg = getLivenessDetectionPackage();
        if (pkg == null) {
            return;
        }
        mPackageByteArray = pkg.verificationData;
        PublicKey publicKey = null;
        String md5hash = null;
        String base64Img = Base64.encodeToString(mPackageByteArray, Base64.NO_WRAP);
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("query_image_package", base64Img);
            requestParams.put("query_image_package_return_image_list", true);
            requestParams.put("user_id", "SaasClientTester");
            publicKey = SignatureUtil.RSAHelper.loadPublicKey(RequestWithSignatureHelper.sPublicKeyContent);
            md5hash = SignatureUtil.generateSignature(publicKey, "testid", requestParams.toString(), "");
        } catch (Exception e) {
            LogUtil.e(TAG, "JsonException in requestParams makeup in packageVerification", e);
        }
        mProgressDialog = ProgressDialog.show(YiTuLivenessActivity.this,
                null, "正在比对，请稍等", true, false);
        HttpServerImpl.yiTuCompare(base64Img, md5hash, SynApplication.spUtils.getString("liveUser"))
                .subscribe(new HttpResultSubscriber<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if ("1".equals(s)) {
                            if (forApplicationCenter) {
                                EventBus.getDefault().post(new FaceAuthEvent(true));
                            }
                            if (mForAuth & !forApplicationCenter) {
                                EventBus.getDefault().post(new FaceLoginEvent(true));
                            }
                        } else {
                            if (forApplicationCenter) {
                                EventBus.getDefault().post(new FaceAuthEvent(false));
                            }
                            if (mForAuth & !forApplicationCenter) {
                                EventBus.getDefault().post(new FaceLoginEvent(false));
                            }
                            Toast.makeText(YiTuLivenessActivity.this, "人脸验证失败!", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }

                    @Override
                    public void onFiled(String message) {
                        ToastUtils.showShort("人脸验证失败！");
                        EventBus.getDefault().post(new FaceLoginEvent(false));
                        finish();
                    }
                });
    }

    /**
     * 活检阶段失败
     */
    @Override
    public void onLivenessFail(int result, LivenessDetectionFrames livenessDetectionFrames) {

        super.onLivenessFail(result, livenessDetectionFrames);

        Toast.makeText(this, "人脸识别失败！", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new FaceLoginEvent(false));
        finish();
    }

    /**
     * 获取大礼包并访问私有云的样例代码，集成时请不要使用
     */
    private class CheckImagePackage extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(YiTuLivenessActivity.this,
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
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(YiTuLivenessActivity.this);
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
                    intent.setClass(YiTuLivenessActivity.this, SampleSaaSResultActivity.class);
                    SampleSaaSResultActivity.mResultJsonString = result.toString();
                } else {
                    intent.setClass(YiTuLivenessActivity.this, SampleUnusualResultActivity.class);
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
