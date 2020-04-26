package synway.module_publicaccount.public_chat.file_upload;

import android.os.Handler;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.http.Header;
import org.json.JSONObject;
import synway.common.upload.SynUpload;

/**
 * 仅仅支持汇信的文件服务器的非阻塞式的文件上传工具<p>
 * 支持停止上传,支持批量上传，大文件不会内存溢出，不支持文件夹上传,目前服务端暂不支持无后缀名文件接收，所以不支持无后缀名文件上传<p>
 * 生命周期：FileUploadTool()初始化，startUpload()开始一次上传，stop()停止上传,destory（）结束
 * Created by dell on 2016/8/18.
 */
public class FileUploadTool {
    private Handler handler;
    /**
     * 超时时间
     */
    private int outTime = 3 * 60;

    /**
     * 观察者列表
     */
    private List<FileUploadLsn> fileUploadLsnLIist;

    /**
     * 保存每一次上传的RequestHandle，用以取消上传
     */
    private HashMap<Object, RequestHandle> uploadList;

    /**
     * 默认超时为3秒
     */
    public FileUploadTool() {
        handler = new Handler();
        fileUploadLsnLIist = new ArrayList<>();
        uploadList = new HashMap<>();
    }

    public FileUploadTool(int outTime) {
        handler = new Handler();
        fileUploadLsnLIist = new ArrayList<>();
        uploadList = new HashMap<>();
        this.outTime = outTime;
    }

    /**
     * 开始上传一个文件
     *
     * @param filePath 文件地址
     * @param url      上传地址
     * @param tag      标记本次上传的tag
     */
    public void startUpload(String filePath, String url, Object tag) {
        MyRunnable myRunnable = new MyRunnable(handler, filePath, url, tag);
        myRunnable.send();
//        new Thread(new UpdateRunnable(url, filePath, tag)).start();

    }

    private class  UpdateRunnable implements Runnable{

        private String urlStr,path;
        private Object tag;
        private UpdateRunnable(String urlStr,String path,Object tag)
        {
            this.urlStr = urlStr;
            this.path=path;
            this.tag=tag;
        }
        @Override
        public void run() {
            String[] result= SynUpload.uploadFile(urlStr,path,getSuffix(path), getTypeFromSuffix(getSuffix(path)), UUID.randomUUID().toString());
            try {
                Log.i("testy","得到的上传结果是"+result);
            } catch (Exception e) {
            }

        }
    }
    /**
     * 开始上传一个文件
     *
     * @param filePath 文件地址
     * @param url      上传地址
     * @return 返回一个默认的UploadObj类型的tag用来标记本次上传，tag里面包含了filePath和url
     */
    public UploadObj startUpload(String filePath, String url) {
        UploadObj tag = new UploadObj();
        tag.filepath = filePath;
        tag.url = url;
        startUpload(filePath, url, tag);
        return tag;
    }

    /**
     * 开始上传一个文件
     *
     * @param FilePath 需要上传的文件的地址集合
     * @param url      上传地址
     * @return 返回一个默认的UploadObj类型的tag集合，tag里面包含了filePath和url
     */
    public List<UploadObj> startUpload(List<String> FilePath, String url) {
        Log.i("testy","得到的文件地址和上传的网址"+FilePath+url);
        List<UploadObj> uploadObjs = new ArrayList<>();
        for (String s : FilePath) {
            uploadObjs.add(startUpload(s, url));
        }
        return uploadObjs;
    }

    /**
     * 停止tag对应的某次上传
     */
    public void stopByTag(Object tag) {
        RequestHandle requestHandle = uploadList.remove(tag);
        if (requestHandle != null) {
            requestHandle.cancel(false);
        }
    }

    /**
     * 停止所有上传
     */
    public void stopAll() {
        Iterator iter = uploadList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            RequestHandle val = (RequestHandle) entry.getValue();
            val.cancel(false);
        }
    }

    /**
     * 销毁，停止所有上传，并移除所有观察者
     */
    public void destory() {
        removeListenAll();
        stopAll();
    }


    /**
     * 添加观察者
     */
    public void addListen(FileUploadLsn fileUploadLsn) {
        fileUploadLsnLIist.add(fileUploadLsn);
    }

    /**
     * 移除观察者
     */
    public void removeListen(FileUploadLsn fileUploadLsn) {
        fileUploadLsnLIist.remove(fileUploadLsn);
    }

    /**
     * 移除所有观察者
     */
    public void removeListenAll() {
        fileUploadLsnLIist = null;
    }

//    public interface FileUploadLsn {
//        void onStart(Object o);
//
//        void onError(Object o, int errorCode, String errorMsg);
//
//        void onFinish(Object o);
//
//        void onCancle(Object o);
//
//        void onProgress(Object o, int progress,long uploaded);
//    }


    /**
     * 文件上传对象，需要在主线程执行<p>
     * 作用域：新建一次上传，并将上传会话保存到队列中，反馈（上传开启，上传进度，上传错误，上传完成，上传取消）。
     */
    class MyRunnable {
        private RequestHandle requestHandle = null;
        private Handler handler = null;
        private String url = null;
        private AsyncHttpClient client = null;
        private RequestParams params = null;
        private Object tag;
        private int lastProgress = 0;

        public MyRunnable(Handler handler, String filePath, String url, Object tag) {
            this.handler = handler;
            this.url = url;
            this.tag = tag;

            client = new AsyncHttpClient();
            client.setConnectTimeout(180000);// 连接超过3分钟就超时
            client.setResponseTimeout(outTime * 1000);// 请求超过outTime秒超时

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("FILE_EXT", getSuffix(filePath));
                jsonObject.put("TYPE", getTypeFromSuffix(getSuffix(filePath)));
                jsonObject.put("FILE_NAME", getFilename(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
             params = new RequestParams();
            try {
                params.put("FILE_INFO", jsonObject.toString(), new File(filePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void send() {
            handler.post(new Runnable() {
                public void run() {
                    requestHandle = client.post(url, params,
                            new AsyncHttpResponseHandler() {
                                @Override
                                public void onStart() {
                                    super.onStart();
                                    uploadList.put(tag, requestHandle);
                                    for (FileUploadLsn fileUploadLsn : fileUploadLsnLIist) {
                                        fileUploadLsn.onStart(tag);
                                    }
                                }

                                @Override
                                public void onFailure(int arg0, Header[] arg1,
                                                      byte[] arg2, Throwable throwable) {
                                    String errorMsg="";
                                    try {
                                        if(arg2!=null) {
                                            errorMsg = new String(arg2, "UTF-8");
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    uploadList.remove(tag);
                                    for (FileUploadLsn fileUploadLsn : fileUploadLsnLIist) {
                                        fileUploadLsn.onError(tag,arg0,errorMsg);
                                    }
                                }

                                @Override
                                public void onSuccess(int arg0, Header[] arg1,
                                                      byte[] arg2) {
                                    uploadList.remove(tag);
                                    for (FileUploadLsn fileUploadLsn : fileUploadLsnLIist) {
                                        fileUploadLsn.onFinish(tag,new String(arg2));
                                    }

                                }

                                @Override
                                public void onProgress(long bytesWritten,
                                                       long totalSize) {
                                    super.onProgress(bytesWritten, totalSize);
                                    int progress = (int) (bytesWritten * 100 / totalSize);
                                    if (progress != lastProgress&&progress<=100) {
                                        lastProgress = progress;
                                        for (FileUploadLsn fileUploadLsn : fileUploadLsnLIist) {
                                            fileUploadLsn.onProgress(tag, progress,bytesWritten);
                                        }
                                    }

                                }

                                @Override
                                public void onCancel() {
                                    super.onCancel();
                                    uploadList.remove(tag);
                                    if (fileUploadLsnLIist != null) {
                                        for (FileUploadLsn fileUploadLsn : fileUploadLsnLIist) {
                                            fileUploadLsn.onCancle(tag);
                                        }
                                    }
                                }
                            });
                }

            });
        }
    }

    /**
     * 1=图片 2=视频 3=声音 4=其他
     */
    private static int getTypeFromSuffix(String suffix) {
        //当文件后缀名为大写的 .JPG .PNG 格式的情况,需要转小写进行比对,不然会识别为附件格式
        suffix = suffix.toLowerCase();
        if (suffix.equals("png")) {
            return 1;
        } else if (suffix.equals("jpg")) {
            return 1;
        } else if (suffix.equals("bmp")) {
            return 1;
        } else if (suffix.equals("amr")) {
            return 3;
        } else if (suffix.equals("mp3")) {
            return 3;
        } else if (suffix.equals("wav")) {
            return 3;
        } else if (suffix.equals("3gp")) {
            return 2;
        } else if (suffix.equals("mp4")) {
            return 2;
        } else {
            return 4;
        }

    }

    /**
     * 获取后缀名，不带“.”
     */
    private static String getSuffix(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 获取文件名,去后缀
     */
    private static String getFilename(String filepath) {
        String filename  =filepath.substring(filepath.lastIndexOf("/")+1);
        if(filename.indexOf(".")>=0){
            filename = filename.substring(0,filename.indexOf("."));
        }
        return filename;
    }
}
