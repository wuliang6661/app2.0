package im.im;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       MessageType.java</p>
 * <p>@PackageName:     com.freddy.chat.im</p>
 * <b>
 * <p>@Description:     消息类型</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/08 00:04</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public enum MessageType {


    /*
     * 握手消息
     */
    HANDSHAKE(1001),

    /*
     * 心跳消息
     */
    HEARTBEAT(1002),

    /*
     * 客户端提交的消息接收状态报告
     */
    CLIENT_MSG_RECEIVED_STATUS_REPORT(1009),

    /*
     * 服务端返回的消息发送状态报告
     */
    SERVER_MSG_SENT_STATUS_REPORT(1010),

    /**
     * 推送消息
     */
    SINGLE_CHAT(2001),

    /**
     * 群聊消息
     */
    GROUP_CHAT(3001),


    /**
     * 聊天消息
     */
    MESSAGE_CHAT(5001),

    /**
     * 杂七杂八的通知消息，比如禁用设备，刷新应用列表
     */
    SERVER_NOTIFICATION(6001);


    private int msgType;

    MessageType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public enum MessageContentType {

        /**
         * 文本消息
         */
        TEXT(101),

        /**
         * 图片消息
         */
        IMAGE(102),

        /**
         * 语音消息
         */
        VOICE(103),

        /**
         * 文件消息
         */
        FILE(104),

        /**
         * 链接分享
         */
        SHARE_LINK(107),

        /**
         * 阅后即焚
         */
        SNAP_CHAT(105),

        /**
         * 阅后即焚已读状态
         */
        SNAP_READ_CHAT(106);

        private int msgContentType;

        MessageContentType(int msgContentType) {
            this.msgContentType = msgContentType;
        }

        public int getMsgContentType() {
            return this.msgContentType;
        }
    }
}
