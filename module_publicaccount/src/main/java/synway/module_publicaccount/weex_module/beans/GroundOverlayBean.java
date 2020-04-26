package synway.module_publicaccount.weex_module.beans;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name 图片覆盖层
 * @describe
 * @time 2019/2/18 9:27
 */
public class GroundOverlayBean {
    public String id;
    // 设置图片的对齐方式，[0,0]是左上角，[1,1]是右下角, 如果不设置，默认为[0.5,0.5]图片的中心点
    public Float anchorU; // 在宽度（水平方向）上的对齐方式，建议范围为[0,1]
    public Float anchorV; // 在高度（垂直方向）上的对齐方式，建议范围为[0,1]
    // 设置ground 覆盖物从正北顺时针的角度，相对锚点旋转
    public Float bearing; // ground 覆盖物从正北顺时针的角度，范围为[0,360)
    // 设置覆盖图片
    public String imgUrl;

    public Double latitude;// 纬度
    public Double longitude;// 经度
    public Float imgWidth; // 覆盖物的宽，单位：米
    public Float imgHeight; // 覆盖物的高，单位：米。

    public Float alpha; //ground 覆盖物的透明度，范围为[0,1]，0为不透明，1为全透明
    public Boolean isVisible; // 可见属性
    public Float zIndex; // Z轴数值，默认为0
}
