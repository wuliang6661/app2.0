package synway.module_publicaccount.public_chat.file_upload_for_camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPickerActivity;
import qyc.library.control.dialog_msg.DialogMsg;
import qyc.tool.qjob.QJobLoader;
import qyc.tool.qjob.QJobTask;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.WebviewFn.camera.PublicAccountCameraNew;
import synway.module_publicaccount.public_chat.file_upload_for_camera.album.PictureEditClick;
import synway.module_publicaccount.public_chat.file_upload_for_camera.album.Ysmcameraclick;
import synway.module_publicaccount.public_chat.file_upload_for_camera.entity.MediaFile;
import synway.module_publicaccount.public_chat.file_upload_for_camera.entity.UploadFiles;
import synway.module_publicaccount.public_chat.file_upload_for_camera.jobs.UploadProgress;
import synway.module_publicaccount.public_chat.file_upload_for_camera.jobs.UploadprogressFiles;
import synway.module_publicaccount.until.StringUtil;

import static android.app.Activity.RESULT_OK;

/**
 * 拍照与上传管理类：
 * 由于公众号支持创建桌面快捷方式，会出现多个公众号调用此方法，同名广播会产生混乱
 * 因此需要在每次初始化时随机出一个广播种子，所有的广播加上种子后缀，防止冲突。
 * Created by 朱铁超 on 2018/11/30.
 */
public class PicUploadManager {

    private static final String TAG = PicUploadManager.class.getSimpleName();

    public static final int FOR_WEEX = 1;//weex平台调用
    public static final int FOR_H5 = 2;//H5平台调用

    public static final int ONLY_CAMERA = 133;//

    //使用相机获得图片
    public static final int BY_CAMERA = 1;

    //使用纯相册获得图片
    public static final int BY_ONLY_ALBUM = 2;
    //使用相册+相机获得图片
    public static final int BY_ALBUM = 3;

    public static final int SYN_START = 1;//同步调用
    public static final int ASY_START = 2;//异步调用

    private ManagerListen mManagerListen = null;
    //    //返回照片名称参数
    private static final String CAMERA_BIGPATH_PARM = "send_picture_bigPath";

    //拍照后返回照片名，携带种子
    private String BROADCAST_CAMERA_BACK_RANDOM = "send_picture";
    //阻塞队列
    public ArrayBlockingQueue<String> queue = null;
    //种子，随机生成，防止广播冲突
    private int seed = 0;

    private QJobLoader qJobLoader = null;
    private CameraBroadcastReceiver cameraBroadcastReceiver = null;
    private Context mContext = null;

    private MaterialDialog cancelUploadDialog = null;
    private ProgressDialog uploadDialog;
    private BaseAnimatorSet mBasIn = null;
    private BaseAnimatorSet mBasOut = null;

    //同一时刻只能使用一种且一个上传(不论同步异步)，等一个上传执行完后，才能执行另一个
    private boolean isStart = false;
    private int startType = 0;//拍照启动模式1 同步调用 2 异步调用
    private int platform = 0; //平台类型

    //异步上传进度以及取消
    private View progressImgs;
    private TextView tv_pic_progress1, tv_pic_progress2, tv_pic_cancel;


    public PicUploadManager(Context mContext) {
        this.mContext = mContext;
        init();
    }

    public void setManagerListen(ManagerListen managerListen) {
        this.mManagerListen = managerListen;
    }

    private NetConfig netConfig = null;

    /**
     * 初始化控制类
     */
    private void init() {
        initSeed();
        netConfig = Sps_NetConfig.getNetConfigFromSpf(mContext);
        registerStartCamera();
        reLoadJob();
        //显示错误信息任务
    }

    /**
     * 初始化异步上传的进度条
     */
    public void initAsyUI() {
        if(mContext!=null){
            progressImgs = ((Activity) mContext).findViewById(R.id.progress_imgs);
            tv_pic_progress1 = ((Activity) mContext).findViewById(R.id.tv_pic_progress1);
            tv_pic_progress2 = ((Activity) mContext).findViewById(R.id.tv_pic_progress2);
            tv_pic_cancel = ((Activity) mContext).findViewById(R.id.tv_pic_cancel);
            tv_pic_cancel.setOnClickListener(onClickListener);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showCancelUploadDialog();
        }
    };

    private void reLoadJob() {
        if (qJobLoader == null) {
            qJobLoader = new QJobLoader(mContext, null, "PUPDATE");
            //显示错误信息任务
            qJobLoader.loadJob("ShowError", qJobShowError);
        } else {
            qJobLoader.removeJob("Upload2");
        }
        qJobLoader.loadJob("Upload2", new UploadprogressFiles(netConfig.ftpIP, netConfig.ftpPort, "ShowError", "jpg", new UploadProgress() {
            @Override
            public void progress(final int maxItem, final int currentItem, final long max, final float curProgress) {

               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (startType == ASY_START) {
                                if (maxItem != -1 && currentItem != -1) {
                                    if (progressImgs!=null&&progressImgs.getVisibility() == View.GONE) {
                                        progressImgs.setVisibility(View.VISIBLE);
                                    }
                                    if(tv_pic_progress1!=null){
                                        tv_pic_progress1.setText("正在上传第" + currentItem + "张图片(共" + maxItem + "张)");
                                    }
//                                    mManagerListen.uploadProgress1("正在上传第" + currentItem + "张图片(共" + maxItem + "张)");
                                }
                                if (max != -1) {//正式进度
                                    if(tv_pic_progress2!=null){
                                        tv_pic_progress2.setText((int) (100 * curProgress) + "%");
                                    }
//                                    mManagerListen.uploadProgress2((int) (100 * curProgress) + "%");
                                }
                        } else if (startType == SYN_START) {
                            if (uploadDialog == null) return;
                            if (maxItem != -1 && currentItem != -1) {
                                uploadDialog.setMessage("正在上传第" + currentItem + "张图片(共" + maxItem + "张)");
                            }
                            if (max != -1) {//正式进度
                                uploadDialog.setProgress((int) (100 * curProgress));
                            }
                        }
                    }
                });


            }
        }));
        qJobLoader.loadJob("Upload2", new QJobTask<UploadFiles>() {
            @Override
            public boolean onStart(UploadFiles uploadFiles) {
                if (startType == SYN_START) {
                    if (uploadDialog.isShowing()) {
                        uploadDialog.dismiss();
                    }
                    if (cancelUploadDialog != null && cancelUploadDialog.isShowing()) {
                        cancelUploadDialog.dismiss();
                    }
                    showFinishDialog("上传成功");
                }
                String json = JSONObject.toJSONString(uploadFiles.getNetFiles());
                putQueue(json);
//                if (mManagerListen != null) {
//                    mManagerListen.updateBack(json);
//                }
                return false;
            }

        });
    }

    /**
     * 创建一个广播种子
     */
    private void initSeed() {
        seed = (int) (Math.random() * 1000);
        BROADCAST_CAMERA_BACK_RANDOM += seed;
    }

    /**
     * 销毁控制类
     */
    public void destroy() {
        unRegisterStartCamera();
        putQueue("noresult");
        mManagerListen = null;
        if (qJobLoader != null) {
            qJobLoader.removeAll();
            qJobLoader = null;
        }
    }

    private void putQueue(String result) {
        isStart = false;
        if (startType == ASY_START) {//异步采用交互回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressImgs!=null&&progressImgs.getVisibility() == View.VISIBLE) {
                        progressImgs.setVisibility(View.GONE);
                    }
                }
            });
            if (mManagerListen != null) {
                if (StringUtil.isEmpty(result) || result.equals("noresult") || result.equals("cancel") || result.equals("upload_failed")) {
                    mManagerListen.updateBack(null);
                } else {
                    if (FOR_H5 == platform) {
                        List<MediaFile> mediaFiles = JSON.parseArray(result, MediaFile.class);
                        ArrayList<String> arrayList = new ArrayList<>();
                        if (mediaFiles != null && mediaFiles.size() > 0) {
                            for (MediaFile mediaFile : mediaFiles) {
                                arrayList.add(mediaFile.getNetPath());
                            }
                        }
                        if (arrayList.size() > 0) {
                            JSONArray array = new JSONArray();
                            for (String str : arrayList) {
                                array.put(str);
                            }
                            result = array.toString();
                        }
                    }
                    mManagerListen.updateBack(result);
                }

            }
        } else if (startType == SYN_START) {//同步采用直接返回
            if (queue != null && queue.size() == 0) {
                try {
                    queue.put(result);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * 将string返回值进行处理，改变成json类型，增加参数key
     *
     * @param str
     * @return 后期处理
     */
    private String translateToJson(String str) {
        return "";
    }

    //相册界面取消获取图片  或者上传终止
    public void cancelQueue() {
        putQueue("cancel");
    }
    /**
     *
     *
     * @param openWay
     */
    /**
     * 请在非ui线程中调用
     * 开始图片获取以及上传
     *
     * @param openWay  1:相机 2相册 3相册+相机
     * @param platform
     * @return null 上传失败,否则返回url
     */
    public String start(int openWay, int platform) {
        if (isStart) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "请等待所有图片上传结束", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
        isStart = true;
        startType = SYN_START;
        this.platform = platform;
        if (queue == null) {
            queue = new ArrayBlockingQueue<>(1);
        } else {
            queue.clear();
        }
        switch (openWay) {
            case BY_CAMERA:
                takePhone(mContext, seed);
                break;
            case BY_ALBUM:
                openPhotoPicker(true);
                break;
            case BY_ONLY_ALBUM:
                openPhotoPicker(false);
                break;
        }
        String result = null;
        try {
            queue.clear();
            result = queue.take();
            if (StringUtil.isEmpty(result) || result.equals("noresult") || result.equals("cancel") || result.equals("upload_failed")) {
                return null;
            }
            if (FOR_H5 == platform) {
                List<MediaFile> mediaFiles = JSON.parseArray(result, MediaFile.class);
                ArrayList<String> arrayList = new ArrayList<>();
                if (mediaFiles != null && mediaFiles.size() > 0) {
                    for (MediaFile mediaFile : mediaFiles) {
                        arrayList.add(mediaFile.getNetPath());
                    }
                }
                if (arrayList.size() > 0) {
                    JSONArray array = new JSONArray();
                    for (String str : arrayList) {
                        array.put(str);
                    }
                    result = array.toString();
                }
            }
        } catch (InterruptedException e) {
        }
        return result;
    }

    /**
     * 异步调用图片上传接口
     * 请在非ui线程中调用
     * 开始图片获取以及上传
     *
     * @param openWay  1:相机 2相册 3相册+相机
     * @param platform
     */
    public void startAsy(int openWay, int platform) {
        if (isStart) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "请等待所有图片上传结束", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        isStart = true;
        startType = ASY_START;//不能使用该方式，应该检测
        this.platform = platform;
        switch (openWay) {
            case BY_CAMERA:
                takePhone(mContext, seed);
                break;
            case BY_ALBUM:
                openPhotoPicker(true);
                break;
            case BY_ONLY_ALBUM:
                openPhotoPicker(false);
                break;
        }
    }


    //注册相机广播
    private void registerStartCamera() {
        if (cameraBroadcastReceiver == null) {
            cameraBroadcastReceiver = new CameraBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BROADCAST_CAMERA_BACK_RANDOM);
            mContext.registerReceiver(cameraBroadcastReceiver, filter);
        }
    }

    //注销广播
    private void unRegisterStartCamera() {
        if (mContext != null && cameraBroadcastReceiver != null) {
            mContext.unregisterReceiver(cameraBroadcastReceiver);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoPicker.REQUEST_CODE://图片选择返回
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        ArrayList<String> photos =
                                data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        startUpload(photos);
                    }
                } else if (resultCode == PhotoPickerActivity.RESULT_CAMERA) {//相机拍完返回时，不处理

                } else {
                    cancelQueue();
                }
                break;
            case PicUploadManager.ONLY_CAMERA://纯相机返回
                if (resultCode != RESULT_OK) {
                    cancelQueue();
                }
                break;
        }
    }

    //相机相关广播
    private class CameraBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BROADCAST_CAMERA_BACK_RANDOM.equals(intent.getAction())) {
                //此处可以回传给weex本地地址+开始上传
                startUpload(intent.getStringExtra(CAMERA_BIGPATH_PARM));
            }
        }
    }

    /**
     * 打开相册
     *
     * @param withCamera 是否含有相机
     */
    private void openPhotoPicker(boolean withCamera) {
        PhotoPicker.builder().
                setPhotoCount(9).
                setGridColumnCount(4)
                .setCameraTargetId("1")
                .setCameraUserId("2")
                .setShowCamera(withCamera)
                .setCameraclick(new Ysmcameraclick(seed))
                .setPictureClick(new PictureEditClick())
                .setAddDescription(false)//添加图片描述
                .setTitleText("上传")
                .start((Activity) mContext);
    }

    /**
     * 上传单个文件
     *
     * @param localPath
     */
    public void startUpload(String localPath) {
        List<String> localFiles = new ArrayList<>();
        localFiles.add(localPath);
        UploadFiles uploadFiles = new UploadFiles();
        uploadFiles.setLocalFiles(localFiles);
        uploadFiles.setNetFiles(new ArrayList<MediaFile>());
        qJobLoader.qJobContext().startJob("Upload2", uploadFiles);
//        showUploadDialog();
        if (startType == SYN_START) {
            showUploadProgressDialog();
        }
    }

    /**
     * 上传多个图片
     *
     * @param localPathList
     */
    public void startUpload(ArrayList<String> localPathList) {
        List<String> localFiles = new ArrayList<>();
        localFiles.addAll(localPathList);
        UploadFiles uploadFiles = new UploadFiles();
        uploadFiles.setLocalFiles(localFiles);
        uploadFiles.setNetFiles(new ArrayList<MediaFile>());
        qJobLoader.qJobContext().startJob("Upload2", uploadFiles);
        if (startType == SYN_START) {
            showUploadProgressDialog();
        }
    }


    //进入拍摄界面
    private void takePhone(Context context, int seed) {
        String cameraBigPath = BaseUtil.ChatFileUtil.getChatPicPath(Sps_RWLoginUser.readUser(context).ID, "public_account");
        String cameraSmallPath = BaseUtil.ChatFileUtil.getChatPicSmallPath(Sps_RWLoginUser.readUser(context).ID, "public_account");
        Intent openCamera = new Intent();
        openCamera.setClass(context, PublicAccountCameraNew.class);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_BIGTHU_FOLDER_KEY, cameraBigPath);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_SMALLTHU_FOLDER_KEY, cameraSmallPath);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_IS_ADD_DESCRIPTION, false);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_IS_SHOW_IMAGE_CONTINUE, false);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_IS_SHOW_IMAGE_EDIT, false);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_SEED, seed);
        ((Activity) context).startActivityForResult(openCamera, ONLY_CAMERA);
    }


    private void showUploadProgressDialog() {
        isCancelUpload = false;
        // 进度条还有二级进度条的那种形式，这里就不演示了
        uploadDialog = new ProgressDialog(mContext);
        uploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        uploadDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        uploadDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        uploadDialog.setTitle("提示");
        uploadDialog.setMax(100);
//        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
        uploadDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCancelUploadDialog();
                    }
                });
//        uploadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                if (cancelUploadDialog != null && cancelUploadDialog.isShowing()) {
//                    cancelUploadDialog.dismiss();
//                }
//            }
//        });
        uploadDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    showCancelUploadDialog();
                    return true;
                }
                return false;
            }
        });
//        uploadDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        showCancelUploadDialog();
//                    }
//                });
        uploadDialog.setMessage("准备上传...");
        uploadDialog.show();
    }

    /**
     * 显示 正在上传 提示的Dialog
     */
//    private void showUploadDialog() {
//        if (this.mBasIn == null) {
//            this.mBasIn = new BounceTopEnter();
//        }
//        if (mBasOut == null) {
//            mBasOut = new SlideBottomExit();
//        }
//        if (uploadDialog == null) {
//            uploadDialog = new MaterialDialog(mContext);
//            uploadDialog.isTitleShow(false)
//                    .content(
//                            "正在上传...")
//                    .btnNum(1)
//                    .btnText("取消")
//                    .showAnim(mBasIn)
//                    .dismissAnim(mBasOut);
//            uploadDialog.setCanceledOnTouchOutside(false);
//            uploadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    if (cancelUploadDialog != null && cancelUploadDialog.isShowing()) {
//                        cancelUploadDialog.dismiss();
//                    }
//                }
//            });
//        }
//        uploadDialog.setOnBtnClickL(new OnBtnClickL() {
//            @Override
//            public void onBtnClick() {
//                showCancelUploadDialog();
//            }
//        });
//        uploadDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    showCancelUploadDialog();
//                    return true;
//                }
//                return false;
//            }
//        });
//        uploadDialog.show();
//    }
    //是否确认了取消上传
    private boolean isCancelUpload = false;

    /**
     * 显示 停止上传 提示的Dialog
     */
    public void showCancelUploadDialog() {
        isCancelUpload = false;
        if (mBasIn == null) {
            mBasIn = new BounceTopEnter();
        }
        if (mBasOut == null) {
            mBasOut = new SlideBottomExit();
        }
        if (cancelUploadDialog == null) {
            cancelUploadDialog = new MaterialDialog(mContext);
            cancelUploadDialog.isTitleShow(false)
                    .content(
                            "是否停止上传图片？")
                    .btnText("否", "是")
                    .showAnim(mBasIn)
                    .dismissAnim(mBasOut)
                    .show();
            cancelUploadDialog.setOnBtnClickL(
                    new OnBtnClickL() {//left btn click listener
                        @Override
                        public void onBtnClick() {
                            isCancelUpload = false;
                            cancelUploadDialog.dismiss();
                            if (startType == SYN_START) {
                                uploadDialog.show();
                            }
                        }
                    },
                    new OnBtnClickL() {//right btn click listener
                        @Override
                        public void onBtnClick() {
                            isCancelUpload = true;
                            cancelQueue();
                            reLoadJob();
                            if (startType == SYN_START && uploadDialog.isShowing()) {
                                uploadDialog.dismiss();
                            }
                            cancelUploadDialog.dismiss();
                        }
                    }
            );
            cancelUploadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (startType == SYN_START && isCancelUpload) {
                        if (uploadDialog.isShowing()) {
                            uploadDialog.dismiss();
                        }
                    }
                }
            });
        } else {
            cancelUploadDialog.show();
        }
    }

    /**
     * 上传成功的job
     */
//    private QJobTask<UploadFiles> qJobArrestUploadFinish = new QJobTask<UploadFiles>() {
//        @Override
//        public boolean onStart(UploadFiles uploadFiles) {
//            if (uploadDialog.isShowing()) {
//                uploadDialog.dismiss();
//            }
//            if (mManagerListen != null) {
//                mManagerListen.updateBack(uploadFiles);
//            }
//
//            String json = JSONObject.toJSONString(uploadFiles.getNetFiles());
//            putQueue(json);
//            showFinishDialog("上传成功");
//            return false;
//        }
//    };

    /**
     * 提示信息的Dialog
     * 点击确定后将关闭activity
     *
     * @param msg 提示信息
     */
    private void showFinishDialog(final String msg) {
        if (mBasIn == null) {
            mBasIn = new BounceTopEnter();
        }
        final MaterialDialog dialog = new MaterialDialog(mContext);
        dialog//
                .btnNum(1)
                .content(msg)//
                .btnText("确定")//
                .showAnim(mBasIn)//
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                if ("上传成功".equals(msg)) {
//                    if (!isFromDetail) {
//                        setResult(RESULT_OK);
//                    } else {
//                        Intent intent = new Intent();
//                        intent.setAction("history_refresh");
//                        sendBroadcast(intent);
//                    }
                }
            }
        });
    }

    /**
     * 显示错误信息的job
     */
    private QJobTask<String[]> qJobShowError = new QJobTask<String[]>() {
        @Override
        public boolean onStart(String[] strings) {
            if (uploadDialog != null && uploadDialog.isShowing()) {
                uploadDialog.dismiss();
            }
            if (cancelUploadDialog != null && cancelUploadDialog.isShowing()) {
                cancelUploadDialog.dismiss();
            }
            if (startType == SYN_START && !isCancelUpload && strings != null) {
                DialogMsg.showDetail(context(), "提示", "上传失败,点击详情查看错误信息", strings[0] + "\n\n" + strings[1]);
            }
            putQueue("upload_failed");
            return false;
        }
    };

    public interface ManagerListen {
        /**
         * 返回上传成功的文件路径（本地与在线地址）
         *
         * @param re
         */
        void updateBack(String re);

//        //当前第几个上传，总共几个上传
//        void uploadProgress1(String s);
//
//        //当前上传文件的进度
//        void uploadProgress2(String s);
//
//        //整体上传文字描述，包含uploadProgress1与uploadProgress2
////        void uploadProgress(String progress1Msg);
//
//        void otherErr(String err);
    }

    public interface UpdateInterface {
        /**
         * activity开启拍照上传流程
         *
         * @param type
         */
        String startPic(int type);

        void startAsyPic(int type);

    }

    public boolean isStart() {
        return isStart;
    }

    private void runOnUiThread(Runnable runnable){
        if(mContext!=null){
            ((Activity)mContext).runOnUiThread(runnable);
        }
    }
}
