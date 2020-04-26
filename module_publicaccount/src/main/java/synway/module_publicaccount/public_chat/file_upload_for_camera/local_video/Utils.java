package synway.module_publicaccount.public_chat.file_upload_for_camera.local_video;

/**
 * Created by Mfh on 2017/2/23.
 */

public class Utils {
    /**
     * 将文件的size值（long）转换成带单位的大小字符串
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return String.format("%d B", size);
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0f);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / 1024.0f / 1024.0f);
        } else {
            return String.format("%.1f GB", size / 1024.0f / 1024.0f / 1024.0f);
        }
    }

    /**
     * 将媒体文件的时长值（long毫秒）转化分秒（如格式：00:00:01）时间字符串
     *
     * @param duration
     * @return
     */
    public static String formatMediaDuration(long duration) {
        int sec = 1000;//1秒=1000毫秒
        int min = sec * 60;
        int h = min * 60;

        long hour = duration / h;
        long minute = (duration - hour * h) / min;
        long second = (duration - hour * h - minute * min) / sec;

        StringBuffer sb = new StringBuffer();

        if (hour > 0) {
            sb.append(hour + ":");
        }

        if (minute >= 0) {
            if (minute < 10) {
                sb.append("0" + minute + ":");
            } else {
                sb.append(minute + ":");
            }
        }

        if (second >= 0) {
            if (second < 10) {
                sb.append("0" + second);
            } else {
                sb.append(second);
            }
        }
        return sb.toString();
    }
}
