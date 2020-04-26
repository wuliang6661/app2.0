package synway.module_interface.module;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by zjw on 2017/11/30.
 * 位置传递实体类
 *
 */

public class LoacationObj  implements Serializable{
    //id
    public String guid;
    //位置类型
    public int type;
    //边框颜色
    public int color;
    //头像
    public String headPath;
    //头像网络地址
    public String PicUrl;
    //名字
    public String name;
    //经度
    public String x;
    //维度
    public String y;
    //方向角
    public int angle;
    //更新动作 默认 0删除，1增加或者移动
    public int action;
    //移动方式 0 直接跳到新店 1线性移动到新点
    public int move;
    //显示的文本
    public String text;
    //头像本地地址
    public byte[] picDrwa;
}
