package cn.synway.app.config;

import android.os.Environment;

import java.io.File;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/1910:29
 * desc   :  本地文件存储目录
 * version: 1.0
 */
public class FileConfig {


    /**
     * 所有文件的父级目录
     */
    public static String getBaseFile() {
        return Environment.getExternalStorageDirectory().getPath()
                + "/SynTerrace";
    }


    /**
     * 存储崩溃日志的文件夹
     */
    public static String getLogFile() {
        return getBaseFile() + File.separator + "ExpLog";
    }


    /**
     * 存储聊天文件的文件夹
     */
    public static String getIMFile() {
        return getBaseFile() + File.separator + "IMFile";
    }


    /**
     * 存储离线地图文件的路径
     */
    public static String getMapFile() {
        return getBaseFile() + File.separator + "MapFile";
    }


    /**
     * 存储更新apk 的文件夹
     */
    public static String getApkFile() {
        return getBaseFile() + File.separator + "APKFile";
    }

}
