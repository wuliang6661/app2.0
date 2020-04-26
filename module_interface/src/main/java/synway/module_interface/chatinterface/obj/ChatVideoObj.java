package synway.module_interface.chatinterface.obj;

/**
 * Created by zjw on 2017/5/17.
 */

public class ChatVideoObj extends ChatMsgObj {
    /** 本地存储的带后缀的文件地址 视频格式为.mp4 */
    public String fileName;
    /** 本地视频是否存在，自己拍摄的视频存在本地文件和url，别人发送的视频只有url没有视频文件 */
    public boolean isFileExit;
//    /** 视频缩略图地址 不带后缀的文件名 格式为.png*/
//    public String picFileName;
    /** url 带http://IP:Port */
    public String url = "";
}
