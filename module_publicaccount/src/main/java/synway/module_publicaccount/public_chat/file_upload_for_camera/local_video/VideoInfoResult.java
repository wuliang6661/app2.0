package synway.module_publicaccount.public_chat.file_upload_for_camera.local_video;

import java.util.HashMap;

/**
 * Created by Mfh on 2017/2/17.
 */

public class VideoInfoResult {

    /**
     * resultCode：
     * -1=异常
     * 0=直接发送
     * 1=压缩、不分割
     * 2=压缩且分割
     * 3=不压缩、分割
     **/
    public int resultCode;

    public static final int CODE_ERROR = -1;
    public static final int CODE_SEND_DIRECTLY = 0;
    public static final int CODE_COMPRESS = 1;
    public static final int CODE_COMPRESS_CUT = 2;
    public static final int CODE_CUT = 3;

    /** 失败原因 */
    public String resultDetail;
    /** 成功返回的map集合 */
    public HashMap<String,Object> resultMap;

    public long size = 0L;
    public long duration = 0L;
    public int width = 0;
    public int height = 0;

    /** string */
    public static final String VIDEO_PATH= "videoPath";

    /** float */
    public static final String SCALE= "scale";

    /** int */
    public static final String BITRATE= "bitrate";

    /** long */
    public static final String SIZE= "size";

    /** long */
    public static final String DURATION= "duration";

    /** int */
    public static final String WIDTH= "width";

    /** int */
    public static final String HEIGHT= "height";

    public VideoInfoResult(int resultCode, String resultDetail, HashMap<String,Object> resultMap){
        this.resultCode = resultCode;
        this.resultDetail = resultDetail;
        this.resultMap = resultMap;
    }

}
