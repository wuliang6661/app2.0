package synway.module_publicaccount.public_chat.WebviewFn.newVideoVoice;

import java.util.ArrayList;

/**
 * Created by 钱园超 on 2016/12/14.
 */

public class VoiceReportObj {
    /** 唯一的ID */
    public String ID;
    /**
     * 案件名
     */
    public String caseName;
    /**
     * 标级
     */
    public int flagLevel;
    /**
     * 历史打标信息
     * */
    public ArrayList<FlagHistory> flagHistories = new ArrayList<FlagHistory>();
    /**
     * 正在打标的标级
     * */
    public FlagHistory myFlag;
    /**
     * 呼叫类型
     */
    public String callTypeStr;

    /** 呼叫类型 0=主叫 1=被叫 */
    public int callType;
    /**
     * 对象名
     */
    public String targetName_number;
    /**
     * 对象LAC
     */
    public String targetLAC_CI;
    /**
     * 呼叫号码
     */
    public String callNumber;
    /**
     * 网元
     */
    public String WName;
    /**
     * 呼叫时间
     */
    public String time;

    /**
     * 流媒体信息
     */
    public VoiceObj voiceObj;
    /**
     * mp3地址
     */
    public String mediaujrl;
}
