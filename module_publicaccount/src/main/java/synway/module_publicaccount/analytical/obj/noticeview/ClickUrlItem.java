package synway.module_publicaccount.analytical.obj.noticeview;

import java.io.Serializable;

/**
 * Created by leo on 2018/10/22.
 */

public class ClickUrlItem implements Serializable {
    //url地址
    public String url;
    //浏览器地址标题
    public String urlName;
    //0:html 1:weex
    public int urlType = 0;
    //传递的参数其实也就是urlParam 名字改变而已
    public String sourceUrl = "";
    //是否显示标题栏,0:不显示标题栏,1:显示标题栏
    public int isShowTitle = 1;
}
