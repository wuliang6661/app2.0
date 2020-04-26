package synway.module_publicaccount.analytical.obj.noticeview;

import java.io.Serializable;

/**
 * Created by leo on 2018/10/22.
 */

public class SectionItem implements Serializable {
    //段落类型 1=文字段落 2=分隔符
    //其中type为2的时候 contentItem为null;
    public int type = 0;
    public ContentItem contentItem;
}
