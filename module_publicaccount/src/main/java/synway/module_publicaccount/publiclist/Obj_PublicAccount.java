package synway.module_publicaccount.publiclist;

import java.io.Serializable;
import java.util.ArrayList;

import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_list_new.adapter.UrlObj;
import synway.module_publicaccount.publiclist.bean.App_InformationBean;

public class Obj_PublicAccount  implements Serializable {

	/** 公众号ID, 对应公众号的图标名称 */
	public String ID = null;

	/** 公众号名字 */
	public String name = null;

	/** 公众号拼音 */
	public String namePinYin = null;

	/** 公众号所属公司 */
	public String company = null;

	/** 公众号联系人 */
	public String contact = null;

	/** 公众号联系人电话 */
	public String contactTel = null;

	/** 标识，adapter  是否是item  */
	public boolean isItem = true;

	public boolean isChecked = false;
	//公众号图标名字
	public String fc_mobilepic=null;
	//公众号的一级菜单
	public ArrayList<Obj_Menu> firstmenus=null;
	//公众号的二级菜单
	public ArrayList<Obj_Menu> secondmenus=null;
	/**搜拼音的结果*/
	public boolean byPinYin = false;
	//公众号的类型 0为普通公众号,1为APP应用,2为只有主入口应的公众号，包括html,weex页面跳转和本地原生跳转,3为无按钮只接收推送消息应用
	public  int type =0;
	//APP应用信息，如果是公众号，则为空
	public App_InformationBean app_information;
	//应用超市条目下的应用
	public ArrayList<Obj_PublicAccount> appList;
	//每个拼音下的公众号
	public ArrayList<Obj_PublicAccount> publicobjs=new ArrayList<>();

	//当type为2时， publicUrlType:0 表示直接跳转html，1：weex页面
	public UrlObj urlObj;


	//公众号所在大类ID
	public String fatherGroupID;
	//公众号所在大类名字
	public String fatherGroupName;
	//
	public String pushMsgTypeList;
}