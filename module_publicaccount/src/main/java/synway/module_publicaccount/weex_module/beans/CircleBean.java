package synway.module_publicaccount.weex_module.beans;


import java.util.List;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name  圆bean
 * @describe
 * @time 2019/1/3 15:11
 */
public class CircleBean {
    public String id;
    public Double latitude; // 圆心纬度
    public Double longitude; // 圆心经度
    public String fillColor; // 圆的填充颜色
    public Double radius; // 圆的半径，单位米
    public Integer strokeDottedLineType; // 圆的边框虚线形状, -1 不绘制虚线(默认);0 方形;1 圆形;如果设置不属于给出的形状，则不绘制虚线
    public String strokeColor; // 圆的边框颜色，ARGB格式
    public Float strokeWidth; // 圆的边框宽度，单位像素
    public Boolean isVisible; // 圆的可见属性
    public Float zIndex; // 圆的Z轴数值，默认为0

    // 内部圆洞组参数
    public List<Double> holeLatList; // 空心圆心纬度
    public List<Double> holeLonList; // 空心圆心经度
    public List<Double> holeRadiusList; // 空心圆的半径，单位米

    // 内部多边形洞 json
    public String holePolygon;
}
