package com.oliveapp.liveness.sample.libsaasclient.datatype;
import com.oliveapp.libcommon.utility.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * AceessInfo代表调用者的身份
 */
public class AccessInfo {
    private final static String TAG = "AccessInfo";

    private static AccessInfo instance;
    public static AccessInfo getInstance() {
        if (instance == null)
            instance = new AccessInfo("android_accessinfo_not_set", "empty");
        return instance;
    }

    /**
     * Setting accessId & accessKey
     * @param accessId
     * @param accessKey
     */
    public AccessInfo setAccessInfo(String accessId, String accessKey) {
        if (accessId == null ||
                accessId.isEmpty()) {
            throw new IllegalArgumentException("invalid accessId");
        }

        if (accessKey == null || accessKey.isEmpty()) {
            throw new IllegalArgumentException("invalid accessKey");
        }

        mAccessId = accessId;
        mAccessKey = accessKey;
        LogUtil.w(TAG, "Setting AccessInfo, AccessId:" + accessId);

        return this;
    }
    /**
     * 构造AccessInfo
     *
     * @param accessId  使用者的 AccessId, 需要向依图申请
     * @param accessKey 使用者的 AccessKey, 需要向依图申请
     */
    private AccessInfo(String accessId, String accessKey) throws IllegalArgumentException {
        if (accessId == null ||
                accessId.isEmpty()) {
            throw new IllegalArgumentException("invalid accessId");
        }

        if (accessKey == null || accessKey.isEmpty()) {
            throw new IllegalArgumentException("invalid accessKey");
        }

        this.mAccessId = accessId;
        this.mAccessKey = accessKey;
    }

    public static AccessInfo fromJsonString(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        return new AccessInfo(jsonObject.getString("access_id"), jsonObject.getString("access_key"));

    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("access_id", mAccessId);
            object.put("access_key", mAccessKey);
        } catch (JSONException e) {
            LogUtil.e(TAG, "fail to transfer access info to string", e);
        }

        return object.toString();
    }

    public String getAccessId() {
        return this.mAccessId;
    }

    public String getAccessKey() {
        return this.mAccessKey;
    }


    private String mAccessId; // 客户标识
    private String mAccessKey; // 请求密钥

}
