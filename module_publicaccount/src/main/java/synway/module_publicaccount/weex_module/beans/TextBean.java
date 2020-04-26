package synway.module_publicaccount.weex_module.beans;


/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name 文字覆盖物
 * @describe
 * @time 2019/1/29 14:23
 */
public class TextBean {

    public String  id; // 文字覆盖物ID
    public Double  latitude;// 纬度
    public Double  longitude;// 经度
    public String  align; // 对齐方式,默认居中对齐 0:居中，1:水平对齐,2:垂直对齐,3: 不对齐
    public String  bgColor; // 背景颜色
    public String  fontColor; // 字体颜色,默认黑色
    public Integer fontSize; // 字体大小
    public Float   rotate; // 旋转角度,逆时针
    public String  object; // 额外信息
    public String  content; // 文字内容
    public Float   zIndex;//设置 zIndex
    public Boolean isVisible; // 文字可见性
}
