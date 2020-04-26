package synway.module_interface.chatinterface.obj;

import java.util.ArrayList;

/**
 * Created by zjw on 2017/5/17.
 */

public class ChatImageObj extends ChatMsgObj {
    /** 本地存储的不带带后缀的文件名 式为.jpg  */
    public ArrayList<String> fileNameList;
    /** 图片的描述 */
    public ArrayList<String> descriptionList;
//    /** url不带http://IP:Port */
    //考虑到服务器情报的图片最好和聊天的图片不要关联到一起，url也就没有必要了
//    public String url = "";
}
