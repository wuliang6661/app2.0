package synway.module_interface.chatinterface;

/**
 * Created by zjw on 2017/5/17.
 * 聊天长按接口
 */

public abstract class ChatLongClickInterface {

    /**
     * 长按后出现的文字
     */
    public abstract String itemContent();
    /**
     * 点击选项后跳转的Activity
     */
    public abstract String activityPath();

    //打开Activity时,通过intent传入的参数:消息对象,
    public static final String CHAT_MSG_OBJ = "CHAT_MSG_OBJ";
    //个人 群，公众号的id
    public static final String TARGET_ID = "TARGET_ID";
    //消息来源，个人:0,群:1,公众号:2
    public static final String TARGET_TYPE = "TARGET_TYPE";

}
