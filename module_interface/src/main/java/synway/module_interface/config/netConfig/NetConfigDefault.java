package synway.module_interface.config.netConfig;

import synway.module_interface.BuildConfig;

public class NetConfigDefault {

    /**
     * http小数据通道地址 60.191.98.34 : 7000
     */
    public static final String DEFAULT_HTTP_IP;
    /**
     * Http小数据通道端口
     */
    public static final int DEFAULT_HTTP_PORT;

    static {
        if (BuildConfig.DEBUG) {
            DEFAULT_HTTP_IP = "172.18.100.29";
            DEFAULT_HTTP_PORT = 9060;
        } else {
            DEFAULT_HTTP_IP = "192.168.20.133";
            DEFAULT_HTTP_PORT = 9060;
        }
    }

    /**
     * TCP推送 60.191.98.34 : 7000
     */
    public static final String DEFAULT_TCP_IP = DEFAULT_HTTP_IP;
    /**
     * TCP推送
     */
    public static final int DEFAULT_TCP_PORT = 7001;

    /**
     * 文件 60.191.98.34 : 7000
     */
    public static final String DEFAULT_FTP_IP = "60.191.98.34";
    /**
     * 文件
     */
    public static final int DEFAULT_FTP_PORT = 7000;

    /**
     * UDP 60.191.98.34 : 7000
     */
    public static final String DEFAULT_UDP_IP = "60.191.98.34";
    /**
     * UDP
     */
    public static final int DEFAULT_UDP_PORT = 7002;

    /**
     * 流媒体 60.191.98.34 : 7000
     */
    public static final String DEFAULT_MEDIA_IP = "60.191.98.34";
    /**
     * 流媒体
     */
    public static final int DEFAULT_MEDIA_PORT = -1;

    /**
     * 公众号网页IP，默认为空，表示采用网页配置的IP
     */
    public static final String DEFAULT_FUNCTIONWEB_IP = "";
    /**
     * 公众号 60.191.98.34 : 7000
     */
    public static final String DEFAULT_FUNCTION_IP = "60.191.98.34";
    /**
     * 公众号
     */
    public static final int DEFAULT_FUNCTION_PORT = -1;

    /**
     * 视频 60.191.98.34 10.0.2.10
     */
    public static final String DEFAULT_VODEO_IP = "60.191.98.34";
    /**
     * 视频 7090  1936
     */
    public static final int DEFAULT_VODEO_PORT = 7090;
    /**
     * 视频 http端口  8080  129测试环境：7010
     */
    public static final int DEFAULT_VIDEO_HTTP_PORT = 8080;

    /**
     * 语音对讲服务器IP
     */
    public static final String DEFAULT_VOICE_IP = "60.191.98.34";
    /**
     * 语音对讲服务器端口
     */
    public static final int DEFAULT_VOICE_PORT = 7060;

    /**
     * rainbowchatAV音视频对讲服务器IP 192.168.0.17  172.16.1.244
     */
    // public static final String DEFAULT_RAINBOWCHATAV_IP = "172.16.1.244";
    public static final String DEFAULT_RAINBOWCHATAV_IP = "192.168.0.17";
    /**
     * rainbowchatAV音视频对讲服务器端口
     */
    public static final int DEFAULT_RAINBOWCHATAV_PORT = 10000;

    /**
     * 听音服务器IP
     */
    public static final String DEFAULT_AUDIO_IP = "12.33.33.36";
    /**
     * 听音服务器端口
     */
    public static final int DEFAULT_AUDIO_PORT = 9051;

    /**
     * NETTY http 地址 60.191.98.34 : 7000
     */
    public static final String DEFAULT_NETTY_HTTP_IP = "60.191.98.34";
    /**
     * NETTY Http端口
     */
    public static final int DEFAULT_NETTY_HTTP_PORT = 7000;
    /**
     * 通过TCP进行位置上传的端口
     */
    public static final int DEFAULT_TCP_LOCATION_UPLOAD_PORT = 9091;

    /***默认的哈密服务器IP*/
    public static final String DEFAULT_HM_IP = "60.191.98.34";
    /**
     * 默认的哈密服务器端口
     **/
    public static final int DEFAULT_HM_PORT = 8087;
}
