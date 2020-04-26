package synway.module_publicaccount.push;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.DownLoadFile;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.Picture;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.Point;

/***
 * 下载图文消息中的 图片域的图片
 *
 * @author liuj [20151125]
 */
public class DownLoadPic {

    /**
     * 下载公众号推送位置消息中的目标头像
     * @param obj
     */
    static final void downLocationPic(Obj_PulibcMsgDynamicLocation obj) {
        File folder = new File(BaseUtil.FILE_HEAD_IMG_THU);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (obj.points == null) {
            return;
        }
        for (Point p : obj.points) {
            if (p.pType == 1) {
                // 类型是一个点，不需要下载头像
                continue;
            }
            String picUrl = ((Picture) p.pointInfo).picURL;
            String picName = getImgName(picUrl);
            File file = new File(BaseUtil.FILE_HEAD_IMG_THU + "/" + picName);
            if (file.exists()) {
                continue;
            }
            DownLoadFile.download(file, picUrl);
        }
    }
    /**
     * 下载公众号推送位置消息中的目标头像
     * @param obj
     */
    static final void downTrailPic(Obj_PublicMsgTrail obj) {
        File folder = new File(BaseUtil.FILE_HEAD_IMG_THU);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (obj.Points == null) {
            return;
        }
        for (Obj_PublicMsgTrail.Point p : obj.Points) {
            if (p.pType == 1) {
                // 类型是一个点，不需要下载头像
                continue;
            }
            String picUrl = ((Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo).picURL;
            String picName = getImgName(picUrl);
            File file = new File(BaseUtil.FILE_HEAD_IMG_THU + "/" + picName);
            if (file.exists()) {
                continue;
            }
            DownLoadFile.download(file, picUrl);
        }
    }
    /**
     * 下载图片消息中的  略图
     * <p/>
     * 图片名称，根据图片的来命名，
     * <p/>
     * 例如,url = "http://192.168.110.130:8080/OSCUserPic/test.jpg"
     * <p/>
     * 图片的名称命为 "test"
     * <p/>
     *
     * @param loginUserID
     * @param publicID
     * @param jsonMsg
     */
    static final void down(String loginUserID, String publicID, JSONObject jsonMsg) {
        int msgType = getMsgType(jsonMsg);
        if (msgType != 4) {
            return;
        }

        JSONArray dataLine = null;
        try {
            JSONObject msgInfo = jsonMsg.getJSONObject("MSG_INFO");
            dataLine = msgInfo.getJSONArray("DATALINE");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        if (dataLine == null || dataLine.length() == 0) {
            return;
        }

        //图片存放的目录
        String folder = GetFolder.getThu(loginUserID, publicID);

        for (int i = 0; i < dataLine.length(); i++) {
            try {
                JSONObject jItem = dataLine.getJSONObject(i);
                int dataType = jItem.getInt("DATATYPE");

                if (dataType == 5) {
//					JSONObject data = jItem.getJSONObject("DATA");
                    String strData = jItem.getString("DATA");
                    JSONObject data = new JSONObject(strData);
                    String url = data.getString("PICURL");
//					//测试
//					url = "http://218.108.76.82:7000/OSCUserPic/User_0571_1114_small";
//					//测试
                    String picName = getImgName(url);
                    if (picName == null) {
                        continue;
                    }
                    File fileFolder = new File(folder);
                    if (!fileFolder.exists()) {
                        fileFolder.mkdirs();
                    }
                    String picPath = folder + "/" + picName;
                    File file = new File(picPath);

//					//图片已经存在就不去下载
//					if(file.exists() && file.length() == 0){
//						continue;
//					}

                    boolean isDown = DownLoadFile.download(file, url);
                    if (isDown) {
                        ImageCompression.compJpg(file.getAbsolutePath(), file.getAbsolutePath(), 200);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

    }

    //picUrl= " http://192.168.110.130:8080/OSCUserPic/test.jpg "
    //所以我将下载下来图片的名字，命为 test.jpg
    //picUrl = " http://192.168.110.130:8080/OSCUserPic/test "
    //命为 test
    public static final String getImgName(String picUrl) {
        if (null == picUrl || "".equals(picUrl.trim())) {
            return null;
        }
        try {
            int startIndex = picUrl.lastIndexOf("/") + 1;

            int lastIndex = picUrl.lastIndexOf(".");
            if (startIndex >= lastIndex) {
                lastIndex = picUrl.length();
            }
            String picName = picUrl.substring(startIndex, lastIndex);

            return picName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("liujie", "图文消息，下载图片出错, e=" + e.toString());
            return null;
        }

    }

    private static final int getMsgType(JSONObject jsonMsg) {
        int type = 0;
        try {
            type = jsonMsg.getInt("MSG_TYPE");
            return type;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


}