package synway.module_interface.chatinterface.obj;


/**
 * 公众号音频消息转情报实体类
 */

public class ChatPublicVoiceObj extends ChatMsgObj {
    /** 本地存储的不带后缀的文件名，格式为.amr  */
    public String fileName;

    public String context;
    /**音频url  */
    public String url;

}
