package synway.module_interface;

import synway.module_interface.config.userConfig.LoginUser;

public class HmIdUtils {


    public static String getId(LoginUser user) {
        if ("cgb".equals(user.LoginCode)) {
            user.ID = "184";
        }
        if ("mj2".equals(user.LoginCode)) {
            user.ID = "190";
        }
        if ("pcsjy".equals(user.LoginCode)) {
            user.ID = "185";
        }
        if ("pcssz".equals(user.LoginCode)) {
            user.ID = "186";
        }
        if ("mj".equals(user.LoginCode)) {
            user.ID = "191";
        }
        if ("gajfjz".equals(user.LoginCode)) {
            user.ID = "187";
        }
        return user.ID;
    }


    public static String getLoginCode(LoginUser user) {
        return user.LoginCode;
    }

}
