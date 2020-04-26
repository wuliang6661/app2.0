package synway.module_interface;

/**
 *
 */
public class AppConfig {

    /**
     * 是否为天津版本
     * 天津版本的唤醒服务唤醒间隔为1分钟,并且触发式心跳间隔为5秒.拍文件视频最大时长为30秒.
     */
    public static final boolean IS_TIANJIN = false;

    /**
     * 互联网检测是否关闭
     * 只有本地配置互联网检测开启,并且网络配置互联网检测也开启的情况下,才允许开启互联网检测.这东西比较影响性能.
     */
    public static final boolean IS_INTERNET_OPEN = true;

    /**
     * 是否显示网络电话
     */
    public static final boolean IS_SHOW_PPVOICE_CALL = true;

    /**
     * 是否使用新版视频录制器
     */
    public static final boolean IS_USE_NEW_VIDEO_RECORD = true;

    /**
     * 是否支持异地人员通讯
     */
    public static final boolean IS_SUPPORT_REMOTE_COMMUNICATION = false;

    /**
     * 指挥部已读/未读是否显示
     */
    public static final boolean IS_COMMAND_READ_SHOW = false;

    /**
     * 是否位置共享使用TCP方式而不使用HTTP方式
     */
    public static final boolean IS_LOCATION_USE_TCP_NO_HTTP = false;
    /**
     * 是否显示隐蔽摄像头APP菜单
     */
    public static final boolean IS_SHOW_OPENYINBISHEXIANTOU = false;
    /**
     * 是否显示图传菜单
     */
    public static final boolean IS_SHOW_OPENTUCHUAN = false;
    /**
     * 是否显示位置共享配置
     */
    public static final boolean IS_SHOW_LOCATIONCONFIG = true;
    /**
     * 为了兼容旧版服务和WEB端，图片能否带文字描述
     */
    public static final boolean IS_PICTURE_DESCRPTION = true;

    /**
     * 是否显示消息已读未读
     */
    public static final boolean IS_SHOW_MSGREAD = true;

    /**
     * 是否使用后台翻译word文件服务
     */
    public static final boolean IS_USE_TRANSLATE_SERVICE = true;

    /**
     * 是否使用新版流媒体服务器主动通知流状态
     * 新版nginx+rtmp流媒体服务器主动推送实时视频流的创建和关闭。而旧版由客户端推送。
     */
    public static final boolean USE_SERVER_NOTIFY_STREAM = true;

    /**
     * 是否默认开启弱网模式
     */
    public static final boolean DEFAULT_USE_WEAK_MODE = true;

    /**
     * 是否显示收藏
     */
    public static final boolean IS_SHOW_COLLECTION = true;
    /**
     * 是否显示实时视频回看
     */
    public static final boolean IS_SHOW_RTVIDEO = false;

    /**
     * 是否显示群拉人权限
     */
    public static final boolean IS_SHOW_GROUPADD = true;

    /**
     * 是否显示上报情报
     */
    public static final boolean IS_SHOW_UPLOAD_INTELLIGENCE = true;

    /**
     * 是否rainbowchatav音视频对讲
     */

    public static final boolean USE_RAINBOWCHATAV = true;


    /**
     * 是否使用新版RTVIDEO2  需要更改rtvideo 模块，若为了兼容旧版，则需要替换成rtvideo_old模块
     */
    public static final boolean IS_USE_RTVIDEO2 = true;

    /**
     * 是否在登陆后显示汇信宣传界面，默认为true 显示
     */
    public static final boolean IS_SHOW_PROMOTION_ACT = true;

    /**
     * 是否在群组详情界面显示重点/任务消息
     */
    public static final boolean IS_SHOW_COMMAND_SPECIAL_MSG = true;

    /**
     * 公众号未读数显示类型
     * 0:应用中心的图标支持未读消息数量的显示
     * 1:应用中心支持有一个统一的“消息入口”，进去后列出各未读应用的消息
     * 2: 0,1兼备
     */
    public static final int PUBLIC_UNREAD_TYPE = 2;

    /**
     * 是否使用公众号的搜索功能，默认为true 使用
     */
    public static final boolean IS_USE_PUBLIC_SEARCH = false;

    /**
     * 判断app是否为汇信版本，true:汇信 false:移动警务
     */
    public static final boolean IS_HX_VERSION = false;

    /**
     * 判断App是否需要隐藏掉UDP功能
     */
    public static final boolean IS_UDP_GONE = true;

    /**
     * app内版本更新功能是否启用,为false时检查更新时提示去应用市场下载
     */
    public static final boolean IS_UPDATE = false;

    /**
     * 是否开启活体人脸
     */
    public static boolean IS_LIVE_FACE = false;

}
