package synway.module_publicaccount.public_list_new.adapter;

import synway.module_publicaccount.publiclist.bean.App_InformationBean;

/**
 * Created by leo on 2018/6/15.
 */

public class PublicGridItem {
    //公众号的唯一标志Id
    public String id;
    //公众号的名字
    public String name;
    //公众号的类型 0为普通公众号,1为APP应用,2为只有主入口应的公众号，包括html,weex页面跳转和本地原生跳转,3为无按钮只接收推送消息应用
    public int type;
    //公众号的图标名字
    public String mobilePic;
    //APP应用信息,如果不为APP应用，则为空
    public App_InformationBean app_information;

    //当公众号类型为2时，存在urlObj
    public UrlObj urlObj = null;

    //20190118 未读消息数
    public int UnReadCount ;

    //公众号所在大类ID
    public String fatherGroupID;
    //公众号所在大类名字
    public String fatherGroupName;

    //公众号在父类下的顺序编码 从1开始
    public int childNum;

    //是否是标题栏1 是  0否
    public int isTitle;
    //当前父类型下的子公众号数量，用来决定是否是标题栏的收起/更多按钮
    public int childPublicAccountCount;

}
