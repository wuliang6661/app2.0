package synway.common.voiceplay;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 语音朗读开关的开启或关闭
 *
 * @author 孙量
 */
public class ReadAloudConfig {

    private static final String FILE_NAME = "Library_ReadAloudConfig";
    private static final String EXTRA_READ_ALOUD_IS_ENABLED = "readAloudEnabled";

    public static void saveReadAloudEnabled(Context context, boolean enabled) {
        SharedPreferences spf = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = spf.edit();
        editor.putBoolean(EXTRA_READ_ALOUD_IS_ENABLED, enabled);
        editor.commit();
    }

    public static boolean getReadAloudEnabled(Context context) {
        SharedPreferences spf = context.getSharedPreferences(FILE_NAME, 0);
        boolean i = spf.getBoolean(EXTRA_READ_ALOUD_IS_ENABLED, false);
        return i;
    }
}
