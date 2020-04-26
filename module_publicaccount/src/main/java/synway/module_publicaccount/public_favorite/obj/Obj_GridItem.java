package synway.module_publicaccount.public_favorite.obj;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.R;
import synway.module_publicaccount.publiclist.SyncGetHeadThu;
import synway.module_publicaccount.until.StringUtil;


/**
 * Grid中的item对象,含有常用应用对象
 * <p>
 * Created by Mfh on 2016/10/13.
 */
public class Obj_GridItem {

    /**
     * 常用应用对象
     */
    public Obj_PublicAccount_Favorite mObj_publicAccount_favorite = null;

    /**
     * item的名称
     */
    public String itemName = "应用";

    /**
     * item的头像,Drawable类型
     */
    public Drawable itemIconDrawable = null;
    public  String itemIconPicId=null;
    /**
     * item默认类型，无效item
     */
    public int itemType = -1;

    /**
     * “常用应用”item
     */
    public static final int ITEM_TYPE_FAVORITE = 20;

    /**
     * “更多”item
     */
    public static final int ITEM_TYPE_MORE = 21;

    /**
     * “常用应用为空，请添加”item
     */
    public static final int ITEM_TYPE_FAVORITE_EMPTY = 22;

    /**
     * “全部应用为空”item
     */
    public static final int ITEM_TYPE_EMPTY = 23;


    /**
     * grid中的item的构造方法
     *
     * @param obj_publicAccount_favorite 只有当itemType为常用应用时才设置，其他type则为null
     * @param context                    上下文，用于ContextCompat.getDrawable
     * @param itemType                   item的类型
     */
    public Obj_GridItem(Obj_PublicAccount_Favorite obj_publicAccount_favorite, Context context, int itemType,SyncGetHeadThu syncGetHeadThu) {

        switch (itemType) {
            case ITEM_TYPE_FAVORITE://常用应用

                if (obj_publicAccount_favorite != null) {

                    this.itemType = ITEM_TYPE_FAVORITE;
                    this.mObj_publicAccount_favorite = obj_publicAccount_favorite;
                    //判断是否为网页跳转类
                    if (mObj_publicAccount_favorite.isHtml) {
                        this.itemName = obj_publicAccount_favorite.menuName;
                    } else {
                        this.itemName = obj_publicAccount_favorite.Name;
                    }
                    if(StringUtil.isEmpty(obj_publicAccount_favorite.fc_mobilepic)){//如果图片名称为空，说明没有头像，不下载
                        this.itemIconDrawable = ContextCompat.getDrawable(context, R.drawable.contact_public_account_png);
                    }else{
                        if(StringUtil.isEmpty(obj_publicAccount_favorite.MenuId)) {//为公众号
                                this.itemIconPicId = obj_publicAccount_favorite.ID;
                            }else{
                                this.itemIconPicId = obj_publicAccount_favorite.MenuId;
                            }
                        }
                }

                break;
            case ITEM_TYPE_MORE://全部应用

                this.itemType = ITEM_TYPE_MORE;
                this.itemName = "全部应用";
                this.itemIconDrawable = ContextCompat.getDrawable(context, R.drawable.public_account_favorite_griditem_more);

                break;
            case ITEM_TYPE_FAVORITE_EMPTY://常用应用为空

                this.itemType = ITEM_TYPE_FAVORITE_EMPTY;
                this.itemName = "无常用应用";
                this.itemIconDrawable = ContextCompat.getDrawable(context, R.drawable.contact_public_account_none_png);

                break;
            case ITEM_TYPE_EMPTY://全部应用为空

                this.itemType = ITEM_TYPE_EMPTY;
                this.itemName = "全部应用为空";
                this.itemIconDrawable = ContextCompat.getDrawable(context, R.drawable.public_account_favorite_griditem_more_none);

                break;
        }
    }


    /**
     * 获取应用的头像路径
     *
     * @param id 应用的id
     * @return 头像路径
     */
    private String getPublicActIconPath(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + id;
    }
}
