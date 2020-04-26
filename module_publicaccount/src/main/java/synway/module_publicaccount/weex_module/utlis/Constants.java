package synway.module_publicaccount.weex_module.utlis;

/**
 * 常量
 *
 * @author dell
 */
public class Constants {

    //hot refresh
    public static final int HOT_REFRESH_CONNECT = 0x111;
    public static final int HOT_REFRESH_DISCONNECT = HOT_REFRESH_CONNECT + 1;
    public static final int HOT_REFRESH_REFRESH = HOT_REFRESH_DISCONNECT + 1;
    public static final int HOT_REFRESH_CONNECT_ERROR = HOT_REFRESH_REFRESH + 1;

    public static final String BROADCAST_CAMERA = "com.synway.osc.camera";

}