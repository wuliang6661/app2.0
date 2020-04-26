package synway.module_publicaccount.map.show;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by QSJH on 2016/5/4 0004.
 */
public class SpsLastHeadHeight {

    private static final String FILENAME = "FILE_LOCATION_LAST_HEAD_HEIGHT";

    public static final void saveLastHeadHeight(Context context, int lastHeight) {
        SharedPreferences.Editor editor = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS).edit();
        editor.putInt("LAST_HEAD_HEIGHT", lastHeight);
        editor.commit();
    }

    public static final int getLastHeadHeight(Context context) {
        SharedPreferences spf = context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS);
        return spf.getInt("LAST_HEAD_HEIGHT",0);
    }
}
