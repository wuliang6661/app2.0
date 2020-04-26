package synway.module_publicaccount.analytical.obj.noticeview;

import java.io.Serializable;

/**
 * Created by leo on 2018/10/22.
 */

public class ContentItem implements Serializable {
    //文本内容
    public String contentText;
    //字体大小，单位sp
    public int contentSize = 16;
    //16进制色，#FFFFFF
    public String contentColor;
    //是否加粗，默认不加粗
    public boolean isContentBold = false;
    //是否下划线，默认没有下划线
    public boolean isContentUnderline = false;
    //是否斜体，默认不斜体
    public boolean isContentItalic = false;


}
