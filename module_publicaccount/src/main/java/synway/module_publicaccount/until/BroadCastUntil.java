package synway.module_publicaccount.until;

/**
 * Created by ysm on 2017/1/23.
 */

public class BroadCastUntil {
    public static final class ChangeMenuMsg {
        public static final String ChangeMenu = "push.changemenumsg.changemenu";
        public static final String freshUI="push.changemenumsg.changepic";

    }
    public static final class ChangePointMsg {
        public static final String ChangePoint = "push.changepointmsg.changepoint";
        public static final String POINTS="push.changepointsg.points";
        public static  String getChangePoint(String targetID) {
            return ChangePoint + targetID;
        }
    }

}
