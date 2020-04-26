package synway.module_publicaccount.weex_module.extend.manager;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;


import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.jzxiang.pickerview.data.Type;

import java.math.BigInteger;

import synway.module_publicaccount.weex_module.beans.TimePickerBean;

/**
 * Created by 朱铁超 on 2019/2/1.
 */

public class WxTimePickerManager {

    public static WxTimePickerManager instance=null;
    public static WxTimePickerManager getInstance() {
        if(instance==null){
            instance = new WxTimePickerManager();
        }
        return instance;
    }

    public void openTimePickerDialog(Context context, TimePickerBean timePickerBean, OnDateSetListener onDateSetListener){
        if (timePickerBean == null) {
            Toast.makeText(context, "数据为空，时间选择器打开失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        String cancelString = TextUtils.isEmpty(timePickerBean.cancelString) ? "取消" : timePickerBean.cancelString;
        String sureString = TextUtils.isEmpty(timePickerBean.sureString) ? "确定" : timePickerBean.sureString;
        String titleString = TextUtils.isEmpty(timePickerBean.titleString) ? "请选择时间" : timePickerBean.titleString;

        long minMillseconds = timePickerBean.minMillseconds == 0L ? System.currentTimeMillis() : timePickerBean.minMillseconds;
        long maxMillseconds = timePickerBean.maxMillseconds == 0L ? minMillseconds + TimePickerBean.TENYEARS : timePickerBean.maxMillseconds;
        long currentMillseconds = timePickerBean.currentMillseconds == 0L ? System.currentTimeMillis() : timePickerBean.currentMillseconds;
        if(currentMillseconds>maxMillseconds){
            currentMillseconds=maxMillseconds;
        }else if(currentMillseconds<minMillseconds){
            currentMillseconds=minMillseconds;
        }
        int themeColor = TextUtils.isEmpty(timePickerBean.themeColor) ? 0xffE60012 : (new BigInteger("ff"+timePickerBean.themeColor, 16)).intValue();
        int wheelItemTextNormalColor = TextUtils.isEmpty(timePickerBean.wheelItemTextNormalColor) ? 0xff999999 : (new BigInteger("ff"+timePickerBean.wheelItemTextNormalColor, 16)).intValue();
        int wheelItemTextSelectorColor = TextUtils.isEmpty(timePickerBean.wheelItemTextSelectorColor) ? 0xff404040 :(new BigInteger("ff"+timePickerBean.wheelItemTextSelectorColor, 16)).intValue();
        int toolBarTVColor = TextUtils.isEmpty(timePickerBean.toolBarTVColor) ? 0xffFFFFFF :(new BigInteger("ff"+timePickerBean.toolBarTVColor, 16)).intValue();
        int wheelItemTextSize = timePickerBean.wheelItemTextSize <= 0 ? 12 : timePickerBean.wheelItemTextSize;

        Type type = Type.ALL;
        switch (timePickerBean.type) {
            case 0:
                type = Type.ALL;
                break;
            case 1:
                type = Type.YEAR_MONTH_DAY;
                break;
            case 2:
                type = Type.HOURS_MINS;
                break;
            case 3:
                type = Type.HOURS_MINS_SEC;
                break;
            case 4:
                type = Type.MONTH_DAY_HOUR_MIN;
                break;
            case 5:
                type = Type.MONTH_DAY_HOUR_MIN_SEC;
                break;
            case 6:
                type = Type.YEAR_MONTH;
                break;
            case 7:
                type = Type.YEAR;
                break;
            default:
                type = Type.ALL;
                break;
        }
        new TimePickerDialog.Builder().setCallBack(onDateSetListener)
                .setCancelStringId(cancelString)
                .setSureStringId(sureString)
                .setTitleStringId(titleString)
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setSecondText("秒")
                .setCyclic(timePickerBean.isCyclic)
                .setMinMillseconds(minMillseconds)
                .setMaxMillseconds(maxMillseconds)
                .setCurrentMillseconds(currentMillseconds)
                .setThemeColor(themeColor)
                .setType(type)
                .setToolBarTextColor(toolBarTVColor)
                .setWheelItemTextNormalColor(wheelItemTextNormalColor)
                .setWheelItemTextSelectorColor(wheelItemTextSelectorColor)
                .setWheelItemTextSize(wheelItemTextSize)
                .build().show(((AppCompatActivity)context).getSupportFragmentManager(), "tag");
    }
}
