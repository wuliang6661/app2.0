package synway.module_publicaccount.map.Bean;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;

/**
 * Created by ysm on 2017/3/15.
 */

public class UserPointBean implements Serializable {
    public String userid;
    public ArrayList<Obj_PublicMsgTrail.Point> points;
    public String username;
    public int DataType;//数据类型 1：普通数据，2：信标数据（设备数据）
    public int type;//1：敌方;2:友方;
    public String EquipType;//设备类型
    public String group;//分组依据
    public String picurl;//图片网络地址
    public Boolean isCheck;//是否被选中



}
