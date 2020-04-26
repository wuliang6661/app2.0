package synway.module_interface.db.table_util;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 最近联系人 最后一条消息的内容替换
 *
 * @author 赵江武
 */
public class ParseMsg {

    /**
     * 处理，并返回最近联系人的显示文字
     */
    public static final String parseMsg(String targetID, String fromUserID,
                                        String fromUserName, int chatType, String msgFull) {
        // 做头
        String headStr = "";
        if (chatType == 1) {
            if (fromUserName != null && !fromUserName.equals("")) {
                headStr = fromUserName + ":";
            }
        }
        return parseMsg(chatType, targetID, fromUserID, headStr, msgFull);
    }

    public static final String parseMsg(int chatType, String targetID, String fromUserID, String headStr, String msgFull) {
        String msgStr = msgFull;
        int type;
        JSONObject msgInfoJson;
        try {
            JSONObject jsonObj = new JSONObject(msgFull);
            type = jsonObj.getInt("MSG_TYPE");

            msgInfoJson = jsonObj.getJSONObject("MSG_INFO");
        } catch (JSONException e) {

            return msgFull;
        }

        if (type == 1) {
            msgStr = msgInfoJson.optString("CONTENT");
        } else if (type == 2) {
            String fileName = msgInfoJson.optString("FILE_NAME");
            int fileType = getTypeFromSuffix(fileName);
            if (fileType == 1) {
                msgStr = "[照片]";
            } else if (fileType == 2) {
                msgStr = "[视频]";
            } else if (fileType == 3) {
                msgStr = "[语音]";
            } else {
                msgStr = "[附件]";
            }
        } else if (type == 3) {
            msgStr = "[位置]";
        } else if (type == 4) {
            msgStr = "[公众号消息]";
        } else if (type == 5) {
            msgStr = "[公众号消息]";
        } else if (type == 6) {
            msgStr = "[公众号消息]";
        } else if (type == 7) {
            msgStr = "[图文消息]";
        } else if (type == -1) {
            msgStr = msgInfoJson.optString("CONTENT");
        }
        //替换别名
        return (headStr == null ? "" : headStr) + msgStr;
    }

    public static final String parseMsg2(String msgFull) {
        return msgFull;
    }

    /**
     * 1=图片 2=视频 3=声音 4=其他
     */
    private static final int getTypeFromSuffix(String fileName) {
        String suffix = getSuffixName(fileName);
        if (suffix.equals("png")) {
            return 1;
        } else if (suffix.equals("jpg")) {
            return 1;
        } else if (suffix.equals("bmp")) {
            return 1;
        } else if (suffix.equals("amr")) {
            return 3;
        } else if (suffix.equals("mp3")) {
            return 3;
        } else if (suffix.equals("wav")) {
            return 3;
        } else if (suffix.equals("3gp")) {
            return 2;
        } else if (suffix.equals("mp4")) {
            return 2;
        } else {
            return 4;
        }

    }

    /**
     * 获取文件后缀名
     */
    private static final String getSuffixName(String fileName) {
        String suffix = "";
        int index = fileName.lastIndexOf(".");

        if (index >= 0) {
            suffix = fileName.substring(index + 1, fileName.length());
        }

        return suffix;
    }

}
