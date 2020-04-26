package qyc.library.tool.linecontrol;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by qyc on 2016/6/13.
 * 用于保存线控的一些状态
 */
class LineControlSave {

    private static final String FILE_NAME = "Library_LineControlConfig";
    static final String SPFKEY_ISLINECONTROL_ENABLED = "lineControlEnabled";
    static final String SPFKEY_VOLUME_KEY_ENABLED = "volumeKeyEnabled";

    public static final void saveLineControlEnabled(Context context, boolean isLineControlEnabled) {
        SharedPreferences spf = context.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putBoolean(SPFKEY_ISLINECONTROL_ENABLED, isLineControlEnabled);
        editor.commit();
    }

    public static final boolean readLineControlEnabled(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        boolean i = spf.getBoolean(SPFKEY_ISLINECONTROL_ENABLED, false);
        return i;
    }

    public static final void saveVolumeKeyEnabled(Context context, boolean isVloumeKeyEnabled) {
        SharedPreferences spf = context.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putBoolean(SPFKEY_VOLUME_KEY_ENABLED, isVloumeKeyEnabled);
        editor.commit();
    }

    public static final boolean readVolumeKeyEnabled(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        boolean i = spf.getBoolean(SPFKEY_VOLUME_KEY_ENABLED, false);
        return i;
    }

}
