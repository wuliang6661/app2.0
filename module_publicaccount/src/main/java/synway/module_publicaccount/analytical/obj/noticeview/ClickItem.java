package synway.module_publicaccount.analytical.obj.noticeview;

import java.io.Serializable;

/**
 * Created by leo on 2018/10/22.
 */

public class ClickItem implements Serializable{

    //链接类型 1=URL 2=通知窗口
    public int type = 1;

    //包含信息的json
    public String clickInfoJson;

    //当type=1的时候
    public ClickUrlItem clickUrlItem;

}
