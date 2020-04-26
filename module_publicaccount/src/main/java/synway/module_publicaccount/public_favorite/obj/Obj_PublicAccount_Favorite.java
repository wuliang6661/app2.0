package synway.module_publicaccount.public_favorite.obj;

import java.io.Serializable;

import synway.module_publicaccount.publiclist.bean.App_InformationBean;

/**
 * Created by Mfh on 2016/10/12.
 */

public class Obj_PublicAccount_Favorite implements Serializable {


    /**
     * 公众号的ID
     */
    public String ID = "";

    /**
     * 公众号的名称
     */
    public String Name = "";
    /**
     * 菜單的id(如果是公众号，默认为"")
     */
    public String  MenuId="";
    /**
     * 网页跳转的名称 （若为公众号，则默认为""）
     */
    public String menuName = "";

    /**
     * 网页跳转类的Url （若为公众号，则默认为""）
     */
    public String menuUrl = "";

    /**
     * 是否为网页跳转类 默认false
     */
    public boolean isHtml = false;
    /**
     *公众号图标ID
     */
     public String fc_mobilepic="";
    /**
     * 公众号类型：0为普通公众号，1为APP应用
     */
    public int type=0;
    /**
     *  APP应用信息，如果是公众号，则为空
     */
    public App_InformationBean app_information=new App_InformationBean();
}
