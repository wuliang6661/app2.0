package synway.module_interface.config.netConfig;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Sps_NetConfig {

    public static final String FILE_CONFIGK = "FILE_CONFIG";

    public static final String KEY_HTTP_IP = "KEY_NETCONFIG_HTTPIP";
    public static final String KEY_HTTP_PORT = "KEY_NETCONG_HTTPPORT";

    public static final String KEY_NETTY_HTTP_IP = "KEY_NETCONFIG_NETTY_HTTPIP";
    public static final String KEY_NETTY_HTTP_PORT = "KEY_NETCONG_NETTY_HTTPPORT";

    public static final String KEY_TCP_IP = "KEY_TCP_IP";
    public static final String KEY_TCP_PORT = "KEY_TCP_PORT";

    public static final String KEY_FTP_IP = "KEY_FTP_IP";
    public static final String KEY_FTP_PORT = "KEY_FTP_PORT";

    public static final String KEY_UDP_IP = "KEY_UDP_IP";
    public static final String KEY_UDP_PORT = "KEY_UDP_PORT";

    public static final String KEY_MEDIA_IP = "KEY_MEDIA_IP";
    public static final String KEY_MEDIA_PORT = "KEY_MEDIA_PORT";

    public static final String KEY_FUNCTION_IP = "KEY_FUNCTION_IP";
    public static final String KEY_FUNCTION_PORT = "KEY_FUNCTION_PORT";

    public static final String KEY_VIDEO_IP = "KEY_VIDEO_IP";
    public static final String KEY_VIDEO_PORT = "KEY_VIDEO_PORT";
    public static final String KEY_VIDEO_HTTP_PORT = "KEY_VIDEO_HTTP_PORT";

    public static final String KEY_VOICE_IP = "KEY_VOICE_IP";
    public static final String KEY_VOICE_PORT = "KEY_VOICE_PORT";

    public static final String KEY_RAINBOWCHATAV_IP = "KEY_RAINBOWCHATAV_IP";
    public static final String KEY_RAINBOWCHATAV_PORT = "KEY_RAINBOWCHATAV_PORT";

    public static final String KEY_FUNCTION_WEB_IP = "KEY_FUNCTION_WEB_IP";

    public static final String KEY_PUBLICSERVER_IP = "KEY_PUBLICSERVER_IP";
    public static final String KEY_PUBLICSERVER_PORT = "KEY_PUBLICSERVER_PORT";

    public static final String KEY_PROXY_IP = "KEY_PROXY_IP";
    public static final String KEY_PROXY_PORT = "KEY_PROXY_PORT";

    public static final String KEY_HM_IP = "KEY_HM_IP";
    public static final String KEY_HM_PORT = "KEY_HM_PORT";

    public static NetConfig getNetConfigFromSpf(Context context) {
        NetConfig netConfig = NetConfigHelper.getCurrentNetConfig(context);
        SharedPreferences spf = context.getSharedPreferences(FILE_CONFIGK,
                Context.MODE_MULTI_PROCESS);

        netConfig.ftpIP = spf.getString(KEY_FTP_IP, netConfig.ftpIP);
        netConfig.ftpPort = spf.getInt(KEY_FTP_PORT, netConfig.ftpPort);

        netConfig.nettyHttpIP = spf.getString(KEY_NETTY_HTTP_IP, netConfig.nettyHttpIP);
        netConfig.nettyHttpPort = spf.getInt(KEY_NETTY_HTTP_PORT, netConfig.nettyHttpPort);


        netConfig.httpIP = spf.getString(KEY_HTTP_IP, netConfig.httpIP);
        netConfig.httpPort = spf.getInt(KEY_HTTP_PORT, netConfig.httpPort);

        netConfig.tcpIP = spf.getString(KEY_TCP_IP, netConfig.tcpIP);
        netConfig.tcpPort = spf.getInt(KEY_TCP_PORT, netConfig.tcpPort);

        netConfig.udpIP = spf.getString(KEY_UDP_IP, netConfig.udpIP);
        netConfig.udpPort = spf.getInt(KEY_UDP_PORT, netConfig.udpPort);

        //.mediaIP = spf.getString(KEY_MEDIA_IP, netConfig.mediaIP);
        //netConfig.mediaPort = spf.getInt(KEY_MEDIA_PORT, netConfig.mediaPort);

        netConfig.functionIP = spf.getString(KEY_FUNCTION_IP,
                netConfig.functionIP);
        netConfig.functionPort = spf.getInt(KEY_FUNCTION_PORT,
                netConfig.functionPort);

        netConfig.videoIP = spf.getString(KEY_VIDEO_IP, netConfig.videoIP);
        netConfig.videoPort = spf.getInt(KEY_VIDEO_PORT, netConfig.videoPort);
        netConfig.videoHttpPort = spf.getInt(KEY_VIDEO_HTTP_PORT, netConfig.videoHttpPort);

        //流媒体测试地址
        netConfig.voiceIP = spf.getString(KEY_VOICE_IP, netConfig.voiceIP);
        netConfig.voicePort = spf.getInt(KEY_VOICE_PORT, netConfig.voicePort);

        netConfig.rainbowchatAVIP = spf.getString(KEY_RAINBOWCHATAV_IP, netConfig.rainbowchatAVIP);
        netConfig.rainbowchatAVPort = spf.getInt(KEY_RAINBOWCHATAV_PORT, netConfig.rainbowchatAVPort);

        netConfig.publicServerIP = spf.getString(KEY_PUBLICSERVER_IP, netConfig.publicServerIP);
        netConfig.publicServerPort = spf.getInt(KEY_PUBLICSERVER_PORT, netConfig.publicServerPort);

        //netConfig.functionWebIP=spf.getString(KEY_FUNCTION_WEB_IP, netConfig.functionWebIP);

        netConfig.proxyIP = spf.getString(KEY_PROXY_IP, netConfig.proxyIP);
        netConfig.proxyPort = spf.getString(KEY_PROXY_PORT, netConfig.proxyPort);

        netConfig.HM_IP = spf.getString(KEY_HM_IP, netConfig.HM_IP);
        netConfig.HM_PORT = spf.getInt(KEY_HM_PORT, netConfig.HM_PORT);
        // 不需要通过代理的IP
        netConfig.nonProxyHosts = netConfig.httpIP + "|" + netConfig.nettyHttpIP + "|"
                + netConfig.tcpIP + "|" + netConfig.ftpIP + "|" + netConfig.udpIP + "|"
                + netConfig.audioIP + "|" + netConfig.videoIP + "|" + netConfig.voiceIP + "|"
                + netConfig.rainbowchatAVIP;
        return netConfig;
    }

    public static void saveNetConfigInSpf(Context context, NetConfig netConfig) {
        Editor edt = context.getSharedPreferences(FILE_CONFIGK,
                Context.MODE_MULTI_PROCESS).edit();

        edt.putString(KEY_HTTP_IP, netConfig.httpIP);
        edt.putInt(KEY_HTTP_PORT, netConfig.httpPort);

        edt.putString(KEY_NETTY_HTTP_IP, netConfig.nettyHttpIP);
        edt.putInt(KEY_NETTY_HTTP_PORT, netConfig.nettyHttpPort);

        edt.putString(KEY_FTP_IP, netConfig.ftpIP);
        edt.putInt(KEY_FTP_PORT, netConfig.ftpPort);

        edt.putString(KEY_TCP_IP, netConfig.tcpIP);
        edt.putInt(KEY_TCP_PORT, netConfig.tcpPort);

        edt.putString(KEY_UDP_IP, netConfig.udpIP);
        edt.putInt(KEY_UDP_PORT, netConfig.udpPort);

        //edt.putString(KEY_MEDIA_IP, netConfig.mediaIP);
        //edt.putInt(KEY_MEDIA_PORT, netConfig.mediaPort);

        edt.putString(KEY_FUNCTION_IP, netConfig.functionIP);
        edt.putInt(KEY_FUNCTION_PORT, netConfig.functionPort);

        edt.putString(KEY_VIDEO_IP, netConfig.videoIP);
        edt.putInt(KEY_VIDEO_PORT, netConfig.videoPort);
        edt.putInt(KEY_VIDEO_HTTP_PORT, netConfig.videoHttpPort);

        edt.putString(KEY_VOICE_IP, netConfig.voiceIP);
        edt.putInt(KEY_VOICE_PORT, netConfig.voicePort);

        edt.putString(KEY_RAINBOWCHATAV_IP, netConfig.rainbowchatAVIP);
        edt.putInt(KEY_RAINBOWCHATAV_PORT, netConfig.rainbowchatAVPort);

        edt.putString(KEY_PUBLICSERVER_IP, netConfig.publicServerIP);
        edt.putInt(KEY_PUBLICSERVER_PORT, netConfig.publicServerPort);
        //edt.putString(KEY_FUNCTION_WEB_IP, netConfig.functionWebIP);

        edt.putString(KEY_PROXY_IP, netConfig.proxyIP);
        edt.putString(KEY_PROXY_PORT, netConfig.proxyPort);

        edt.putString(KEY_HM_IP, netConfig.HM_IP);
        edt.putInt(KEY_HM_PORT, netConfig.HM_PORT);
        edt.commit();
    }

    /**
     * 重置配置信息
     */
    public static void clear(Context context) {
        context.getSharedPreferences(FILE_CONFIGK, Context.MODE_MULTI_PROCESS)
                .edit().clear().commit();
    }

}
