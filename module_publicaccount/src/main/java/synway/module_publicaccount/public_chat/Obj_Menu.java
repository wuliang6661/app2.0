package synway.module_publicaccount.public_chat;

import java.io.Serializable;

public class Obj_Menu implements Serializable {

	/** 公众号ID */
	public String ID = null;
	/**公众号名字*/
	public String Name=null;

	/** 按钮名称 */
	public String menuName = "aaaa";

	public String menuGUID = null;
	
	//menu最多只有2级
	/** 
	 * menu 对应的父级 menu
	 * <p>
	 * 如果该字段本身是父级menu,那么该值为 "" 
	 * <p>
	 * 如果有父级menu， 那么该值为menu对应的menuGUID
	 */
	public String menuFather = null;

	/** menu 唯一认证标识 */
	public String menuKey = null;

	/**
	 * <p>
	 * 0=menuButton 此时只有本字段. (代表是父节点),
	 * 一级菜单只有父节点
	 * <p>
	 * 1=view 此时同时含有3个字段			(url)
	 * <p>
	 * 2=click 此时含有本子段个menuKey字段  (点击事件
	 */
	public int menuType = -1;

	/**
	 * 原文链接，当MenuType=1 时有效
	 */
	public String menuUrl = null;

	/**
	 * url类型，0:html，1：weex
	 */
	public int menuUrlType = 0;

	//0 不使用本地title，1 使用本地title
	public int isShowTitle =1;

	public String sourceUrl;
    //菜單的圖片ID
	public String menuPicUrl=null;
	/**搜拼音的结果*/
	public boolean byPinYin = false;
	/** 标识，adapter  是否是item  */
	public boolean isItem = true;
	/** 菜单拼音 */
	public String namePinYin = null;

	@Override
	public String toString() {
		return "Obj_Menu [ID=" + ID + ", menuName=" + menuName + ", menuGUID="
				+ menuGUID + ", menuFather=" + menuFather + ", menuKey="
				+ menuKey + ", menuType=" + menuType + ", menuUrl=" + menuUrl
				+ "]";
	}
	
}