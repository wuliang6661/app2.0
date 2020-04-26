package synway.module_publicaccount.public_chat.file_upload_for_camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import qyc.library.control.dialog_confirm.DialogConfirm;
import qyc.library.control.dialog_confirm.DialogConfirmCfg;
import qyc.library.control.dialog_confirm.OnDialogConfirmCancel;
import qyc.library.control.dialog_confirm.OnDialogConfirmClick;
import qyc.library.control.dialog_msg.DialogMsg;
import qyc.tool.qjob.QJobLoader;
import qyc.tool.qjob.QJobTask;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.file_upload_for_camera.entity.MediaFile;
import synway.module_publicaccount.public_chat.file_upload_for_camera.entity.UploadFiles;
import synway.module_publicaccount.public_chat.file_upload_for_camera.jobs.UploadProgress;
import synway.module_publicaccount.public_chat.file_upload_for_camera.jobs.UploadprogressFiles;
import synway.module_publicaccount.public_chat.file_upload_for_camera.local_video.AsyncGetVideoInfo;
import synway.module_publicaccount.public_chat.file_upload_for_camera.local_video.LocalVideo;
import synway.module_publicaccount.public_chat.file_upload_for_camera.local_video.LocalVideoCompressAct;
import synway.module_publicaccount.public_chat.file_upload_for_camera.local_video.Utils;
import synway.module_publicaccount.public_chat.file_upload_for_camera.local_video.VideoInfoResult;
import synway.module_publicaccount.public_chat.file_upload_for_camera.record.VideoRecord;
import synway.module_publicaccount.until.StringUtil;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * 视频拍摄与上传管理类：
 * 由于公众号支持创建桌面快捷方式，会出现多个公众号调用此方法，同名广播会产生混乱
 * 因此需要在每次初始化时随机出一个广播种子，所有的广播加上种子后缀，防止冲突。
 * Created by 朱铁超 on 2018/11/30.
 */
public class VideoUploadManager {

    private static final String TAG = VideoUploadManager.class.getSimpleName();

    public static final int FOR_WEEX = 1;//weex平台调用
    public static final int FOR_H5 = 2;//H5平台调用

    //拍摄上传
    public static final int BY_RECORD = 1;
    //上传本地视频
    public static final int BY_LOCALVIDEO = 2;

    //本地视频选择后返回
    public static final int LOCAL_VIDEO_REQUEST_CODE = 130;
    public static final int LOCAL_VIDEO_COMPRESS = 131;
    public static final int VIDEO_RECPRD = 132;

    private ManagerListen mManagerListen = null;

    //阻塞队列
    public ArrayBlockingQueue<String> queue = null;
    //种子，随机生成，防止广播冲突
    private int seed = 0;

    private QJobLoader qJobLoader = null;
    private Context mContext = null;

    //    private MaterialDialog uploadDialog = null;
    private MaterialDialog cancelUploadDialog = null;
    private ProgressDialog uploadDialog;

    private BaseAnimatorSet mBasIn = null;
    private BaseAnimatorSet mBasOut = null;

    public static final int SYN_START = 1;//同步调用
    public static final int ASY_START = 2;//异步调用
    //同一时刻只能使用一种且一个上传(不论同步异步)，等一个上传执行完后，才能执行另一个
    private boolean isStart = false;
    private int startType = 0;//启动模式1 同步调用 2 异步调用
    private int platform = 0; //平台类型

    private View progressVideo;
    private TextView tv_video_progress2, tv_video_cancel;

    public VideoUploadManager(Context mContext) {
        this.mContext = mContext;
        init();
    }
    /**
     * 初始化异步上传的进度条
     */
    public void initAsyUI() {
        if (mContext != null) {
            progressVideo = ((Activity) mContext).findViewById(R.id.progress_video);
            tv_video_progress2 = ((Activity) mContext).findViewById(R.id.tv_video_progress2);
            tv_video_cancel = ((Activity) mContext).findViewById(R.id.tv_video_cancel);
            tv_video_cancel.setOnClickListener(onClickListener);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showCancelUploadDialog();
        }
    };

    public void setManagerListen(ManagerListen managerListen) {
        this.mManagerListen = managerListen;
    }

    NetConfig netConfig = null;

    /**
     * 初始化控制类
     */
    private void init() {
        queue = new ArrayBlockingQueue<>(1);
        netConfig = Sps_NetConfig.getNetConfigFromSpf(mContext);
        reLoadJob();
    }

    private void reLoadJob() {
        if (qJobLoader == null) {
            qJobLoader = new QJobLoader(mContext, null, "PUPDATE");
            //显示错误信息任务
            qJobLoader.loadJob("ShowError", qJobShowError);
        } else {
            qJobLoader.removeJob("Upload3");
        }
        qJobLoader.loadJob("Upload3", new UploadprogressFiles(netConfig.ftpIP, netConfig.ftpPort, "ShowError", "mp4", new UploadProgress() {
            @Override
            public void progress(final int maxItem, final int currentItem, final long max, final float curProgress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (startType == ASY_START) {
                                if (maxItem != -1 && currentItem != -1) {
                                    if(progressVideo!=null&&progressVideo.getVisibility() == View.GONE){
                                        progressVideo.setVisibility(View.VISIBLE);
                                    }
                                }
                                if (max != -1) {//正式进度
                                    if(tv_video_progress2!=null){
                                        tv_video_progress2.setText((int) (100 * curProgress) + "%");
                                    }
                                }
                        } else if (startType == SYN_START) {

                            if (uploadDialog == null) return;
                            if (maxItem != -1 && currentItem != -1) {
                                uploadDialog.setMessage("正在上传视频");
                            }
                            if (max != -1) {//正式进度
                                uploadDialog.setProgress((int) (100 * curProgress));
                            }

                        }
                    }
                });
            }
        }));
        qJobLoader.loadJob("Upload3", new QJobTask<UploadFiles>() {
            @Override
            public boolean onStart(UploadFiles uploadFiles) {
                if (uploadDialog != null && uploadDialog.isShowing()) {
                    uploadDialog.dismiss();
                }
                if (cancelUploadDialog != null && cancelUploadDialog.isShowing()) {
                    cancelUploadDialog.dismiss();
                }
                String json = JSONObject.toJSONString(uploadFiles.getNetFiles());
                putQueue(json);
                if (startType == SYN_START) {
                    showFinishDialog("上传成功");
                }
                return false;
            }
        });


    }

    /**
     * 创建一个广播种子
     */
    private void initSeed() {
        seed = (int) (Math.random() * 1000);
//        BROADCAST_CAMERA_BACK_RANDOM += seed;
    }

    /**
     * 销毁控制类
     */
    public void destroy() {
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
                    if (progressVideo != null && progressVideo.getVisibility() == View.VISIBLE) {
                        progressVideo.setVisibility(View.GONE);
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
     * 请在非ui线程中调用
     * 开始图片获取以及上传
     *
     * @param openWay
     * @return null 上传失败,否则返回url
     */
    public String start(int openWay, int platform) {
        if (isStart) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "请等待视频上传结束", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
        isStart = true;
        startType = SYN_START;
        this.platform = platform;
        switch (openWay) {
            case BY_RECORD:
                videoRecord(mContext);
                break;
            case BY_LOCALVIDEO:
                openVideoPicker();
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
     * 将string返回值进行处理，改变成json类型，增加参数key
     *
     * @param str
     * @return 后期处理
     */
    private String translateToJson(String str) {
        return null;
    }

    /**
     * 异步调用
     * 请在非ui线程中调用
     * 开始图片获取以及上传
     *
     * @param openWay
     * @return null 上传失败,否则返回url
     */
    public void startAsy(int openWay, int platform) {
        if (isStart) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "请等待视频上传结束", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        isStart = true;
        startType = ASY_START;//不能使用该方式，应该检测
        this.platform = platform;
        switch (openWay) {
            case BY_RECORD:
                videoRecord(mContext);
                break;
            case BY_LOCALVIDEO:
                openVideoPicker();
                break;
        }
    }

    private void videoRecord(Context mContext) {
        Intent intent = new Intent();
        intent.putExtra("TARGET_ID", "public_account");
        intent.putExtra("USER_ID", Sps_RWLoginUser.readUser(mContext).ID);
        intent.putExtra("SHOW_AUTO_UPLOAD", false);//不显示自动上传设置
        intent.putExtra("IS_SINGLE", true);//不显示自动上传设置
        intent.setClass(mContext, VideoRecord.class);
        ((Activity) mContext).startActivityForResult(intent, VIDEO_RECPRD);
    }


    /**
     * 打开视频相册
     */
    private void openVideoPicker() {
        //本地视频
        Intent intent1 = new Intent();
        intent1.setClass(mContext, LocalVideo.class);
        intent1.putExtra(LocalVideo.EXTRA_MAX_SELECT_NUM, 1);
        intent1.putExtra(LocalVideo.EXTRA_GRIDVIEW_COLUMN, 4);
        intent1.putExtra(LocalVideo.EXTRA_ACTION_BUTTON_TEXT, "上传");
//        intent1.putStringArrayListExtra(LocalVideo.KEY_SELECTED_VIDEOS, selectedVideos);
        ((Activity) mContext).startActivityForResult(intent1, LOCAL_VIDEO_REQUEST_CODE);
    }

    /**
     * 上传单个文件
     *
     * @param localPath
     */
    public void startUpload(String localPath) {
        isCancelUpload = false;
        List<String> localFiles = new ArrayList<>();
        localFiles.add(localPath);
        UploadFiles uploadFiles = new UploadFiles();
        uploadFiles.setLocalFiles(localFiles);
        uploadFiles.setNetFiles(new ArrayList<MediaFile>());
        qJobLoader.qJobContext().startJob("Upload3", uploadFiles);
        if (startType == SYN_START) {
            showUploadProgressDialog();
        }
    }

    private void showUploadProgressDialog() {
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
//    public void startUpload(ArrayList<String> localPathList) {
//        List<String> localFiles = new ArrayList<>();
//        localFiles.addAll(localPathList);
//        UploadFiles uploadFiles = new UploadFiles();
//        uploadFiles.setLocalFiles(localFiles);
//        uploadFiles.setNetFiles(new ArrayList<MediaFile>());
//        qJobLoader.qJobContext().startJob("Upload3", uploadFiles);
//        showUploadDialog();
//    }

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
                            "是否停止上传视频？")
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

    //取消选择或上传
    public void cancelQueue() {
        putQueue("cancel");
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
            if (startType == SYN_START && !isCancelUpload && strings != null) {//取消上传的话，那么不弹出错误提示
                DialogMsg.showDetail(context(), "提示", "上传失败,点击详情查看错误信息", strings[0] + "\n\n" + strings[1]);
            }
            putQueue("upload_failed");
            return false;
        }
    };


    /********************压缩 ******************/
    /**
     * 处理 本地视频相册 返回的结果
     */
    public void ActivityResult_PlusLocalVideo(ArrayList<String> paths) {
        if (paths != null && !paths.isEmpty()) {
            final String path = paths.get(0);
            /* 暂时只做1个视频的处理，后续要做多个视频的扩展 */
            AsyncGetVideoInfo asyncGetVideoInfo = new AsyncGetVideoInfo(mContext);
            asyncGetVideoInfo.setOnGetVideoInfoListener(new AsyncGetVideoInfo.OnGetVideoInfoListener() {
                @Override
                public void OnResult(final VideoInfoResult result) {
                    if (result != null) {
                        switch (result.resultCode) {
                            case VideoInfoResult.CODE_ERROR:
                                cancelQueue();
                                DialogMsg.show(mContext, "通知", result.resultDetail);
                                break;
                            case VideoInfoResult.CODE_SEND_DIRECTLY:
                                startUpload(path);
                                break;
                            case VideoInfoResult.CODE_COMPRESS:
                                long size = (long) result.resultMap.get(VideoInfoResult.SIZE);
                                int width = (int) result.resultMap.get(VideoInfoResult.WIDTH);
                                int height = (int) result.resultMap.get(VideoInfoResult.HEIGHT);
                                // 这个码率不是原视频的码率，是压缩后的结果码率，不显示
//                                int bitrate = (int) result.resultMap.get(VideoInfoResult.BITRATE);

                                String stringBuilder = "准备发送的视频文件是否进行压缩？" +
                                        "\n大小：" + Utils.formatFileSize(size) +
                                        "\n分辨率：" + width + "×" + height;

                                // 压缩、不分割 */
                                DialogConfirmCfg cfg = new DialogConfirmCfg();
                                cfg.cancelBtnText = "直接发送";
                                cfg.confirmBtnText = "压缩";
                                DialogConfirm.show(mContext, "提示", stringBuilder, new OnDialogConfirmClick() {
                                    @Override
                                    public void onDialogConfirmClick() {
                                        //Toast.makeText(ChatAct.this, "压缩、不分割", Toast.LENGTH_SHORT).show();
                                        // 启动压缩界面
                                        LocalVideoCompressAct.actionStart(
                                                (Activity) mContext,
                                                result.resultMap,
                                                BaseUtil.ChatFileUtil.getChatVideoPath(Sps_RWLoginUser.readUser(mContext).ID, "public_account"),
                                                LOCAL_VIDEO_COMPRESS
                                        );
                                    }
                                }, new OnDialogConfirmCancel() {
                                    @Override
                                    public void onDialogConfirmCancel() {
                                        //“取消”，即直接发送
//                                        copyAndSend(path);
                                        startUpload(path);
                                    }
                                }, false, cfg);

                                break;
                            case VideoInfoResult.CODE_COMPRESS_CUT:
                                break;
                            case VideoInfoResult.CODE_CUT:
                                break;
                        }
                    }
                }
            });
            asyncGetVideoInfo.start(path);
        } else {
            cancelQueue();
        }
    }

    //activity回调
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case VIDEO_RECPRD://拍摄视频完毕
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        cancelQueue();
                        return;
                    }
                    ArrayList<String> paths = new ArrayList<>();
                    paths.add(data.getStringExtra(VideoRecord.EXTRA_VIDEO_PATH));
                    ActivityResult_PlusLocalVideo(paths);
                } else {
                    cancelQueue();
                }
                break;
            case LOCAL_VIDEO_REQUEST_CODE://本地视频获取完毕
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        cancelQueue();
                        return;
                    }
                    ArrayList<String> paths = data.getStringArrayListExtra(LocalVideo.KEY_SELECTED_VIDEOS);
                    ActivityResult_PlusLocalVideo(paths);
                } else {
                    cancelQueue();
                }
                break;
            case LOCAL_VIDEO_COMPRESS://压缩完
                if (resultCode == RESULT_OK && data != null) {
                    String videoPath = data.getStringExtra("videoPath");
                    if (videoPath != null && !videoPath.isEmpty()) {
                        startUpload(videoPath);
                    }
                } else if (resultCode == RESULT_CANCELED && data != null) {
                    boolean sendDirectly = data.getBooleanExtra("sendDirectly", false);
                    final String path = data.getStringExtra("videoPath");
                    if (path == null || path.isEmpty()) {
                        cancelQueue();
                        DialogMsg.show(mContext, "通知", "压缩后视频路径返回出错！");
                        return;
                    }
                    if (sendDirectly) {
                        startUpload(path);
                    }
                } else {
                    cancelQueue();
                }
                break;
        }
    }

    public interface ManagerListen {
        /**
         * 返回上传成功的文件路径（本地与在线地址）
         */
        void updateBack(String bc);
    }

    public interface UpdateInterface {
        /**
         * activity开启拍照上传流程
         *
         * @param type
         */
        String startVideoUpload(int type);

        void startVideoUploadAsy(int type);
    }

    public boolean isStart() {
        return isStart;
    }

    private void runOnUiThread(Runnable runnable) {
        if (mContext != null) {
            ((Activity) mContext).runOnUiThread(runnable);
        }
    }
}
