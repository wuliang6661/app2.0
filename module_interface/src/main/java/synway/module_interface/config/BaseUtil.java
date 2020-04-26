package synway.module_interface.config;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import synway.module_interface.config.userConfig.Sps_RWLoginUser;

public class BaseUtil {

    public static final String Response_result = "RESULT";
    public static final String Response_reason = "REASON";
    public static final String Response_detail = "DETAIL";

    /**
     * sdb/SynwayOSC
     */
    public static final String getFolderPath() {
        return Environment.getExternalStorageDirectory().getPath()
                + "/SynwayOSC";
    }

    /**
     * 获取SD卡的路径，永远存在
     */
    public static final String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 联系人头像 缩略图 地址
     */
    public final static String FILE_HEAD_IMG_THU = getFolderPath()
            + "/HeadImage/Thumbnail";

    /**
     * 联系人头像 原图 地址
     */
    public final static String FILE_HEAD_IMG_ORI = getFolderPath()
            + "/HeadImage/Original";


    /**
     * 更新apk存放地址
     */
    public final static String FILE_APK_PATH = getFolderPath() + "/_APK";

    /**
     * 文件浏览wps存放地址
     */
    public final static String FILE_WPS_APK_PATH = getFolderPath() + "/_WPSAPK";

    /**
     * 公众号 图标, 小
     */
    public final static String FILE_PUBLIC_ACCOUNT_THU = getFolderPath()
            + "/PublicAccount/Thu";

    /**
     * 公众号 图标 ， 大
     */
    public final static String FILE_PUBLIC_ACCOUNT_ORI = getFolderPath()
            + "/PublicAccount/Ori";

    /**
     * 收藏文件路径
     */
    public static class CollectionFileUtil {
        public final static String getVoicePath(String userID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/collection/voice";
        }

        public final static String getVideoPath(String userID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/collection/video";
        }

        public final static String getVideoSmallPath(String userID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/collection/video/small";
        }

        public final static String getPicPath(String userID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/collection/pic";
        }

        public final static String getPicSmallPath(String userID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/collection/pic/small";
        }

        public final static String getMapShotPath(String userID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/collection/map";
        }

        public final static String getOtherPath(String userID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/collection/other";
        }

        public final static String getWordPath(String userID, String url) {
            if (TextUtils.isEmpty(url)) {
                return BaseUtil.getFolderPath() + "/" + userID + "/collection/word";
            }
            else {
                String time = getFileUrlTime(url);
                File file = new File(BaseUtil.getFolderPath() + "/" + userID + "/collection/word/" + time);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return BaseUtil.getFolderPath() + "/" + userID + "/collection/word/" + time;
            }

        }
    }

    public static class ChatFileUtil {
        public final static String getChatPicPath(String userID, String targetID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/"
                    + targetID + "/pic";
        }

        public final static String getChatPicSmallPath(String userID,
                                                       String targetID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/"
                    + targetID + "/pic/small";
        }

        public final static String getChatVideoPath(String userID,
                                                    String targetID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/"
                    + targetID + "/video";
        }

        public final static String getChatVideoSmallPath(String userID,
                                                         String targetID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/"
                    + targetID + "/video/small";
        }

        public final static String getChatVoicePath(String userID,
                                                    String targetID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/"
                    + targetID + "/voice";
        }

        public final static String getChatWordPath(String userID, String targetID, String url) {

            if (TextUtils.isEmpty(url)) {
                return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/" + targetID + "/word";
            }
            else {
                String time = getFileUrlTime(url);
                File file = new File(BaseUtil.getFolderPath() + "/" + userID + "/msgfile/" + targetID + "/word/" + time);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/" + targetID + "/word/" + time;
            }


        }

        public final static String getChatOtherPath(String userID, String fileUrl) {
            if (!TextUtils.isEmpty(fileUrl)) {
                String time = getFileUrlTime(fileUrl);
                File file = new File(BaseUtil.getFolderPath() + "/FuJian" + "/" + userID + "/" + time);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return BaseUtil.getFolderPath() + "/FuJian" + "/" + userID + "/" + time;
            }
            else {
                return BaseUtil.getFolderPath() + "/FuJian" + "/" + userID;
            }
        }

        public final static String getChatMapScreenShotPath(String userID,
                                                            String targetID) {
            return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/"
                    + targetID + "/map";
        }

    }

    /**
     * 获取DING录音文件夹路径
     */
    public static final String getDingRecordFolderPath(String userID) {
        return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/ding/voice";
    }

    /**
     * 获取DING图片文件夹路径
     */
    public static final String getDingPicFolderPath(String userID) {
        return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/ding/pic";
    }

    /**
     * 获取DING视频文件夹路径
     */
    public static final String getDingVideoFolderPath(String userID) {
        return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/ding/video";
    }

    /**
     * 获取DING图片文件夹路径
     */
    public static final String getDingPicSmallFolderPath(String userID) {
        return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/ding/pic/small";
    }

    /**
     * 获取DING视频文件夹路径
     */
    public static final String getDingVideoSmallFolderPath(String userID) {
        return BaseUtil.getFolderPath() + "/" + userID + "/msgfile/ding/video/small";
    }

    /**
     * 获取数据库文件夹路径
     */
    public static final String getDBFolderPath() {
        return BaseUtil.getFolderPath() + "/db";
        // return context.getDatabasePath(dbName).getAbsolutePath();
    }

    public static final String getDBName(String userID) {
        String dbName = "SynwayOSC_" + userID + ".db";
        return dbName;
    }

    public static final String getDBName(Context context) {
        String userID = Sps_RWLoginUser.readUserID(context);
        if (userID == null) {
            return null;
            // ThrowExp.throwRxp("DBUpdate.class" +
            // " -> getDBPath -> userID == null &&&&&&&&&");
        }
        return getDBName(userID);
    }

    /**
     * 获取数据库地址
     */
    public static final String getDBPath(Context context) {
        String userID = Sps_RWLoginUser.readUserID(context);
        if (userID == null) {
            Log.i("liujie", "DBUpdate.class"
                    + " -> getDBPath -> userID == null &&&&&&&&&");
            return null;
            // ThrowExp.throwRxp("DBUpdate.class" +
            // " -> getDBPath -> userID == null &&&&&&&&&");
        }
        return getDBFolderPath() + "/" + getDBName(userID);
    }

    /**
     * 离线地图指定路径
     */
    public final static String OFF_LINE_MAP_PATH = Environment
            .getExternalStorageDirectory() + "/SynwayMap/Gaode/";

    /**
     * 离线地图下载的临时包存储路径
     */
    public final static String OFF_LINE_MAP_DOWN_PATH = OFF_LINE_MAP_PATH
            + "DownLoadTemp/";

    public static String getOffLineMapZIPPath() {

        File file = new File(OFF_LINE_MAP_DOWN_PATH);
        if (!file.exists()) {
            file.exists();
        }
        return OFF_LINE_MAP_DOWN_PATH;

    }

    /**
     * <p>
     * 离线地图解压的路径
     * <p>
     * 这个路径=地图指定路径+API自动附加的路径,目的就是将文件解压到正确的位置
     */
    public final static String OFF_LINE_MAP_UNZIP_PATH = OFF_LINE_MAP_PATH
            + "data_v6/map/";


    /**
     * 获取FileUrl时间
     *
     * @param fileUrl
     * @return
     */
    public static String getFileUrlTime(String fileUrl) {
        String time = "";
        String s = fileUrl.substring(0, fileUrl.lastIndexOf("/"));
        time = s.substring(s.lastIndexOf("/") + 1, s.length());
        return time;
    }
}
