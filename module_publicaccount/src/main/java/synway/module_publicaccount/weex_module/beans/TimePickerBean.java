package synway.module_publicaccount.weex_module.beans;

/**
 * Created by zhutiechao
 * DATE :2019/1/31
 */


public class TimePickerBean {
    public static final long TENYEARS = 10L * 365 * 1000 * 60 * 60 * 24L;

    public int type=0;//显示的时间轴(0:ALL 1：YEAR_MONTH_DAY 2：HOURS_MINS 3 HOURS_MINS_SEC 4。MONTH_DAY_HOUR_MIN 5 MONTH_DAY_HOUR_MIN_SEC 6 YEAR_MONTH 7 YEAR ) 默认：0
//    public int returnType = 0;//数据返回格式。(0：返回2001-01-01 19:11:01字符串数据 1:毫秒值) 默认： 0
    public String cancelString="取消";//取消按钮名 默认：取消
    public String sureString="确定";//确认按钮名 默认：确定
    public String titleString="请选择时间";//选择器标题 默认：请选择时间
    public String YearText="年"; //年月日时分秒单位名。默认：年月日时分秒
    public String MonthText="月";
    public String DayText="日";
    public String HourText="时";
    public String MinuteText="分";
    public String SecondText="秒";

    public Boolean isCyclic=false;//列表是否循环 默认：false

    public long minMillseconds=0L;//列表可选起始时间(毫秒).默认：当前时间
    public long maxMillseconds=0L;//列表可选结束时间(毫秒).默认：当前时间+10年
    public long currentMillseconds =0L;//当前时间(毫秒).默认：当前时间

    public String themeColor="E60012";//主题颜色 默认：0XFFE60012
    public String wheelItemTextNormalColor="999999";//未选择的item字体颜色 默认：0xFF999999
    public String wheelItemTextSelectorColor="404040";//已选择的item字体颜色 默认：0XFF404040
    public String toolBarTVColor="FFFFFF";//标题栏颜色  默认0xFFFFFFFF
    public int wheelItemTextSize=12;//item字体大小 默认12sp
}
