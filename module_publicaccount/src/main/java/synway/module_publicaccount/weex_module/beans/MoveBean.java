package synway.module_publicaccount.weex_module.beans;

import java.util.List;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name
 * @describe
 * @time 2019/2/18 18:15
 */
public class MoveBean {

    public Boolean hasPolyline;

    public List<Double> latList;//纬度数组
    public List<Double> lonList;//经度数组
    public String polylineId; // 折线Id

    // 移动的点
    public String imgUrl;//图标地址
    public Float alpha;//点的透明度
    public Boolean infoWindowEnable;// Marker覆盖物的InfoWindow是否允许显示,默认为true
    public String infoTitle;//弹出窗的标题
    public String infoContent;//弹出窗的文字描述


    public Integer time; // 滑动时间，单位: 秒
    public Boolean isStart; // 是否添加完就开始滑动
}
