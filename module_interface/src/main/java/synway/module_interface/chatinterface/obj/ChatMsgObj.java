package synway.module_interface.chatinterface.obj;

import java.io.Serializable;

/**
 * Created by zjw on 2017/5/17.
 * 情报中间实体类
 */

public abstract class ChatMsgObj implements Serializable{
    //暂时没用的消息guid
    public String guid;
    public String title;//情报标题
    public String description;  //基本内容的文字描述
}
