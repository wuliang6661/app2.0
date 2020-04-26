package com.oliveapp.liveness.sample.libsaasclient.client;

import android.util.Base64;
import android.util.Log;

import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.liveness.sample.libsaasclient.backend.RequestWithSignatureHelper;
import com.oliveapp.liveness.sample.libsaasclient.datatype.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by jxu on 1/23/16.
 */
public class SaasClient {
    public static final String TAG = SaasClient.class.getSimpleName();

    private UserInfo mUserInfo;
    public String mBaseURL;
    public String mURLUploadUserImage;
    public String mURLFacePairVerification;
    public String mURLCheckUserDatabaseImageStatus;
    public String mURLVerifyPairImages;
    public String mURLCheckFanpai;
    public String mURLTripleVerification;

    public SaasClient(UserInfo userInfo) {
        mUserInfo = userInfo;
        mBaseURL = "http://staging.yitutech.com";
        setupUrls();
    }

    public SaasClient(UserInfo userInfo, String baseUrl) {
        mUserInfo = userInfo;
        mBaseURL = baseUrl;
        setupUrls();

    }

    private void setupUrls() {
        mURLCheckFanpai = mBaseURL + "/face/basic/check_image_package";
        LogUtil.e(TAG, "mURLCheckFanpai: " + mURLCheckFanpai);
    }

    public ArrayList<String> decodePackage(byte[] queryPackage) throws TimeoutException, IOException {
        String base64EncodedPackage= Base64.encodeToString(queryPackage, Base64.NO_WRAP);
        return decodePackage(base64EncodedPackage);
    }

    public ArrayList<String> decodePackage(String queryPackageBase64) throws TimeoutException, IOException {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("query_image_package", queryPackageBase64);
            requestParams.put("query_image_package_return_image_list", true);
            requestParams.put("user_id", "SaasClientTester");
        } catch (JSONException e) {
            LogUtil.e(TAG, "JsonException in requestParams makeup in packageVerification", e);
        }

        JSONObject responseJson = null;
        try {
            responseJson = RequestWithSignatureHelper.requestWithSignature(
                    new URL(mURLFacePairVerification), "POST",
                    mUserInfo.getAccessInfo(),
                    "", requestParams, 2000, 15000);
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }

        ArrayList<String> result = new ArrayList<>();
        int rtn = 0;
        try {
            rtn = responseJson.getInt("rtn");
            if (rtn != 0) {
                LogUtil.w(TAG, "Request error: " + responseJson.toString());
            } else {
                LogUtil.i(TAG, "Response content: " + responseJson.toString());
            }
        } catch (JSONException e) {
            LogUtil.e(TAG, "JsonException in response", e);
        }

        try {
            JSONObject packageResult = responseJson.getJSONObject("query_image_package_result");
            if (packageResult != null) {
                JSONArray imageContents = packageResult.getJSONArray("query_image_contents");
                for (int i = 0; i < imageContents.length(); i++) {
                    result.add(imageContents.getString(i));
                }
            }
        } catch (JSONException e) {
            LogUtil.w(TAG, "JsonException in response");
        }

        return result;
    }


    public JSONObject checkImagePackage(byte[] queryPackage, String threshold) throws TimeoutException, IOException {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("query_image_package", Base64.encodeToString(queryPackage, Base64.NO_WRAP));
            requestParams.put("query_image_package_check_anti_screen", true);
            requestParams.put("query_image_package_check_same_person",true);
            requestParams.put("query_image_package_check_anti_picture",true);
            requestParams.put("query_image_package_check_anti_eye_blockage",true);
            requestParams.put("query_image_package_check_anti_hole",true);
            requestParams.put("query_image_package_return_image_list",true);

            //翻拍照阈值
            if (threshold != null) {
                requestParams.put("query_image_package_check_anti_screen_threshold", threshold);
            }

        } catch (JSONException e) {
            LogUtil.e(TAG, "JsonException in requestParams makeup in packageVerification", e);
        }

        JSONObject responseJson = null;
        try {
            responseJson = RequestWithSignatureHelper.requestWithSignature(
                    new URL(mURLCheckFanpai), "POST",
                    mUserInfo.getAccessInfo(),
                    "", requestParams, 2000, 15000);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }

        return responseJson;
    }
}
