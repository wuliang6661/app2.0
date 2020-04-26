package synway.module_publicaccount.analytical.obj.view;

public class Obj_ViewPicTxt extends Obj_ViewBase {

	private static final long serialVersionUID = 1L;

	/** 
	 * 不包括后缀名
	 * 图片下载到本地的名称，该名称根据 图片的url来获得
	 * 例如，url：http://xx/xx/text.jpg,
	 * 那么picName = text
	 */
	public String picName = null;

	/**
	 * 图片地址
	 */
	public String picUrl = null;

	/** 
	 * 图片类型， 1=小图，2=大图 ,
	 * 小图 是 48x48 , 小图 图片显示在右边
	 * 大图，分辨率位置， 所以下载大图的时候， 根据最长边来生产缩略图
	 */
	public int picType = 0;

}