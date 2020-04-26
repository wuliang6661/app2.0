package synway.module_interface.config.userConfig;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import synway.module_interface.module.LoacationObj;

//import com.google.zxing.common.StringUtils;

//请注意context.getSharedPreferences 中设置的第二个参数Context.MODE_MULTI_PROCESS
//因为需要在service push进程中读取/写入/清除操作
//所以第二个参数的属性不能设置为Context.MODE_PRIVATE
//MODE_MULTI_PROCESS :  can also be used if multiple processes are mutating the same SharedPreferences file. 
public class Sps_RWLoginUser {

    private static final String FILENAME = "FILE_LOGIN_USER";

    // private static final String KEY_IMSI = "LOGIN_IMSI";
    // private static final String KEY_IMEI = "LOGIN_IMEI";
    private static final String KEY_IS_LOGIN = "KEY_IS_LOGIN";
    private static final String KEY_ID = "LOGIN_ID";
    private static final String KEY_NAME = "LOGIN_NAME";
    private static final String KEY_PROVINCE = "LOGIN_PROVINCE";
    private static final String KEY_AREA = "LOGIN_AREA";
    private static final String KEY_LOGIN_CODE = "LOGIN_CODE";
    private static final String KEY_LOGIN_PWD = "LOGIN_PWD";
    private static final String KEY_LOGIN_TELNUMBER = "KEY_LOGIN_TELNUMBER";
    private static final String KEY_LOGIN_USERROLE = "KEY_LOGIN_USERROLE";
    private static final String KEY_LOGIN_USER_INFO_JSON = "KEY_LOGIN_USER_INFO_JSON";
    private static final String KEY_SESSION = "SESSION";
    private static final String KEY_INIT = "INITSTATE";
    private static final String KEY_USER_ORAGIN_ID = "KEY_USER_ORAGIN_ID";
    private static final String KEY_USER_ORAGIN = "KEY_USER_ORAGIN";

    /**
     * 从本地文件中获取登陆信息,如果没有将取出null
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static LoginUser readUser(Context context) {
        SharedPreferences spf = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS);
        LoginUser loginUser = new LoginUser();
        String userID = spf.getString(KEY_ID, null);

        if (userID == null) {
            // 没有userID就认为没有数据,没有数据就返回null,返回初始值已经没有意义
            return null;
        }

        loginUser.ID = userID;
        loginUser.name = spf.getString(KEY_NAME, null);
        loginUser.province = spf.getString(KEY_PROVINCE, null);
        loginUser.area = spf.getString(KEY_AREA, null);
        loginUser.LoginCode = spf.getString(KEY_LOGIN_CODE, null);
        loginUser.LoginPass = spf.getString(KEY_LOGIN_PWD, null);
        loginUser.userRole = spf.getInt(KEY_LOGIN_USERROLE, 1);
        loginUser.userInfoJson = spf.getString(KEY_LOGIN_USER_INFO_JSON, null);
        loginUser.userOragianId = spf.getString(KEY_USER_ORAGIN_ID, null);
        loginUser.userOragian = spf.getString(KEY_USER_ORAGIN, null);
        loginUser.telNumber = spf.getString(KEY_LOGIN_TELNUMBER, null);
        return loginUser;
    }

    /**
     * 从本地文件中获取登录信息中的用户ID,如果没有将取出null
     *
     * @param context
     * @return
     */
    @SuppressLint("InlinedApi")
    public static final String readUserID(Context context) {
        SharedPreferences spf = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS);
        return spf.getString(KEY_ID, null);
    }

    /**
     * 从本地文件中获取登录信息中的用户电话,如果没有，则返回null
     * 可能返回 "未知"字符串
     *
     * @param context
     * @return
     */
    @SuppressLint("InlinedApi")
    public static final String readUserTelNumber(Context context) {
        SharedPreferences spf = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS);
        return spf.getString(KEY_LOGIN_TELNUMBER, null);
    }
    // @SuppressLint("InlinedApi")
    // public static final String readIMEI(Context context){
    // SharedPreferences spf = context.getSharedPreferences(FILENAME,
    // Context.MODE_MULTI_PROCESS);
    // return spf.getString(KEY_IMEI, null);
    // }

    /**
     * <p>
     * 写入登陆信息
     * <p>
     * 登陆信息将被写入在本地文件中
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void writeUser(Context context, LoginUser loginInfoObj) {
        Editor editor = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS).edit();

        editor.putString(KEY_ID, loginInfoObj.ID);
        editor.putString(KEY_NAME, loginInfoObj.name);
        editor.putString(KEY_PROVINCE, loginInfoObj.province);
        editor.putString(KEY_AREA, loginInfoObj.area);
        editor.putString(KEY_LOGIN_CODE, loginInfoObj.LoginCode);
        editor.putString(KEY_LOGIN_PWD, loginInfoObj.LoginPass);
        editor.putString(KEY_LOGIN_TELNUMBER, loginInfoObj.telNumber);
        editor.putInt(KEY_LOGIN_USERROLE, loginInfoObj.userRole);
        editor.putString(KEY_LOGIN_USER_INFO_JSON, loginInfoObj.userInfoJson);
        editor.putString(KEY_USER_ORAGIN, loginInfoObj.userOragian);
        editor.putString(KEY_USER_ORAGIN_ID, loginInfoObj.userOragianId);
        editor.commit();
    }

    public static void writeLogin(Context context) {
        context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS)
                .edit()
                .putBoolean(KEY_IS_LOGIN, true)
                .apply();
    }

    public static boolean readLogin(Context context) {
        return context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS)
                .getBoolean(KEY_IS_LOGIN, false);
    }


    /**
     * <p>
     * 写入登陆ID
     * <p>
     * 登陆ID写入在本地文件中
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void writeLoginID(Context context, String loginTCPID) {
        Editor editor = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS).edit();
        editor.putString(KEY_ID, loginTCPID);
        editor.commit();
    }

    /**
     * <p>
     * 重置登陆信息
     * <p>
     * 登陆信息会从缓存中以及本地文件中被删除
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void clear(Context context) {
        context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS).edit().clear().commit();
    }

    /**
     * <p>
     * 写入语音SESSION
     * <p>
     * 信息将被写入在本地文件中
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void writeSession(Context context, String session) {
        Editor editor = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS).edit();
        editor.putString(KEY_SESSION, session);
        editor.commit();
    }

    /**
     * 从本地文件中获取登录信息中的用户SESSION,如果没有将取出null
     *
     * @param context
     * @return
     */
    @SuppressLint("InlinedApi")
    public static final String readSession(Context context) {
        SharedPreferences spf = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS);
        return spf.getString(KEY_SESSION, null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void writeInitState(Context context, int session) {
        Editor editor = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS).edit();
        editor.putInt(KEY_INIT, session);
        editor.commit();
    }

    @SuppressLint("InlinedApi")
    public static final int readInitState(Context context) {
        SharedPreferences spf = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS);
        return spf.getInt(KEY_INIT, 0);
    }

}
