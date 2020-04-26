package synway.module_publicaccount.rtvideovoice.rtvoice;

/**
 * Created by 朱铁超 on 2018/5/22.
 */
import android.net.Uri;
public final class Util {

    public Util() {
    }


    public static boolean isUrlLocalFile(String path) {
        return getPathScheme(path) == null || "file".equals(getPathScheme(path));
    }

    public static String getPathScheme(String path) {
        return Uri.parse(path).getScheme();
    }


}
