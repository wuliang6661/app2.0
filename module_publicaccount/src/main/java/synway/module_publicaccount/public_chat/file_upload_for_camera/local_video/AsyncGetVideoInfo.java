package synway.module_publicaccount.public_chat.file_upload_for_camera.local_video;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

import qyc.library.tool.main_thread.MainThread;

/**
 * Created by Mfh on 2017/2/17.
 */

public class AsyncGetVideoInfo {

    private OnGetVideoInfoListener mOnGetVideoInfoListener;
    private boolean isStopped = false;
    private Context mContext;

    private Exception exception ;

    private static final int STANDARD_WIDTH = 960;//768;//视频长宽中最长的长度
    private static final long MAX_SIZE = 5 * 1024 * 1024;//5M
    private static final int MAX_BITRATE = 1500000;


    public AsyncGetVideoInfo(Context context) {
        mContext = context;
    }


    public void start(String videoPath) {
        if (isStopped) {
            throw new RuntimeException("AsyncGetVideoInfo can't start after stop. Please reNew an instance");
        }
        Thread thread = new Thread(new copyFileRunnable(videoPath), "AsyncGetVideoInfoThread");
        thread.start();
    }

    public void stop() {
        isStopped = true;
        mOnGetVideoInfoListener = null;
        mContext = null;
    }


    private class copyFileRunnable implements Runnable {
        private String videoPath;

        copyFileRunnable(String videoPath) {
            this.videoPath = videoPath;
        }

        @Override
        public void run() {
            int originalWidth = 0;
            int originalHeight = 0;

            int resultWidth;
            int resultHeight;
            int bitrate = 0;

            long size = 0L;
            long duration = 0L;

            float scale;

            // 获取视频信息：宽、高、比特率
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(videoPath);

                String widthV = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever
                        .METADATA_KEY_VIDEO_WIDTH);
                if (widthV != null) {
                    originalWidth = Integer.parseInt(widthV);
                }
                String heightV = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever
                        .METADATA_KEY_VIDEO_HEIGHT);
                if (heightV != null) {
                    originalHeight = Integer.parseInt(heightV);
                }

                String bitrateV = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever
                        .METADATA_KEY_BITRATE);
                if (bitrateV != null) {
                    bitrate = Integer.parseInt(bitrateV);
                }

                String durationV = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (durationV != null) {
                    duration = Integer.parseInt(durationV);
                }

                File file = new File(videoPath);
                size = file.length();
                Log.i("AsyncGetVideoInfo", "run: file size:" + size);

            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
                MainThread.joinMainThread(new MainThread.Runnable_MainThread() {
                    @Override
                    public void run() {
                        if (mOnGetVideoInfoListener != null) {
                            VideoInfoResult result = new VideoInfoResult(VideoInfoResult.CODE_ERROR, "获取视频信息出错！",
                                    null);
                            mOnGetVideoInfoListener.OnResult(result);
                        }
                    }
                });

            } finally {
                try {
                    if (mediaMetadataRetriever != null) {
                        mediaMetadataRetriever.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (exception != null) {
                    return;
                }
            }

            // 因为要把视频压缩 AAR 的 minSdkVersion 设置为 16 ，而视频压缩功能需要最低 API-Level 为 18 所以要进行判断
            if( Build.VERSION.SDK_INT < 18 ){
                Log.d("Mfh==========>>", "API<18, dont support compress");
                if (size > 100 * 1024 * 1024) { //大于100M
                    MainThread.joinMainThread(new MainThread.Runnable_MainThread() {
                        @Override
                        public void run() {
                            if (mOnGetVideoInfoListener != null) {
                                VideoInfoResult result = new VideoInfoResult(VideoInfoResult.CODE_ERROR, "视频大小超过100M，不支持发送！",
                                        null);
                                mOnGetVideoInfoListener.OnResult(result);
                            }
                        }
                    });
                } else {
                    MainThread.joinMainThread(new MainThread.Runnable_MainThread() {
                        @Override
                        public void run() {
                            if (mOnGetVideoInfoListener != null) {
                                VideoInfoResult result = new VideoInfoResult(VideoInfoResult.CODE_SEND_DIRECTLY, "", null);
                                mOnGetVideoInfoListener.OnResult(result);
                            }
                        }
                    });
                }
                return;
            }

            resultWidth = originalWidth;
            resultHeight = originalHeight;

            // 与 要求的大小 对比，按比例缩放，得到缩放后的长宽、bitrate
            if ((resultWidth > STANDARD_WIDTH || resultHeight > STANDARD_WIDTH) && size > MAX_SIZE) {

                scale = resultWidth > resultHeight ? (float) STANDARD_WIDTH / resultWidth : (float) STANDARD_WIDTH /
                        resultHeight;
                resultWidth *= scale;
                resultHeight *= scale;

                if (bitrate != 0) {
//                            bitrate *= Math.max(0.5f, scale);
                    bitrate *= Math.max(0.35f, scale);
                }

                // 如果码率还是比预设的大，则强制设为预设值
                if (bitrate > MAX_BITRATE || bitrate <= 0) {
                    bitrate = MAX_BITRATE;
                }

                Log.d("Mfh==========>>", "scale:" + scale);
                Log.d("Mfh==========>>", "result bitrate:" + bitrate);
                returnResult(videoPath, scale, bitrate, originalWidth, originalHeight, duration, size);

            } else if (size > MAX_SIZE) {
                scale = 1.0f;

                // 如果码率还是比预设的大，则强制设为预设值
                if (bitrate > MAX_BITRATE || bitrate <= 0) {
                    bitrate = MAX_BITRATE;
                    returnResult(videoPath, scale, bitrate, originalWidth, originalHeight, duration, size);
                } else {
                    Log.d("Mfh==========>>", "dont need compress");
                    MainThread.joinMainThread(new MainThread.Runnable_MainThread() {
                        @Override
                        public void run() {
                            if (mOnGetVideoInfoListener != null) {
                                VideoInfoResult result = new VideoInfoResult(VideoInfoResult.CODE_SEND_DIRECTLY, "",
                                        null);
                                mOnGetVideoInfoListener.OnResult(result);
                            }
                        }
                    });
                }

            } else {
                Log.d("Mfh==========>>", "dont need compress");
                MainThread.joinMainThread(new MainThread.Runnable_MainThread() {
                    @Override
                    public void run() {
                        if (mOnGetVideoInfoListener != null) {
                            VideoInfoResult result = new VideoInfoResult(VideoInfoResult.CODE_SEND_DIRECTLY, "", null);
                            mOnGetVideoInfoListener.OnResult(result);
                        }
                    }
                });
            }
        }

    }

    private void returnResult(String videoPath, float scale, int bitrate, int originalWidth, int originalHeight, long
            duration, long size) {
        final HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put(VideoInfoResult.VIDEO_PATH, videoPath);
        resultMap.put(VideoInfoResult.SCALE, scale);
        resultMap.put(VideoInfoResult.BITRATE, bitrate);
        resultMap.put(VideoInfoResult.WIDTH, originalWidth);
        resultMap.put(VideoInfoResult.HEIGHT, originalHeight);
        resultMap.put(VideoInfoResult.DURATION, duration);
        resultMap.put(VideoInfoResult.SIZE, size);

        MainThread.joinMainThread(new MainThread.Runnable_MainThread() {
            @Override
            public void run() {
                if (mOnGetVideoInfoListener != null) {
                    VideoInfoResult result = new VideoInfoResult(VideoInfoResult.CODE_COMPRESS, "", resultMap);
                    mOnGetVideoInfoListener.OnResult(result);
                }
            }
        });
    }


    public interface OnGetVideoInfoListener {

        void OnResult(VideoInfoResult result);
    }

    public void setOnGetVideoInfoListener(OnGetVideoInfoListener onGetVideoInfoListener) {
        this.mOnGetVideoInfoListener = onGetVideoInfoListener;
    }

}
