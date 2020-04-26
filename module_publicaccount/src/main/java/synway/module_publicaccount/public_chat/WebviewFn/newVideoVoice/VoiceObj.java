package synway.module_publicaccount.public_chat.WebviewFn.newVideoVoice;

/**
 * Created by 钱园超 on 2016/12/14.
 */

public class VoiceObj {
    public String Mo_number = "";
    public String filePath = "";
    public String mainCall = "";
    public String otherCall = "";
    public int recIndex = -1;
    public String timeLengthStr = null;

    public VoiceObj(int tempRecIndex, String tempMoNumber, String tempFilePath,
                    String tempMainCall, String tempOtherCall, int timeLength) {
        this.filePath = tempFilePath;
        this.mainCall = tempMainCall;
        this.otherCall = tempOtherCall;
        this.Mo_number = tempMoNumber;
        this.recIndex = tempRecIndex;
        if (timeLength >= 60) {
            timeLengthStr = timeLength / 60 + "分";
            timeLengthStr += timeLength % 60 + "秒";
        } else {
            timeLengthStr = timeLength + "秒";
        }
    }
}
