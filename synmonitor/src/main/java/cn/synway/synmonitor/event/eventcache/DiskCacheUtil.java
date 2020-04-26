package cn.synway.synmonitor.event.eventcache;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import cn.synway.synmonitor.config.Config;
import cn.synway.synmonitor.event.eventmodel.BaseEvent;


class DiskCacheUtil {
    public static final String PREFERENCES = "XGEVENTS_STORE";
    private String FILESUFFIX = ".dat";


    private DiskCacheUtil() {

    }


    private static class DiskCacheUtilHolder {
        private static DiskCacheUtil instance = new DiskCacheUtil();
    }

    public static DiskCacheUtil getInstance() {
        return DiskCacheUtilHolder.instance;
    }

    public synchronized void addMemoryCacheEvent(List<BaseEvent> list) {
        if (list == null || list.size() == 0 || Config.context == null) {
            return;
        }

        File persistentDirFile = new File(Config.context.getFilesDir().toString() + File.separator + DiskCacheUtil.PREFERENCES);
        if (!persistentDirFile.exists()) {
            persistentDirFile.mkdirs();
        }

        try {
            String[] names = persistentDirFile.list();
            List<String> existsFileNameList = Arrays.asList(names);

            int i = 0;
            while (true) {
                if (existsFileNameList.contains(i + FILESUFFIX)) {
                    i++;
                }
                else {
                    String data = new Gson().toJson(list);

                    File file = new File(persistentDirFile.toString() + File.separator + i + FILESUFFIX);
                    if (!file.exists()) {
                        file.createNewFile();
                        FileOutputStream fileWriter = new FileOutputStream(file);
                        OutputStreamWriter writer = new OutputStreamWriter(fileWriter, "UTF-8");
                        writer.write(data);
                        writer.flush();
                        writer.close();
                    }
                    break;
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public synchronized File[] getFileData() {
        if (Config.context == null) {
            return null;
        }
        File[] files = null;
        File persistentDirFile = new File(Config.context.getFilesDir().toString() + File.separator + DiskCacheUtil.PREFERENCES);
        if (!persistentDirFile.exists()) {
            return files;
        }

        files = persistentDirFile.listFiles();
        return files;
    }

    public synchronized void deleteFile(File file) {
        file.delete();
    }
}
