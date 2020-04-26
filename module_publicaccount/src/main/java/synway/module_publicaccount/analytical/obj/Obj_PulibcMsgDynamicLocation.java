package synway.module_publicaccount.analytical.obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by QSJH on 2016/5/6 0006.
 * Gis动态消息(6)
 */
public class Obj_PulibcMsgDynamicLocation extends Obj_PublicMsgBase {
    public int screenType=1;//大屏类型 0=全部 1=地图大屏 2=指挥大屏   手机端只接收1类型的数据点
    public int DataType=1;//数据类型 1=普通数据 2=信标数据（设备数据）
    public int type = 1;// GIS类型：1=敌方，2=友方
    public int num = 1;// 轨迹点在地图中显示的数量，clear=1时有效
    public ArrayList<Point> points;
    public int showNum;//显示在哪个地图大屏上（1、2、3、4屏），0全部显示
    public String dataGuid;//数据类型GUID，每种数据类型都有唯一的标识
    public  int dataCount;//数据数量
    public static class Point  extends Obj_PublicMsgTrail.Point{
        public String uuid;// 唯一性地标识一个点
        public String id;// GIS ID
        public String name;// GIS Name
        public String group;// GIS 分组名称
        public int type;// 画轨迹类型 0=删除，1=新增/移动
        public String textColor = "#ffffff";// 默认#ffffff
        public String tip;// 提示信息


    }

    public static class PointInfo implements Serializable {
    }

    public static class Float implements Serializable {
        public int fType;// 浮动类型1=文本，2=图片
        public String fPicURL;// 图片链接
        public String fURL;// 属性URL
    }
    /**
     * 点
     */
    public static class Dot extends PointInfo {
        public int wink = 0;// 1=是，0=否
        public int size = 12;// 点大小
        public String color = "#ff0000";
    }

    /**
     * 图片
     */
    public static class Picture extends PointInfo {
        public String picURL;//
        public int picSize;//
    }

    public static class tag implements Serializable {
        public String time;
        public int type;
        public equip equip;
    }
    //友方TAG； type=2
    public static class friendTag extends  tag{
        public String name;
        public String group;
        public String policeNum;
    }
    //敵方TAG，type=1
      public static class enemyTag extends  tag{
        public  String number;
        public String publiccase;
        public String publicObject;
    }
    public static class equip  implements Serializable{
        public String typeName;
        public String equipId;
        public String UserGroupType;
        public String equipName;
        public String memo;
        public String locType;
        public String locId;
        public String equipModel;
        public String userType;
        public String userMemo;
    }
}
