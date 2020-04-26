package synway.module_interface.chatinterface.imagetextobj;

import java.io.Serializable;

/**
 * Created by zjw on 2017/6/19.
 * 情报模块图文消息的对象
 */

public class ImageTextObj implements Serializable{
    //图文消息标题
    public String title;
    //图文消息内容
    public String content;
    //图文消息logo地址
    public String logoPath;
    //tag
    public String tag;
    //图文消息类型 情报的图文消息类型是 1 用于判断点击图文消息后进行相应的操作
    public int imageTextType;
}
