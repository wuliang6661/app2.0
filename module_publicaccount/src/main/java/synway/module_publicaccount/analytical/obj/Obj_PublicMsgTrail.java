package synway.module_publicaccount.analytical.obj;

import android.nfc.Tag;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import synway.module_publicaccount.map.Bean.UserPointBean;

/**
 * Created by ysm on 2017/2/24.
 * 轨迹消息（5）
 */

public class Obj_PublicMsgTrail extends Obj_PublicMsgBase{
    public String  ID;//轨迹线ID
    public String Name;//轨迹线名称
    public String Group="";//Gis分组名称，默认为空
    public int Type;// 轨迹线类型：1=敌方，2=友方
    public int Auto=1;//默认为1，是否自动播放轨迹点 1：是 0：否
    public int  Visble=1;//默认为1，轨迹点是否显示1：是 0：否
    public int Num;//轨迹点在地图中显示的数量，只有在Visble时有效
    public String TextColor="0xFFFFFF";//字体颜色，默认为0xFFFFFF
    public ArrayList<Point> Points;//点数组信息
    public static class Point implements Cloneable, Serializable, Parcelable {
        public String y;// 经度
        public String x;// 纬度
        public int tagType;// 附加属性类型:0=空，1=文本，2=链接
        public Obj_PulibcMsgDynamicLocation.tag tag;// 点的附加属性，可通过事件获取
        public ArrayList<Obj_PulibcMsgDynamicLocation.Float> floats=new ArrayList<>();//浮动属性信息
        public String time;// 轨迹点时间秒
        public int pType = 1;// 点类型，默认1 1=点  2=图像
        public Obj_PulibcMsgDynamicLocation.PointInfo pointInfo;//点信息
        public Date datetime;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(y);
            dest.writeString(x);
            dest.writeInt(tagType);
            dest.writeString(time);
            dest.writeInt(pType);
        }
        public static final Creator<Point> CREATOR = new Creator() {
            public Point createFromParcel(Parcel source) {
                Point p = new Point();
                return p;
            }

            public Point[] newArray(int size) {
                return new Point[size];
            }
        };

    }




}
