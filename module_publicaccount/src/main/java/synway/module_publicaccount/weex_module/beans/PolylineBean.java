package synway.module_publicaccount.weex_module.beans;

import java.util.List;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name
 * @describe
 * @time 2018/12/28 10:20
 */
public class PolylineBean {
    /**
     * 必须设置
     */
    public List<Double> latList;//纬度数组
    public List<Double> lonList;//经度数组
    public boolean isAdd; // 是否是在原有的线段上延长

    /**
     * 可设置
     */
    public String id; // 折线Id,不设置Id,无法通过Id删除指定折线
    public Float alpha; // 线段的透明度0~1，默认是1,1表示不透明
    public Boolean isGeodesic; // 线段是否为大地曲线，默认false，不画大地曲线
    public String color; // 整段折线的颜色，"#ffffff"
    public List<Integer> colorValues; // 线段的颜色
    public Boolean useGradient; // 线段是否使用渐变色
    public Integer lineCapType; // Polyline尾部形状,参数：0:普通头；1:扩展头；2:箭头；3:圆形头；
    public Integer lineJoinType; // Polyline连接处形状,参数：0:斜面连接点；1:斜接连接点；2:圆角连接点；

    public String lineImg; // 整段折线的纹理img
    public List<String> lineImgs; // 线段纹理img—list
    public List<Integer> lineIndexs; // 线段纹理index数组,必须和线段纹理list.size相等
    public Boolean useTexture; // 是否使用纹理贴图画线

    public Boolean isDottedLine; // 是否画虚线，默认为false，画实线
    public Integer dottedLineType; // 虚线形状 0:方形，1圆形
    public Float transparency; // 线段的透明度0~1，默认是1,1表示不透明
    public Boolean isVisible; // 线段的可见性
    public Float width; // 线段的宽度，默认为10
    public Float zIndex; // 线段Z轴的值

}
