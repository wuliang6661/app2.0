package synway.module_publicaccount.weex_module.beans;

import java.util.List;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name  海量点
 * @describe
 * @time 2019/2/13 8:41
 */
public class MultiPointBean {
    // 设置海量点的锚点比例。锚点是图标接触地图平面的点。图标的左顶点为（0,0）点，右底点为（1,1）点。默认为（0.5,0.5）
    public Float anchorU; // u - 锚点水平范围的比例，建议传入0 到1 之间的数值。
    public Float anchorV; // v - 锚点垂直范围的比例，建议传入0 到1 之间的数值。
    public String imgUrl;

    public List<Double> latList; // 海量点纬度
    public List<Double> lonList; // 海量点经度
}
