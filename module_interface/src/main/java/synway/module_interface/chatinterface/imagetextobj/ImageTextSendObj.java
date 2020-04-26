package synway.module_interface.chatinterface.imagetextobj;

import java.io.Serializable;
import java.util.ArrayList;

import synway.module_interface.chatinterface.imagetextobj.ImageTextObj;

/**
 * Created by zjw on 2017/6/19.
 * 图文发送的对象
 */

public class ImageTextSendObj implements Serializable{
    //发送图文消息的目标ID
    public String targetID;
    //目标对象类型 个人为0 群组为1；
    public int targetType;
    //图文消息列表
    public ArrayList<ImageTextObj> imageTextObjList;
}
