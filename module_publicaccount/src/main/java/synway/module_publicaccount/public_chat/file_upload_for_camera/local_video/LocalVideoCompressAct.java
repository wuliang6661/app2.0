package synway.module_publicaccount.public_chat.file_upload_for_camera.local_video;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Future;

import qyc.library.control.dialog_confirm.DialogConfirm;
import qyc.library.control.dialog_confirm.OnDialogConfirmCancel;
import qyc.library.control.dialog_confirm.OnDialogConfirmClick;
import qyc.library.control.dialog_msg.DialogMsg;
import synway.module_publicaccount.R;

//import android.util.Log;

/**
 * Created by Mfh on 2017/2/17.
 */

public class LocalVideoCompressAct extends Activity {

    private static final String TAG = "LocalVideoCompressAct";
    private static final String FILE_PROVIDER_AUTHORITY = "OSC";

    private String videoOutputDir;
    private HashMap<String, Object> resultMap;

    private String videoPath = "";
    private float scale = 0.5f;
    private int bitrate = 1500 * 1000;


    private static final int PROGRESS_BAR_MAX = 1000;
    private Future<Void> mFuture;

    private ProgressBar mProgressBar;
    private Button mBt_cancelCompress;
    private TextView mTv_progressBarPercent;

    private boolean showToast = false;
    private TextView tvTitle;
    private ImageButton imgvBack;

    private boolean isCancelled = false;

    private Dialog dialog_fileCreateFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_local_video_compress);

        tvTitle = findViewById(R.id.lblTitle);
        tvTitle.setText("视频压缩");
        imgvBack = findViewById(R.id.back);
        imgvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });

        mBt_cancelCompress = findViewById(R.id.cancel_button);
        mBt_cancelCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });

        mTv_progressBarPercent = findViewById(R.id.tv_progressBar_percent);

        Intent intent = getIntent();
        videoOutputDir = intent.getStringExtra("videoOutputDir");
        resultMap = (HashMap<String, Object>) intent.getSerializableExtra("resultMap");

        videoPath = (String) resultMap.get(VideoInfoResult.VIDEO_PATH);
        scale = (float) resultMap.get(VideoInfoResult.SCALE);
        bitrate = (int) resultMap.get(VideoInfoResult.BITRATE);

        FileDescriptor fileDescriptor ;

        final ParcelFileDescriptor parcelFileDescriptor;

        ContentResolver resolver = this.getContentResolver();
        try {
            parcelFileDescriptor = resolver.openFileDescriptor(Uri.fromFile(new File(videoPath)), "r");

        } catch (FileNotFoundException e) {
//            Log.w("Could not open '" + videoPath + "'", e);
            return;
        }
        fileDescriptor = parcelFileDescriptor.getFileDescriptor();


        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setMax(PROGRESS_BAR_MAX);

        final File file;
        final File file_tmp;
        File outputDir = new File(videoOutputDir);
        //noinspection ResultOfMethodCallIgnored
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        try {
            //用createTempFile方法创建的文件的文件名有随机数
            //file = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".mp4", outputDir);
            String fileName = String.valueOf(System.currentTimeMillis());
//            file = new File(outputDir, fileName + ".mp4");
//            file_tmp = new File(outputDir, fileName + ".mp4.tmp");

            file = new File(outputDir, fileName );
            file_tmp = new File(outputDir, fileName + ".tmp");
            if (!file_tmp.exists()) {
                file_tmp.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e(TAG, "Failed to create temporary file.", e);
            dialog_fileCreateFail = DialogMsg.show(this, "通知", "临时文件创建失败！");
            dialog_fileCreateFail.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    LocalVideoCompressAct.this.finish();
                }
            });
            return;
        }

        final long startTime = SystemClock.uptimeMillis();
        MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {
                if (isCancelled) {
                    return;
                }
                if (progress < 0) {
                    mProgressBar.setIndeterminate(true);
                    mTv_progressBarPercent.setText("进度：0%");
                } else {
                    int percent = (int) Math.round(progress * PROGRESS_BAR_MAX);
                    mProgressBar.setIndeterminate(false);
                    mProgressBar.setProgress(percent);
                    mTv_progressBarPercent.setText("进度：" + (float) percent / 10 + "%");
                }
            }

            @Override
            public void onTranscodeCompleted() {
                if (isCancelled) {
                    return;
                }
//                Log.d(TAG, "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                mTv_progressBarPercent.setText("进度：100%");

                //压缩成功，重命名temp文件：xxx.mp4.tmp -> xxx.mp4
                //判断重命名是否失败
                if (!file_tmp.renameTo(file)) {

                    DialogConfirm.show(LocalVideoCompressAct.this, "提示", "视频压缩文件重命名失败，是否直接发送原视频？", new OnDialogConfirmClick() {
                        @Override
                        public void onDialogConfirmClick() {
                            Intent data = new Intent();
                            data.putExtra("sendDirectly", true);
                            data.putExtra("videoPath", videoPath);
                            LocalVideoCompressAct.this.setResult(RESULT_CANCELED, data);
                            LocalVideoCompressAct.this.finish();
                        }
                    }, new OnDialogConfirmCancel() {
                        @Override
                        public void onDialogConfirmCancel() {
                            LocalVideoCompressAct.this.finish();
                        }
                    });
                }


                onTranscodeFinished(true, "transcoded file placed on " + file, showToast);
//                Uri uri = FileProvider.getUriForFile(LocalVideoCompressAct.this, FILE_PROVIDER_AUTHORITY,
//                        file);
//                startActivity(new Intent(Intent.ACTION_VIEW)
//                        .setDataAndType(uri, "video/mp4")
//                        .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));

                Dialog dialog = DialogMsg.show(LocalVideoCompressAct.this, "提示", "视频已压缩完毕!", new DialogInterface
                        .OnDismissListener() {

                    public void onDismiss(DialogInterface dialog) {

                        Intent data = new Intent();
                        data.putExtra("videoPath", file.getPath());
                        data.putExtra("videoName", file.getPath().substring(file.getPath().lastIndexOf("/") + 1));

                        LocalVideoCompressAct.this.setResult(RESULT_OK, data);
                        finish();
                    }
                });
                dialog.setCanceledOnTouchOutside(false);

            }

            @Override
            public void onTranscodeCanceled() {
                if (isCancelled) {
                    return;
                }
                onTranscodeFinished(false, "Transcoder canceled.", showToast);
            }

            @Override
            public void onTranscodeFailed(Exception exception) {
                if (isCancelled) {
                    return;
                }
                onTranscodeFinished(false, "Transcoder error occurred.", showToast);
                DialogConfirm.show(LocalVideoCompressAct.this, "提示", "视频压缩出现异常，是否直接发送？", new OnDialogConfirmClick() {
                    @Override
                    public void onDialogConfirmClick() {
                        Intent data = new Intent();
                        data.putExtra("sendDirectly", true);
                        data.putExtra("videoPath", videoPath);
                        LocalVideoCompressAct.this.setResult(RESULT_CANCELED, data);
                        LocalVideoCompressAct.this.finish();
                    }
                }, new OnDialogConfirmCancel() {
                    @Override
                    public void onDialogConfirmCancel() {
                        LocalVideoCompressAct.this.finish();
                    }
                });
            }
        };


        mFuture = MediaTranscoder.getInstance().transcodeVideo(fileDescriptor, file_tmp.getAbsolutePath(),
                MediaFormatStrategyPresets.createAndroidAdaptableFormatStrategy(scale, bitrate), null,
                listener);
        mBt_cancelCompress.setEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            if (mFuture != null) {
                isCancelled = true;
                mFuture.cancel(true);
            }
            if (dialog_fileCreateFail != null && dialog_fileCreateFail.isShowing()) {
                dialog_fileCreateFail.dismiss();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showCancelDialog();
    }

    private void showCancelDialog(){
//        if (mFuture != null && (!mFuture.isCancelled() && !mFuture.isDone())) {//未被取消 且 未执行完成
//                DialogConfirm.show(LocalVideoCompressAct.this, "提示", "确定取消视频压缩？", new OnDialogConfirmClick() {
//                    @Override
//                    public void onDialogConfirmClick() {
//                        if (mFuture != null) {
//                            isCancelled = true;
//                            mFuture.cancel(true);
//                        }
//                        LocalVideoCompressAct.this.finish();
//                    }
//                }, new OnDialogConfirmCancel() {
//                    @Override
//                    public void onDialogConfirmCancel() {
//
//                    }
//                });
//
//        }else {
//            LocalVideoCompressAct.this.finish();
//        }


        if (!isCancelled) {
            DialogConfirm.show(LocalVideoCompressAct.this, "提示", "确定取消视频压缩？", new OnDialogConfirmClick() {
                @Override
                public void onDialogConfirmClick() {
                    if (mFuture != null) {
                        isCancelled = true;
                        mFuture.cancel(true);
                    }
                    LocalVideoCompressAct.this.finish();
                }
            }, new OnDialogConfirmCancel() {
                @Override
                public void onDialogConfirmCancel() {

                }
            });
        }else {
            LocalVideoCompressAct.this.finish();
        }
    }

    private void onTranscodeFinished(boolean isSuccess, String toastMessage, boolean showToast) {
        mProgressBar.setIndeterminate(false);
        mProgressBar.setProgress(isSuccess ? PROGRESS_BAR_MAX : 0);
        mBt_cancelCompress.setEnabled(false);
        if (showToast) {
            Toast.makeText(LocalVideoCompressAct.this, toastMessage, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 向外部公开的启动方法
     *
     * @param activity
     * @param resultMap
     * @param videoOutputDir
     * @param requestCode
     */
    public static void actionStart(Activity activity, HashMap<String, Object> resultMap, String videoOutputDir, int
            requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, LocalVideoCompressAct.class);
        intent.putExtra("resultMap", resultMap);
        intent.putExtra("videoOutputDir", videoOutputDir);
        activity.startActivityForResult(intent, requestCode);
    }
}
