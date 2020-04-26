package synway.module_interface.config.netConfig;

import java.io.Serializable;

public class NetConfig implements Serializable {

    public String name;

    /**
     * 表示该网络配置是否是当前正在使用的
     */
    public boolean isCurrent;

    /**
     * 表示该网络配置是预先配置好的还是用户添加的
     */
    public boolean isPreset;

    /**
     * http小数据通道地址 218.108.76.82 : 7000
     */
    public String httpIP = NetConfigDefault.DEFAULT_HTTP_IP;
    /**
     * Http小数据通道端口
     */
    public int httpPort = NetConfigDefault.DEFAULT_HTTP_PORT;

    /**
     * NETTY http 地址 60.191.98.34 : 7000
     */
    public String nettyHttpIP = NetConfigDefault.DEFAULT_NETTY_HTTP_IP;

    /**
     * NETTY Http端口
     */
    public int nettyHttpPort = NetConfigDefault.DEFAULT_NETTY_HTTP_PORT;
    /**
     * 推送服务IP 172.16.1.129
     */
    public String tcpIP = NetConfigDefault.DEFAULT_TCP_IP;
    /**
     * 推送服务端口 9988
     */
    public int tcpPort = NetConfigDefault.DEFAULT_TCP_PORT;

    /**
     * 文件上传下载服务地址
     */
    public String ftpIP = NetConfigDefault.DEFAULT_FTP_IP;
    /**
     * 文件上传下载服务端口
     */
    public int ftpPort = NetConfigDefault.DEFAULT_FTP_PORT;

    /**
     * udpIP(位置上传)
     */
    public String udpIP = NetConfigDefault.DEFAULT_UDP_IP;
    /**
     * udpPort(位置上传)
     */
    public int udpPort = NetConfigDefault.DEFAULT_UDP_PORT;

    // /** 流媒体UDP地址 */
    // public String streamIP = "127.0.0.1";
    // /** 流媒体UDP端口 */
    // public int streamPort = 90;
    //


//    /**
//     * 流媒体TCP地址
//     */
//    public String mediaIP = NetConfigDefault.DEFAULT_MEDIA_IP;
//    /**
//     * 流媒体TCP端口
//     */
//    public int mediaPort = NetConfigDefault.DEFAULT_MEDIA_PORT;

    /**
     * 公众号网页IP
     */
    public String functionIP = NetConfigDefault.DEFAULT_FUNCTION_IP;
    /**
     * 公众号网页端口
     */
    public int functionPort = NetConfigDefault.DEFAULT_FUNCTION_PORT;
//    /**
//     * 公众号网页IP
//     */
//    public String functionWebIP = NetConfigDefault.DEFAULT_FUNCTIONWEB_IP;

    /**
     * 公众号听音服务器IP
     */
    public String audioIP = NetConfigDefault.DEFAULT_AUDIO_IP;
    /**
     * 公众号听音服务器端口
     */
    public int audioPort = NetConfigDefault.DEFAULT_AUDIO_PORT;

    /**
     * 视频直播IP
     */
    public String videoIP = NetConfigDefault.DEFAULT_VODEO_IP;
    /**
     * 视频直播端口
     */
    public int videoPort = NetConfigDefault.DEFAULT_VODEO_PORT;
    /**
     * 视频http端口
     */
    public int videoHttpPort = NetConfigDefault.DEFAULT_VIDEO_HTTP_PORT;

    /**
     * 语音对讲服务器IP
     */
    public String voiceIP = NetConfigDefault.DEFAULT_VOICE_IP;

    /**
     * 语音对讲服务器端口
     */
    public int voicePort = NetConfigDefault.DEFAULT_VOICE_PORT;

    /**
     * Tcp位置上传的端口
     */
    public int tcpLocationUploadPort = NetConfigDefault.DEFAULT_TCP_LOCATION_UPLOAD_PORT;

    /**
     * rainbowchatAV音视频对讲服务器IP
     */
    public String rainbowchatAVIP = NetConfigDefault.DEFAULT_RAINBOWCHATAV_IP;

    /**
     * rainbowchatAV音视频对讲服务器端口
     */
    public int rainbowchatAVPort = NetConfigDefault.DEFAULT_RAINBOWCHATAV_PORT;

    /**
     * 业务号配置查询服务ip端口
     */
    public String publicServerIP = "";

    public int publicServerPort = 0;

    /**
     * 全代理ip端口
     */
    public String proxyIP = "";

    public String proxyPort = "";

    // 不需要通过代理的IP(多个， “|”)
    public String nonProxyHosts = "";

    /**
     * 哈密IP、端口
     */
    public String HM_IP = NetConfigDefault.DEFAULT_HM_IP;

    public int HM_PORT = NetConfigDefault.DEFAULT_HM_PORT;


    /**
     * 不要修改此方法的返回值，该返回值显示在“配置页面（ConfigAct.java）”中的当前选择的配置项名称
     *
     * @return
     */
    @Override
    public String toString() {
        return name;
    }

    // 错误信息
    private String result[] = null;

    public void setResult(String result[]) {
        this.result = result;
    }

    public String[] getResult() {
        return this.result;
    }

    public String getString() {
        return publicServerIP + "----" + publicServerPort;
    }

}
