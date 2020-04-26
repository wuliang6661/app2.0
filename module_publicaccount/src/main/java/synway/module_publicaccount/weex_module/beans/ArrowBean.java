package synway.module_publicaccount.weex_module.beans;

import java.util.List;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name 导航箭头对象
 * @describe
 * @time 2019/2/18 14:31
 */
public class ArrowBean {
    public String id;

    /**
     * 必须设置
     */
    public List<Double> latList;//纬度数组
    public List<Double> lonList;//经度数组
    public String topColor; // 箭头(NavigateArrow)覆盖物的顶颜色
    public Boolean isVisible; // 箭头(NavigateArrow)覆盖物的可见性
    public Float width; // 箭头(NavigateArrow)覆盖物的宽度，默认为10
    public Float zIndex; // 箭头(NavigateArrow)覆盖物Z轴的值
}
