package synway.module_publicaccount.analytical.obj.view;

public class Obj_ViewUrlTxt extends Obj_ViewBase{

	private static final long serialVersionUID = 1L;
	/**
	 * URL类型 0：H5,1:Weex
	 */
	public int urlType = 0;

	/**
	 * 页面请求数据
	 */
	public String data = null;
	/**
	 * 是否显示标题栏1 是  0否
	 */

	public int isShowTitle=1;

	public String sourceUrl =null;

	
}