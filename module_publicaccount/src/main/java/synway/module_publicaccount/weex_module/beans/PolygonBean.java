package synway.module_publicaccount.weex_module.beans;

import java.util.List;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name 多边形
 * @describe
 * @time 2019/2/1 14:29
 */
public class PolygonBean {
    public String id;
    // 多边形顶点
    public List<Double> latList;//纬度数组
    public List<Double> lonList;//经度数组
    public String fillColor; // 多边形的填充颜色
    public String strokeColor; // 多边形的边框颜色，ARGB格式，默认为黑色。
    public Float strokeWidth; // 多边形的边框宽度，单位像素
    public Boolean isVisible; // 多边形的可见属性
    public Float zIndex; // 多边形的Z轴数值，默认为0

    // 内部圆洞组参数
    public List<Double> holeLatList; // 空心圆心纬度
    public List<Double> holeLonList; // 空心圆心经度
    public List<Double> holeRadiusList; // 空心圆的半径，单位米


    // 内部多边形洞 json
    public String holePolygon;

}
