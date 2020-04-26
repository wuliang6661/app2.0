package cn.synway.app.db.table;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.NameInDb;

/**
 * 消息记录表
 */
@Entity
public class MessageEntry {

    @Id
    public long id;

    /**
     * 所属的用户
     */
    @NameInDb("user_id")
    private String userId;

    /**
     * 消息id
     */
    @NameInDb("msg_id")
    private String msgId;

    /**
     * 会话id (对应某个联系人或某个群组)
     */
    @Index
    @NameInDb("recent_id")
    private String recentId;

    /**
     * 消息类型
     * <p>
     * 0: 文字  1：语音  2：图片 3 ： 视频  4 文件  5 定位
     */
    @NameInDb("msg_type")
    private String fileType;

    /**
     * 消息内容
     */
    @NameInDb("msg_content")
    private String content;

    /**
     * 消息时间
     */
    @NameInDb("msg_time")
    private String time;

    /**
     * 消息发送人id
     */
    @NameInDb("send_user_id")
    private String sendUserId;

    /**
     * 消息发送人名
     */
    @NameInDb("send_user_name")
    private String sendUserName;

    /**
     * 消息发送人头像
     */
    @NameInDb("send_user_head")
    private String header;

    /**
     * 消息发送状态
     * <p>
     * 0: 发送中  1: 发送成功  2： 发送失败
     */
    @NameInDb("msg_send_status")
    private int sendState;

    /**
     * 消息是我发送的还是接收的
     * <p>
     * 0: 接受消息  1： 发送消息
     */
    @NameInDb("msg_status")
    private int type;

    /**
     * 消息阅读状态
     * <p>
     * 0： 未读  1：已读
     */
    @NameInDb("msg_read_status")
    private int msgReadStatus;

    /**
     * 消息是文件时
     * 文件地址
     */
    @NameInDb("file_path")
    public String filepath;

    /**
     * 消息是语音时，语音的时间
     */
    @NameInDb("voice_time")
    private long voiceTime;

    public MessageEntry() {

    }

    public MessageEntry(String userId, String msgId, String recentId, String fileType, String content, String time, String sendUserId, String sendUserName,
                        String header, int sendState, int type, int msgReadStatus, String filepath, long voiceTime) {
        this.userId = userId;
        this.msgId = msgId;
        this.recentId = recentId;
        this.fileType = fileType;
        this.content = content;
        this.time = time;
        this.sendUserId = sendUserId;
        this.sendUserName = sendUserName;
        this.header = header;
        this.sendState = sendState;
        this.type = type;
        this.msgReadStatus = msgReadStatus;
        this.filepath = filepath;
        this.voiceTime = voiceTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecentId() {
        return recentId;
    }

    public void setRecentId(String recentId) {
        this.recentId = recentId;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public int getMsgReadStatus() {
        return msgReadStatus;
    }

    public void setMsgReadStatus(int msgReadStatus) {
        this.msgReadStatus = msgReadStatus;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public long getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(long voiceTime) {
        this.voiceTime = voiceTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }


    @Override
    public String toString() {
        return "MessageEntry{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", filepath='" + filepath + '\'' +
                ", sendState=" + sendState +
                ", time='" + time + '\'' +
                ", header='" + header + '\'' +
                ", voiceTime=" + voiceTime +
                ", msgId='" + msgId + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
