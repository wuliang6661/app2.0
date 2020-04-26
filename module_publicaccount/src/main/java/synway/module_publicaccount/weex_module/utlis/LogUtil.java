package synway.module_publicaccount.weex_module.utlis;

import android.util.Log;

/**
 * Log工具
 *
 * @author dell
 */
public class LogUtil {

    private static final String TAG = "WeexCode";

    /**
     * 打印啰嗦日志
     *
     * @param message：消息
     */
    public static void v(String message) {
        Log.v(TAG, message);
    }

    /**
     * 打印调试日志
     *
     * @param message：消息
     */
    public static void d(String message) {
        Log.d(TAG, message);
    }

    /**
     * 打印消息日志
     *
     * @param message：消息
     */
    public static void i(String message) {
        Log.i(TAG, message);
    }

    /**
     * 打印警告日志
     *
     * @param message：消息
     */
    public static void w(String message) {
        Log.w(TAG, message);
    }

    /**
     * 打印错误日志
     *
     * @param message：消息
     */
    public static void e(String message) {
        Log.e(TAG, message);
    }

}