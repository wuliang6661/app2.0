package synway.module_publicaccount.weex_module.beans;

import java.util.List;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name 热力图
 * @describe
 * @time 2019/2/14 15:43
 */
public class TileOverlayBean {
    public String id;
    public Boolean isPower; // 是否使用权值
    public List<Double> latList;//纬度数组
    public List<Double> lonList;//经度数组

    // 权值，大于零；两个权值等于一的位置点等同于一个权值等于二的点,位置点默认权值1.0
    public List<Double> powerList;

    // 热力图渐变，有默认值 DEFAULT_GRADIENT，可不设置
    // color和statPoints不能为null，长度不能为0，两数组长度须一致，startPoints数据必须递增
    public List<String> colors; // 渐变色可以是所有颜色数组, 按声明的顺序由冷色到热色
    public float[] startPoints; // 每一个颜色对应的起始点数组[0.0f, 1.0f]，数组一一对应。

    public Integer radius; //热力图点半径，默认为12ps,可不设置 范围[10,50]
    public Double alpha; // 热力图层透明度，默认 0.6 ，范围 [0, 1]

    public Boolean isVisible; // 热力图的可见属性
    public Float zIndex; // 热力图的Z轴数值，默认为0
}
