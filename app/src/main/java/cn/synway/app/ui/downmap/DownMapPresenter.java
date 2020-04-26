package cn.synway.app.ui.downmap;

import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cn.synway.app.api.DownloadResponseBody;
import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.base.SynApplication;
import cn.synway.app.bean.MyAreaDO;
import cn.synway.app.bean.request.DownMapRequest;
import cn.synway.app.config.FileConfig;
import cn.synway.app.mvp.BasePresenterImpl;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class DownMapPresenter extends BasePresenterImpl<DownMapContract.View> implements DownMapContract.Presenter {
    private Set<String> hashSet = new HashSet<String>(SynApplication.spUtils.getStringSet(MyAreaDO.SP_NAME_KEY, new HashSet<String>()));
    //数据丢失
    //private Set<String> hashSet =SPUtils.getInstance().getStringSet(MyAreaDO.SP_NAME_KEY, new HashSet<String>());

    @Override
    public void getMapInfo() {
        HttpServerImpl.getMapInfo().subscribe(new HttpResultSubscriber<List<MyAreaDO>>() {
            @Override
            public void onSuccess(List<MyAreaDO> list) {

                if (mView != null) {
                    ArrayList<MyAreaDO> arrayList = converList(list);
                    mView.getData(arrayList);
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
                ToastUtils.showShort(message);
            }
        });
    }

    /**
     * 整理数据
     *
     * @param list
     * @return
     */
    private ArrayList<MyAreaDO> converList(List<MyAreaDO> list) {
        ArrayList<MyAreaDO> arr = new ArrayList<>();

        arr.add(new MyAreaDO("概要图", true));
        MyAreaDO areaDO = new MyAreaDO("省市", true);
        arr.add(areaDO);
        for (MyAreaDO m : list) {
            if ("概要图".equals(m.getName())) {
                arr.addAll(1, m.getChild());
            } else {
                arr.add(1 + arr.indexOf(areaDO), m);
            }
        }
        return arr;
    }

    /**
     * 获取下载地址
     *
     * @param entity
     */
    @Override
    public void downMap(MyAreaDO entity) {
        DownMapRequest downMapRequest = new DownMapRequest();
        downMapRequest.name = entity.getName();
        downMapRequest.father = entity.getFather();

        //防止多次点击或无效点击
        if (entity.getState() != 0) {
            return;
        }
        entity.setState(1);
        entity.setPercentage("0");
        updateViewByEntity(entity);
        HttpServerImpl.getDownMap(downMapRequest).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String url) {
                if (mView != null) {
                    downLoad(url, entity);
                }
            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShort(message);
            }
        });
    }


    /**
     * 下载地图
     *
     * @param url
     * @param entity
     */
    private void downLoad(String url, MyAreaDO entity) {
        String pingYin = entity.getPingYinName();
        File file = new File(FileConfig.getMapFile(), pingYin + ".zip");
        boolean existsFile = FileUtils.createOrExistsFile(file);
        if (!existsFile || pingYin == null) {
            ToastUtils.showShort("IO异常");
            entity.setState(3);
            updateViewByEntity(entity);
            return;
        }

        /**
         *观察者处于工作线程中，更新UI要发送到主线程
         */
        DownloadResponseBody.DownloadListener downloadListener = new DownloadResponseBody.DownloadListener() {
            @Override
            public void onProgress(String progress) {
                LogUtils.e("progress = " + progress);
                entity.setState(2);
                entity.setPercentage(progress);
                updateViewByEntity(entity);
            }
        };

        HttpServerImpl.downLoad(url, downloadListener, file).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.e("onCompleted", Thread.currentThread().getName());
                        try {
                            entity.setState(5);
                            updateViewByEntity(entity);
                            unZip(file.getAbsolutePath());
                            entity.setState(6);
                            updateViewByEntity(entity);
                            hashSet.add(entity.getPingYinName());
                            SynApplication.spUtils.put(MyAreaDO.SP_NAME_KEY, hashSet);
                        } catch (Exception e) {
                            e.printStackTrace();
                            entity.setState(7);
                            updateViewByEntity(entity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort(e.getMessage());
                        entity.setState(3);
                        LogUtils.e("onError", Thread.currentThread().getName());
                        updateViewByEntity(entity);
                    }

                    @Override
                    public void onNext(ResponseBody integer) {
                        entity.setState(8);
                        updateViewByEntity(entity);

                    }

                });


    }

    /**
     * 通过线程调度发送到主线成
     *
     * @param entity
     */
    private void updateViewByEntity(MyAreaDO entity) {

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onCompleted();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                if (mView != null) {
                    mView.updateItemState(entity);
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer integer) {

            }
        });

    }


    /**
     * 解压文件的方法
     *
     * @param unZipfileName 文件名（包括路径）
     */
    private void unZip(String unZipfileName) throws Exception {
        String mDestPath = FileConfig.getMapFile();
        File destination = new File(mDestPath);
        if (!destination.exists()) {
            destination.mkdirs();
        }
        FileOutputStream fileOut = null;
        ZipInputStream zipIn = null;
        ZipEntry zipEntry = null;
        File file = null;
        int readedBytes = 0;
        byte buf[] = new byte[4096];
        try {
            zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(unZipfileName)));

            while ((zipEntry = zipIn.getNextEntry()) != null) {
                file = new File(mDestPath + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // 如果指定文件的目录不存在,则创建之.
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    while ((readedBytes = zipIn.read(buf)) > 0) {
                        fileOut.write(buf, 0, readedBytes);
                    }
                    fileOut.close();
                }
                zipIn.closeEntry();
            }
            File file2 = new File(unZipfileName);
            if (file2.exists()) {
                file2.delete();
                Log.i("lmly", "删除原文件");
            }
            Log.i("lmly", "解压success");
        } catch (Exception ioe) {
            Log.i("lmly", "解压失败，原因为：" + ioe.toString());
            throw new Exception();
        }
    }


}
