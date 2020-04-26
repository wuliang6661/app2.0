package synway.module_publicaccount.map.Bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail.Point;

/**
 * Created by ysm on 2017/3/15.
 */

public class UserTrailPointBean implements Parcelable {
    public String userid;
    public ArrayList<Point> points;
    public String username;
    public int type;//1：敌方;2:友方;3：设备
    public String group;//分组依据
    public String picurl;//图片地址
    public Boolean isCheck;//是否被选中

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Creator<UserTrailPointBean> CREATOR = new Creator() {
        public UserTrailPointBean createFromParcel(Parcel source) {
            UserTrailPointBean p = new UserTrailPointBean();
            return p;
        }

        public UserTrailPointBean[] newArray(int size) {
            return new UserTrailPointBean[size];
        }
    };


}
