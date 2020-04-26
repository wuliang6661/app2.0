package synway.module_publicaccount.weex_module.beans;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.beans
 * @name 粒子系统
 * @describe
 * @time 2019/2/13 9:21
 */
public class ParticleOverlayBean {

    public String  imgUrl; // 粒子图标
    public Integer weatherType;   // 高德地图提供的天气粒子,0 晴天，1雨天，2雪天，3雾霾
    public Boolean isLoop; // 整个粒子效果是否循环
    public Boolean isVisibile; // 覆盖物的可见属性。
    public Long    duration; // 整个粒子效果的存活时间,单位毫秒
    public Integer maxParticles; // 整个粒子效果的粒子最大数量
    public Long particleLifeTime; // 每个粒子的存活时间,单位毫秒

    // 发射率，每个多少时间发射粒子数量，越多会越密集
    public Integer rate;  // 发射数量,不能为0
    public Integer rateTime; // 间隔时间

    // 从某个点开始发射粒子
    public Float shapeX; // 指定X位置发射，屏幕比例 （0~1）
    public Float shapeY; // 指定Y位置发射，屏幕比例 （0~1）

    // 从某块区域发射粒子
    public Float shapeLeft; // 屏幕比例 （0~1）
    public Float shapeTop; // 屏幕比例 （0~1）
    public Float shapeRight; // 屏幕比例 （0~1）
    public Float shapeBottom; // 屏幕比例 （0~1）

    // 粒子初始速度，每秒多少个像素，必须有，不然只会原地转，若速度相同，则只会固定的轨迹
    public Float speedX1; // 速度单位像素/秒
    public Float speedY1; // 速度单位像素/秒
    public Float speedX2; // 速度单位像素/秒
    public Float speedY2; // 速度单位像素/秒

    // 每个粒子的初始颜色，在两个颜色范围之间变化
    public String color1; // #12345678
    public String color2;


    // 每个粒子生命周期过程中是否变化
    public Boolean isChange;
    // 每个粒子生命周期过程中角度的变化，
    public Float changeRotation;
    // 每个粒子生命周期过程中速度的变化，
    public Float changeSpeedX1; // 速度单位像素/秒
    public Float changeSpeedY1; // 速度单位像素/秒
    public Float changeSpeedX2; // 速度单位像素/秒
    public Float changeSpeedY2; // 速度单位像素/秒
    // 每个粒子生命周期过程中颜色的变化，
    public String changeColor1;
    public String changeColor2;
    // 每个粒子生命周期过程中大下的变化，正数放大，
    public Float changeSizeX;
    public Float changeSizeY;

    // 修正一下比例, 默认都会被绘制成1像素，设置自己的宽高,需要乘上一个比例
    // 粒子大小-宽高
    public Integer particleW;
    public Integer particleH;
}
