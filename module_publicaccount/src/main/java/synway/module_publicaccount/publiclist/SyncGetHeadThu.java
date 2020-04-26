package synway.module_publicaccount.publiclist;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import qyc.library.tool.http.HttpDownLoad;
import qyc.library.tool.http.HttpDownLoad.OnHttpDownLoad;
import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.until.FileTestLog;

public class SyncGetHeadThu {

    private HttpDownLoad httpDownLoad = null;
    private String urlHead = null;
    private Handler handler = null;
    private String httpIP;
    private int httpPort;

    public SyncGetHeadThu(String httpIP, int httpPort, Context context) {
        handler = new Handler(context.getMainLooper());
        // 建立缩略图文件夹

        this.httpIP = httpIP;
        this.httpPort = httpPort;
        urlHead = "http://" + httpIP + ":" + httpPort + "/" + "publicFuncImages/Public_";
        this.httpDownLoad = new HttpDownLoad(100);
        this.httpDownLoad.setOnHttpDownloadListen(onHttpDownLoad);
    }

    /**
     * <p>
     * 这里start可以调用多次，每次调用都加入头像下载；但是正在下载的头像不会重复下载，它根据ID来区分；如果头像文件存在就不去下载了。
     * <p>
     * start调用多次没有违反模型好吗？！
     * <p>
     */
    public void start(String id) {
//		Log.i("testy","下载头像"+id);
        // 如果头像文件存在就不要去下了
        File filePath = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (id));
        if (filePath.exists()) {
            if (onGetHeadSmall != null) {
                onGetHeadSmall.onHeadGet(id);
            }
        } else {
//			String urlStr = urlHead + id + "_small";
            String urlStr = "";
            if (id != null && !id.equals("")) {
                if (id.contains(".")) {
                    urlStr = "http://" + httpIP + ":" + httpPort + "/" + "publicFuncImages/" + id;
                } else {
                    urlStr = urlHead + id;
                }
                Log.i("testy", "得到的图片下载地址" + urlStr);
                httpDownLoad.startDownLoad(urlStr, BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (id), id);
            }

        }
    }

    public void start(ArrayList<String> idList) {
        for (int i = 0; i < idList.size(); i++) {
            start(idList.get(i));
        }
    }


    /*****
     *
     * @param publicid 公众号或者菜单ID
     * @param picid  图片的ID
     *  新修改 以公众号ID来命名图片名称，下载的时候图片以公众号的ID来命名，而不是图片自己的ID
     * 下载的时候  要先删除原来有公众号ID的图片
     * 然后再下载新的图片
     */
    public void startPublicIdIcon(String publicid, String picid) {
        File filePath = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (publicid));
        if (filePath.exists()) {//如果存在该公众号ID的头像，先删除
            filePath.delete();
        }
        //下载图片
        String urlStr = "";
        if (picid != null && !picid.equals("")) {
            if (picid.contains(".")) {
                urlStr = "http://" + httpIP + ":" + httpPort + "/" + "publicFuncImages/" + picid;
            } else {
                urlStr = urlHead + picid;
            }
            Log.i("testy", "得到的图片下载地址" + urlStr);
            FileTestLog.write("ImageLog.txt", urlStr);
            httpDownLoad.startDownLoad(urlStr, BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (publicid), publicid);
        }
    }

    /***
     *
     * @param picidPublicidBeens  公众号或者菜单对应的服务端图片id的列表
     */
    public void startPublicIdIcon(ArrayList<PicidPublicidBean> picidPublicidBeens) {
        for (int i = 0; i < picidPublicidBeens.size(); i++) {
            startPublicIdIcon(picidPublicidBeens.get(i).publicid, picidPublicidBeens.get(i).picid);
        }
    }


    /**
     * 停止
     */
    public void stop() {
        this.onGetHeadSmall = null;
        this.httpDownLoad.stopDownLoad();
    }

    private OnHttpDownLoad onHttpDownLoad = new OnHttpDownLoad() {

        @Override
        public void onStart(Object key, long downloadLength, File filePath) {

        }

        @Override
        public void onProgressChanged(Object key, int progress) {

        }

        @Override
        public void onFinish(Object key, boolean result, String msg, File filePath) {
            if (result) {
                final String id = (String) key;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onGetHeadSmall != null) {
                            onGetHeadSmall.onHeadGet(id);
                        }
                    }
                });
            } else {

            }
        }
    };

    public void setOnListen(IOnGetHeadThu onGetHeadSmall) {
        this.onGetHeadSmall = onGetHeadSmall;
    }

    private IOnGetHeadThu onGetHeadSmall = null;

    public interface IOnGetHeadThu {
        void onHeadGet(String ID);

        void onFailGet(String ID);
    }
}
