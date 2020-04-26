package synway.module_publicaccount.until;

import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by leo on 2019/1/30.
 */

public class FileTestLog {
    // 日期格式化字符串
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final String FILE_EXT = ".txt";


    public static void write(String fileName, String msg) {
        // 建立文件名
        String folderName = "_配置网站测试";
        String file = creatFile(folderName, fileName);
        if (file == null) {
            return;
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            long length = randomAccessFile.length();
            // 移至文件尾
            randomAccessFile.seek(length);
            // 由于安卓平台默认是GB2312编码,因此要转成Unicode才能被java的IO类识别写入
            StringBuffer str = new StringBuffer(
                "--------------------------------------------------------------------------------------------\r\n");
            str.append(sdf.format(new Date()) + "\r\n");
            str.append(msg.replace("\n", "\r\n"));
            str.append("\r\n");

            randomAccessFile.write(str.toString().getBytes("Unicode"));
            randomAccessFile.close();
        } catch (Exception e) {
            return;
        }

    }

    private static String creatFile(String folderName, String fileName) {
        String pathStr = getFolderPathToday(folderName);

        File pathFile = new File(pathStr);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        String fileStr = pathStr + "/" + fileName + FILE_EXT;
        File file = new File(fileStr);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }

        return fileStr;
    }

    /**
     * 获取今天的日志文件夹
     */
    private static final String getFolderPathToday(String folderName) {
        return Environment.getExternalStorageDirectory().getPath() + "/" + folderName + "/" + getTime(true);
    }

    /**
     * 获取当前时间
     *
     * @param isOnlyData true表示返回日期格式2010-09-19 false表示返回时间格式2010-09-19 08:30:00
     * @return
     */
    private static final String getTime(boolean isOnlyData) {
        String timeStr = null;
        if (isOnlyData) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            timeStr = sdf.format(new Date());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            timeStr = sdf.format(new Date());
        }
        return timeStr;
    }
}
