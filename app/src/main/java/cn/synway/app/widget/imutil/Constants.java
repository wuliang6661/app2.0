package cn.synway.app.widget.imutil;

/**
 * 作者：Rance on 2016/12/20 16:51
 * 邮箱：rance935@163.com
 */
public class Constants {
    public static final String TAG = "rance";
    public static final String AUTHORITY = "cn.synway.app.fileprovider";
    /**
     * 0-接受消息  1-发送消息
     **/
    public static final int CHAT_ITEM_TYPE_LEFT = 0;
    public static final int CHAT_ITEM_TYPE_RIGHT = 1;
    /**
     * 0x003-发送中  0x004-发送失败  0x005-发送成功
     **/
    public static final int CHAT_ITEM_SENDING = 0;
    public static final int CHAT_ITEM_SEND_ERROR = 2;
    public static final int CHAT_ITEM_SEND_SUCCESS = 1;

    public static final String CHAT_FILE_TYPE_TEXT = "text";
    public static final String CHAT_FILE_TYPE_FILE = "file";
    public static final String CHAT_FILE_TYPE_IMAGE = "image";
    public static final String CHAT_FILE_TYPE_VOICE = "voice";
    public static final String CHAT_FILE_TYPE_SNAP = "snapchat";  //阅后即焚
    public static final String CHAT_FILE_TYPE_LINK = "LINK";    //分享的链接
    public static final String CHAT_FILE_TYPE_CONTACT = "contact";
}
