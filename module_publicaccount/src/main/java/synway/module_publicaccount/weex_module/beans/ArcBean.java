package synway.module_publicaccount.weex_module.beans;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name 圆弧Bean
 * @describe
 * @time 2019/1/16 10:09
 */
public class ArcBean {
    public String id;
    public Double startLatitude; // 圆弧起点纬度
    public Double startLongitude; // 圆弧起点经度
    public Double passedLatitude; // 圆弧中点纬度
    public Double passedLongitude; // 圆弧中点经度
    public Double endLatitude; // 圆弧终点纬度
    public Double endLongitude; // 圆弧终点经度
    public String strokeColor; // 圆弧的边框颜色，ARGB格式
    public Float strokeWidth; // 圆弧的边框宽度，单位像素
    public Boolean isVisible; // 圆弧的可见属性
    public Float zIndex; // 圆的Z轴数值，默认为0
}
