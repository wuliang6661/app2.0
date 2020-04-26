package synway.module_publicaccount.analytical.obj;

import java.util.ArrayList;
import synway.module_publicaccount.analytical.obj.noticeview.ClickItem;
import synway.module_publicaccount.analytical.obj.noticeview.SectionItem;
import synway.module_publicaccount.analytical.obj.noticeview.TagItem;

/**
 * Created by leo on 2019/1/17.
 */

public class Obj_PublicMsgTaskNotice extends Obj_PublicMsgBase {
    //通知消息的title 比如：任务审批提醒
    public String title="";
    //顶部标签组
    public ArrayList<TagItem> topTags;
    //底部标签组
    public ArrayList<TagItem> bottomTags;
    //右上角三角标记,对文本内容字数进行限制(目前是支持两个字)
    public TagItem triangleTag;
    //点击事件，如果值为null则没有点击事件，如果有值则有两种点击事件，
    //一种跳转url 一种跳转 通知页面
    public ClickItem clickItem;
    //内容段落组
    public ArrayList<SectionItem> sections;
    //PageType 0=默认不进任何分栏页面，只显示在全部页面。 1=显示在待办页面
    public int pageType = 0;
    //每条通知的唯一ID
    public String noticeMsgID;
}
