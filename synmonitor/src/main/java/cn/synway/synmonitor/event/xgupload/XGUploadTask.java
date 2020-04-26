package cn.synway.synmonitor.event.xgupload;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.synway.synmonitor.event.eventcache.CacheManager;
import cn.synway.synmonitor.event.eventmodel.BaseEvent;
import cn.synway.synmonitor.logutil.LogUtil;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class XGUploadTask implements Runnable {
    private String serverUrl;
    // private String deviceID;
    private String dataSourceType;
    //  private String sessionData;
    //private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String salt;

    public XGUploadTask(String serverUrl, String dataSourceType) {
        this.serverUrl = serverUrl;
        //this.deviceID = DeviceInfo.getInstance().getDeviceId(Config.context);
        this.dataSourceType = dataSourceType;
        //   this.sessionData = sessionData;
    }


    public void sendCacheData(List<BaseEvent> memoryEvent) {
        String data = new Gson().toJson(memoryEvent);
        LogUtil.e("Task", data);
        if (TextUtils.isEmpty(data)) {
            return;
        }
        String postBody = data;
        Request request = new Request.Builder().url(this.serverUrl).post(RequestBody.create(OKHttpFactory.MEDIA_TYPE_JSON, postBody)).build();
        try {
            Response response = OKHttpFactory.getInstance().getOkHttpClient().newCall(request).execute();
            if (response != null) {
                if (response.isSuccessful()) {
                    CacheManager.getInstance().clearMemory(memoryEvent);
                    LogUtil.e("CacheData Successful", data);
                }
                else {

                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFileData(File file) {
        if (file == null) {
            return;
        }
        Request request = new Request.Builder().url(this.serverUrl).post(RequestBody.create(OKHttpFactory.MEDIA_TYPE_TEXT, file)).build();
        try {
            Response response = OKHttpFactory.getInstance().getOkHttpClient().newCall(request).execute();
            if (response != null) {
                if (response.isSuccessful()) {
                    LogUtil.e("monitor", "update File Success!!");
                    CacheManager.getInstance().deleteFile(file);
                }
                else {
                    LogUtil.e("monitor", "update File Filed!!");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            file.delete();
        }
    }

    @Override
    public void run() {
        if (XGUploadManager.MEMORY.equalsIgnoreCase(dataSourceType)) {
            List<BaseEvent> memoryEvent = CacheManager.getInstance().getMemoryEvent();
            if (memoryEvent.size() == 0) {
                LogUtil.e("memoryEvent","empty");
                return;
            }

            sendCacheData(memoryEvent);
        }
        else if (XGUploadManager.FILE.equalsIgnoreCase(dataSourceType)) {
            File[] files = CacheManager.getInstance().getFileEvent();
            if (files == null || files.length == 0) {
                LogUtil.e("files","empty");
                return;
            }
            for (File file : files) {
                sendFileData(file);
            }
        }
    }
}
