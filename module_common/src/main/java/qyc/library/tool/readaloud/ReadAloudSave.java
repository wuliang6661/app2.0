package qyc.library.tool.readaloud;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by qyc on 2016/6/14.
 */
class ReadAloudSave {

    private static final String FILE_NAME = "Library_ReadAloudConfig";
    private static final String SPFKEY_ISREADALOUND_ENABLED = "readAloudEnabled";

    public static final void saveReadAloudEnabled(Context context, boolean enabled) {
        SharedPreferences spf = context.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putBoolean(SPFKEY_ISREADALOUND_ENABLED, enabled);
        editor.commit();
    }

    public static final boolean getReadAloudEnabled(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        boolean i = spf.getBoolean(SPFKEY_ISREADALOUND_ENABLED, false);
        return i;
    }


}
