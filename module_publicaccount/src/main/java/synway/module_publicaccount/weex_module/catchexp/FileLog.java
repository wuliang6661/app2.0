package synway.module_publicaccount.weex_module.catchexp;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import synway.module_interface.config.BaseUtil;

public class FileLog {

    // 日期格式化字符串
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final String UPLOAD_FILE_EXT = ".txt";
    private static final String FILE_EXT = ".log";

    /**
     * 获取今天的,还没上传过的日志文件
     */
    public static final File[] getLogList(String folderName) {
        String folerPath = getFolderPathToday(folderName);
        File folder = new File(folerPath);
        if (!folder.exists()) {
            return null;
        }

        if (!folder.isDirectory()) {
            return null;
        }

        File[] file = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //筛选出文件后缀为 FILE_EXT 的文件
                return pathname.getName().lastIndexOf(FILE_EXT) >= 0;
            }
        });

        return file;
    }

    /**
     * 上传日志文件夹到服务端,这里不会在意上传结果
     *
     * @param IP       服务端IP地址
     * @param port     服务端端口
     * @param IMEI     机身码,用于服务端区分文件夹
     * @param fileList 日志文件
     */
    public static void upload(String IP, int port, String IMEI, File[] fileList) {
        final String url = "http://" + IP + ":" + port + "/SynwayOSCFTP/FTP/CrashLogUpload.osc";
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(3000);// 连接超过3秒就超时
        client.setResponseTimeout(3000);// 请求3秒超时
        for (int i = 0; i < fileList.length; i++) {
            final File file = fileList[i];
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("IMEI", IMEI);
                jsonObj.put("NAME", file.getName());
                jsonObj.put("APPEND", true);
            } catch (Exception e) {
                continue;
            }

            RequestParams params = new RequestParams();
            try {
                params.put("FILE_INFO", jsonObj.toString(), new File(file.getPath()));
            } catch (FileNotFoundException e) {
                continue;
            }

            Log.d("QYC", "上传一次");
            client.post(url, params,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                        }
                        @Override
                        public void onFailure(int arg0, Header[] arg1,
                                              byte[] arg2, Throwable throwable) {
                            Log.d("QYC", "日志上传失败" + throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(int arg0, Header[] arg1,
                                              byte[] arg2) {
                            String oldFilePath = file.getPath();
                            String newFilePath = oldFilePath.replace(FILE_EXT, UPLOAD_FILE_EXT);

                            append(file.getPath(), newFilePath);
                            file.delete();
                            Log.d("QYC", "日志上传成功");
                        }

                        @Override
                        public void onProgress(long bytesWritten,
                                               long totalSize) {
                            super.onProgress(bytesWritten, totalSize);
                        }

                    });
        }
    }

    /**
     * 将旧文件的内容追加到新文件的末尾
     *
     * @param oldFilePath
     * @param newFilePath
     */
    private static void append(String oldFilePath, String newFilePath) {
        File oldFile = new File(oldFilePath);
        if (!oldFile.exists()) {
            return;
        }
        File newFile = new File(newFilePath);
        {
            if (!newFile.exists()) {
                File newFileFolder = new File(newFilePath.replace(newFile.getName(), ""));
                newFileFolder.mkdirs();
            }
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                return;
            }
        }

        RandomAccessFile rfRead = null;
        RandomAccessFile rfWrite = null;
        append:
        {
            try {
                rfRead = new RandomAccessFile(oldFilePath, "rw");
            } catch (FileNotFoundException e) {
                break append;
            }

            try {
                rfWrite = new RandomAccessFile(newFilePath, "rw");
            } catch (FileNotFoundException e) {
                break append;
            }

            try {
                long length = rfWrite.length();
                rfWrite.seek(length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] readBuffer = new byte[1024];
            while (true) {
                int readLength = -1;
                try {
                    readLength = rfRead.read(readBuffer);
                } catch (IOException e) {
                    break append;
                }

                if (readLength <= 0) {
                    break append;
                }

                try {
                    rfWrite.write(readBuffer, 0, readLength);
                } catch (IOException e) {
                    break append;
                }
            }
        }

        //关闭资源
        if (rfRead != null) {
            try {
                rfRead.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (rfWrite != null) {
            try {
                rfWrite.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void write(String folderName, String fileName, String msg) {
        // 建立文件名
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
    public static final String getFolderPathToday(String folderName) {
        return BaseUtil.getFolderPath() + "/" + folderName + "/" + getTime(true);
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
