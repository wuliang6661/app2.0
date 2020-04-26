package synway.module_publicaccount.until;

import com.blankj.utilcode.util.SPUtils;

public class SPDao {
    private static String USER = "user";

    public static void saveUser(String string) {
        SPUtils.getInstance().put(USER, string);
    }

    public static String getUSERJson() {
        return SPUtils.getInstance().getString(USER, "");
    }

}
