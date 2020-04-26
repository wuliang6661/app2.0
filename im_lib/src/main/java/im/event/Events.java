package im.event;

/**
 * Created by Freddy on 2015/11/4.
 * chenshichao@outlook.com
 */
public class Events {

    /**
     * 推送消息
     */
    public static final String CHAT_SINGLE_MESSAGE = "chat_push_message";

    /**
     * 群聊消息
     */
    public static final String CHAT_GROUP_MESSAGE = "chat_group_message";

    /**
     * 单聊消息
     */
    public static final String CHAT_FRIEND_MESSAGE = "chat_friend_message";


    /**
     * 发送消息的状态报告
     */
    public static final String CHAT_SERVER_STATE = "chat_server_state";

    /**
     * 服务端通知客户端改变的杂七杂八通知，比如刷新，禁用设备
     */
    public static final String SERVER_NOTIFICATION = "server_notification";

}
