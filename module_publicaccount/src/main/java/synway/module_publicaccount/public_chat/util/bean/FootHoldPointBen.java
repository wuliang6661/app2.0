package synway.module_publicaccount.public_chat.util.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


/**
 * Created by admin on 2017/10/13.
 */

public class FootHoldPointBen implements Serializable {
    private double minlng;//最小经度
    private double maxlng;//最大经度
     private double minlat;//最小纬度
    private double maxlat;//最大纬度
    private String total_duration;

    public String getTotal_duration() {
        return total_duration;
    }

    public void setTotal_duration(String total_duration) {
        this.total_duration = total_duration;
    }

    public double getMinlng() {
        return minlng;
    }

    public void setMinlng(double minlng) {
        this.minlng = minlng;
    }

    public double getMaxlng() {
        return maxlng;
    }

    public void setMaxlng(double maxlng) {
        this.maxlng = maxlng;
    }

    public double getMinlat() {
        return minlat;
    }

    public void setMinlat(double minlat) {
        this.minlat = minlat;
    }

    public double getMaxlat() {
        return maxlat;
    }

    public void setMaxlat(double maxlat) {
        this.maxlat = maxlat;
    }
}
