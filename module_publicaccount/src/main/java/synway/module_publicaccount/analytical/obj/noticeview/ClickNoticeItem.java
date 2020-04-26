package synway.module_publicaccount.analytical.obj.noticeview;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by leo on 2018/10/22.
 */

public class ClickNoticeItem implements Serializable {
    //标题
    public String title;
    //副标题
    public String subTitle;
    //发布者姓名
    public String publishName;
    //发布日期
    public String dateTime;
    //通知内容，支持多段，每个item表示一段
    public ArrayList<String> sections;

}
