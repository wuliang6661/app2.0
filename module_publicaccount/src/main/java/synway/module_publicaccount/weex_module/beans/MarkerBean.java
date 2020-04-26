package synway.module_publicaccount.weex_module.beans;

import com.amap.api.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by huangxi
 * DATE :2018/12/28
 * Description ：
 * id 唯一的key id，可为null
 * alpha 点的透明度
 * imgUrl 图标地址
 * imgUrls 设置Marker覆盖物的动画帧图标列表地址，多张图片模拟gif的效果
 * title 图标下方显示的文字
 * titleColor 图标下方显示的文字颜色
 * titleBg 图标下方显示的文字背景颜色
 * infoWindowEnable Marker覆盖物的InfoWindow是否允许显示,默认为true
 * latitude  纬度
 * longitude 经度
 * infoContent 弹出窗的文字描述
 * infoTitle 弹出窗的标题
 */
public class MarkerBean {
        public String id;//唯一的key id，可为null
        public Boolean draggable;//设置Marker覆盖物是否可拖拽
        public Float alpha;//点的透明度
        public String imgUrl;//图标地址
        public List<String> imgUrls;//设置Marker覆盖物的动画帧图标列表地址，多张图片模拟gif的效果
        public String title;//图标下方显示的文字
        public String titleColor;//图标下方显示的文字颜色
        public String titleBg;//图标下方显示的文字背景颜色
        public Boolean infoWindowEnable;// Marker覆盖物的InfoWindow是否允许显示,默认为true
        public Double latitude;// 纬度
        public Double longitude;// 经度
        public String infoContent;//弹出窗的文字描述
        public String infoTitle;//弹出窗的标题
        public Integer period;//设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快。
        public Float rotate;//设置Marker覆盖物的图片旋转角度，从正北开始，逆时针计算。
        public Boolean flat;//设置Marker覆盖物是否平贴地图
        public Boolean isGps;//设置Marker覆盖物的坐标是否是Gps，默认为false
        public Integer offsetX;//设置Marker覆盖物的InfoWindow相对Marker的偏移X
        public Integer offsetY;//设置Marker覆盖物的InfoWindow相对Marker的偏移Y
        public Float zIndex;//设置Marker覆盖物 zIndex
        public Boolean isVisible; // 点的可见性



}
