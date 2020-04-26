package cn.synway.synmonitor.event.xgupload;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class XGUploadManager {
    private String SERVER_URL = "";
    private String APP_KEY = "";
    private ExecutorService executorService;
    public static String FILE = "file";
    public static String MEMORY = "memory";

    public void ensureExecutor() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
    }

    public void setServerUrl(String serverUrl) {
        this.SERVER_URL = serverUrl;
    }

    public void setAppKey(String appKey) {
        this.APP_KEY = appKey;
    }

    private XGUploadManager() {

    }

    public static XGUploadManager getInstance() {
        return XGUploadManagerHolder.instance;
    }


    private static class XGUploadManagerHolder {
        private static XGUploadManager instance = new XGUploadManager();
    }


    public void checkInternalState() {
        if (this.APP_KEY == null || this.APP_KEY.length() == 0) {
            throw new IllegalStateException("appkey无效");
        }
        if (this.SERVER_URL == null) {
            throw new IllegalStateException("serverUrl无效");
        }
    }

    public void updateFile() {
        checkInternalState();
        ensureExecutor();
        executorService.submit(new XGUploadTask(SERVER_URL, FILE));
        executorService.submit(new XGUploadTask(SERVER_URL, MEMORY));

    }

}
