package com.oliveapp.liveness.sample.libsaasclient.datatype;


import com.oliveapp.libcommon.utility.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jqshen on 5/24/15.
 */
public class UserInfo {
    /**
     * 一个注册用户的信息,包含AccessInfo和用户ID
     */
    public static String BUNDLE_KEY = "USER_INFO";
    private static final String TAG = UserInfo.class.getSimpleName();

    private static final int MAX_USER_ID_LENGTH = 80;
    private static final int CITIZEN_ID_LENGTH = 18;
    private static final int MAX_USER_NAME_LENGTH = 80;

    /**
     *  构造函数
     *  @param userId       最大长度: 128字节，utf-8编码
     *  @param citizenId  用户身份证号，最长18字节，utf-8编码，如果照片类是金融行业带水印的证件照_小, 金融行业带水印的证件照_中, 公安行业带水印的证件照_小, 公安行业带水印的证件照_中之一，则可传入null
     *  @param userName   最长128字节，用户姓名，如果照片类是金融行业带水印的证件照_小, 金融行业带水印的证件照_中, 公安行业带水印的证件照_小, 公安行业带水印的证件照_中之一，则可传入null
     *  @param accessInfo   客户标示，
     *  @exception          IllegalArgumentException 传入的参数不符合要求
     */
    public UserInfo(String userId,
                    String citizenId,
                    String userName,
                    AccessInfo accessInfo) throws IllegalArgumentException {
        setUserId(userId);

        mCitizenId = citizenId; // can be empty
        mUserName = userName; // can be empty
        mAccessInfo = accessInfo;
    }


    public UserInfo(String userId, AccessInfo accessInfo) throws IllegalArgumentException {
        this(userId, "", "", accessInfo);
    }

    public AccessInfo getAccessInfo() {
        return mAccessInfo;
    }

    public void setAccessInfo(AccessInfo mAccessInfo) throws IllegalArgumentException {
        if (mAccessInfo == null) {
            throw new IllegalArgumentException("accessInfo is null");
        }
        this.mAccessInfo = mAccessInfo;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) throws IllegalArgumentException {

        if (userName == null) {
            throw new IllegalArgumentException("User name is null");
        } else if (userName.length() > MAX_USER_NAME_LENGTH) {
            throw new IllegalArgumentException("User name exceed " + MAX_USER_NAME_LENGTH);
        } else if (userName.isEmpty()) {
            throw new IllegalArgumentException("User name is empty");
        }

        this.mUserName = userName;
    }

    public String getCitizenId() {
        return mCitizenId;
    }

    public void setCitizenId(String citizenId) {
        if (citizenId.length() != CITIZEN_ID_LENGTH) {
            throw new IllegalArgumentException("Citizen id lenth is not " + CITIZEN_ID_LENGTH);
        }

        mCitizenId = citizenId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId is null");
        } else if (userId.length() > MAX_USER_ID_LENGTH) {
            throw new IllegalArgumentException("UserId length exceed " + MAX_USER_ID_LENGTH);
        } else if (userId.isEmpty()) {
            throw new IllegalArgumentException("UserId is empty");
        }

        mUserId = userId;
    }

    public String toString() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("user_name", mUserName);
            obj.put("citizen_id", mCitizenId);
            obj.put("user_id", mUserId);
            obj.put("access_info", mAccessInfo.toString());
        } catch (JSONException e) {
            LogUtil.e(TAG, "Usre Info to string", e);
        }

        return obj.toString();
    }

    private AccessInfo mAccessInfo;

    private String mUserName;

    private String mCitizenId;

    private String mUserId;

    static public UserInfo fromString(String userBasicInfoString) {
        try {
            JSONObject obj = new JSONObject(userBasicInfoString);
            String userName = obj.getString("user_name");
            String citizenId = obj.getString("citizen_id");
            String userId = obj.getString("user_id");
            AccessInfo accessInfo = AccessInfo.fromJsonString(obj.getString("access_info"));
            UserInfo userInfo = new UserInfo(userId, citizenId, userName, accessInfo);
            return userInfo;
        } catch (Exception e) {
            LogUtil.e(TAG, "USER_INFO from string", e);
            return null;
        }

    }
}
