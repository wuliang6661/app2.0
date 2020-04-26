package synway.module_publicaccount.public_chat.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_publicaccount.Main;

/**
 * Created by QSJH on 2016/6/8 0008.
 */
public abstract class PublicAccountURL {
    public static final int URL_TYPE_GPS = 0;// GPS类型
    public static final int URL_TYPE_SUPERLINK = 1;// 超链接类型
    public static final int URL_TYPE_MEDIA = 2;// 流媒体类型
    public static final int URL_TYPE_RTMP = 3;// 流媒体类型

    public abstract int getUrlType();

    public abstract boolean createFromUrl(String url);


    /**
     * 跳转到地图，包含经纬度
     */
    public static class URL_GPS extends PublicAccountURL {

        public static final String tag = "HXPositionURL://";
        public String lon;
        public String lat;
        public ArrayList<URL_GPS_ITEM> gpsList = null;

        @Override
        public int getUrlType() {
            return URL_TYPE_GPS;
        }

        @Override
        public boolean createFromUrl(String url) {
            String str = url.replace(tag, "");
            if(str.contains("[")) {
                // 新gps传输协议，json ,例如：[{"LNG":56.36,"TEXT":"位置信息","LAT":118.23}]
                try {
                    JSONArray array = new JSONArray(str);
                    gpsList = new ArrayList<>();
                    for(int i=0 ; i < array.length() ;i++) {
                        JSONObject object = array.getJSONObject(i);
                        URL_GPS_ITEM item = new URL_GPS_ITEM();
                        item.setLat(object.getString("LAT"));
                        item.setLon(object.getString("LNG"));
                        gpsList.add(item);
                    }
                } catch (JSONException e) {
                    return false;
                }
                return true;
            } else {
                // 旧协议: 例如: 56.36:118.23
                String strs[] = str.split(":", -1);
                if (strs.length < 2) {
                    return false;
                }
                lon = strs[0];
                lat = strs[1];
                return true;
            }
        }

    }

    /**
     * 普通超链接，通过webview跳转，默认类型
     */
    public static class URL_SUPERLINK extends PublicAccountURL {
        public String superLinkUrl;
        public String name;

        @Override
        public int getUrlType() {
            return URL_TYPE_SUPERLINK;
        }

        @Override
        public boolean createFromUrl(String url) {
            superLinkUrl = url;
            return true;
        }
    }

    /**
     * 流媒体类型，跳转到流媒体播放器
     */
    public static class URL_MEDIA extends PublicAccountURL {
        public static final String tag = "HXVoiceURL://";
        public String rtmpUrl = "rtmp://%s:%s/live/%s";
        public String postUrl = "http://%s:%s/%s/%s?audio1=%s&audio2=%s&mode=1&mode_opt=start";
        public String name = "";


        @Override
        public int getUrlType() {
            return URL_TYPE_MEDIA;
        }

        @Override
        public boolean createFromUrl(String url) {
            String str = url.replace(tag, "");
            String strs[] = str.split(":", -1);
            if (strs.length < 2) {
                return false;
            }
            String mainFile = strs[0];// 主叫文件路径
            String mainFileName = getFileName(mainFile);
            String subFile = strs[1];// 被叫文件路径
            String subFileName = getFileName(subFile);

            String path = getDir(mainFile);
            String uuid = UUID.randomUUID().toString();


            NetConfig netConfig = Sps_NetConfig.getNetConfigFromSpf(Main.instance().context);
            String stream_name =  uuid;

            String audioIP = netConfig.audioIP;
            int audioPort = netConfig.audioPort;
            String zhiboIP = netConfig.videoIP;
            int zhiboPort = netConfig.videoPort;


            postUrl = String.format(postUrl, audioIP, audioPort,path, stream_name, mainFileName, subFileName);
            rtmpUrl = String.format(rtmpUrl, zhiboIP, zhiboPort, stream_name);
            return true;
        }



        private String getDir(String filePath) {
//            filePath = "yuyin\\20050205\\200502051353178613627871184XAA.pcm";
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            return filePath;
        }

        private String getFileName(String filePath) {
            return filePath.substring(filePath.lastIndexOf("/")+1);
        }
    }

    public static class URL_RTMP extends PublicAccountURL{
        public static final String tag = "hxrtmpurl://";
//        public static final String tag = "rtmp://";
        public String rtmpUrl = "";

        @Override
        public int getUrlType() {
            return URL_TYPE_RTMP;
        }

        @Override
        public boolean createFromUrl(String url) {
            rtmpUrl= cutStrHead(url,PublicAccountURL.URL_RTMP.tag);
//            rtmpUrl=url.replace(tag,"");
            return true;
        }



    }

    /** 把head剪掉 */
    private static final String cutStrHead(String str, String head) {

        Pattern pattern = Pattern.compile(head, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        // 替换第一个符合正则的数据
        return matcher.replaceFirst("");

    }

    /**
     * 新GPS协议，多个点
     */
    public static class URL_GPS_ITEM {
        private String text; // 文字描述
        private String lon;
        private String lat;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }
}
