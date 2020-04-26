package synway.module_publicaccount.public_chat.file_upload_for_camera.local_video;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import android.util.Log;

/**
 * Created by ZeroPE on 2017/1/12.
 */

public class AsyncGetVideo {

    private boolean isStop = false;

    private OnGetVideoListener listener;

    public AsyncGetVideo() {

    }


    public void start(Context context) {
        if (isStop) {
            return;
        }
        new Thread(new mRunnable(context)).start();

    }

    public void stop() {
        this.isStop = true;
        this.listener = null;
    }


    private class mRunnable implements Runnable {

        private List<Obj_item> paths;
        private Context context;

        mRunnable(Context context) {
            this.context = context;
            paths = new ArrayList<>();
        }

        @Override
        public void run() {

            /** 扫描视频 */
            Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            ContentResolver contentResolver_video = context.getContentResolver();

            String selection_video = MediaStore.Video.Media.MIME_TYPE + "=?";
            String[] selectionArgs_video = new String[]{"video/mp4"};
            String sortOrder_video = MediaStore.Video.Media.DATE_MODIFIED + " DESC";

            Cursor cursor_video = contentResolver_video.query(mVideoUri, null, selection_video,
                    selectionArgs_video, sortOrder_video);

            Set<String> mDirPaths_video = new HashSet<String>();

            int totalVideoCount = 0;
            while (cursor_video.moveToNext()) {
                String path = cursor_video.getString(cursor_video.getColumnIndex(MediaStore.Video.Media.DATA));
                String duration = cursor_video.getString(cursor_video.getColumnIndex(MediaStore.Video.Media.DURATION));
                long fileSize_L = cursor_video.getLong(cursor_video.getColumnIndex(MediaStore.Video.Media.SIZE));
                long duration_L = Long.valueOf(duration);

                if (duration_L == 0) {
                    continue;
                }

                File parentFile = new File(path).getParentFile();

                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();

//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
//                Date date = new Date(new File(path).lastModified());
//                String time = sdf.format(date);
//                Log.d("Mfh--->", "video path:" + path+" lastModified:"+time);

                Obj_item item = new Obj_item();
                item.path = path;
                item.type = 1;
                item.size = fileSize_L > 0L ? fileSize_L : 0L;
                item.duration = duration_L > 0L ? duration_L : 0L;
                paths.add(item);

                totalVideoCount++;
            }
//            Log.d("Mfh--->", "video count:" + totalVideoCount);

            cursor_video.close();

            if (listener != null) {
                listener.onGetVideoResult(paths);
            }

        }
    }


    public interface OnGetVideoListener {
        void onGetVideoResult(List<Obj_item> paths);
    }

    public void setOnGetVideoListener(OnGetVideoListener listener) {
        this.listener = listener;
    }

}
