package synway.module_publicaccount.analytical.obj;

import java.util.ArrayList;

import synway.module_publicaccount.analytical.obj.view.Obj_ViewBase;

/**
 * 图文消息
 */
public class Obj_PublicMsgPicTxt extends Obj_PublicMsgBase {

	private static final long serialVersionUID = 1L;

	public String title = null;
	
	public String titleUrl = null;

	public String titleUrlName = null;
	
	/** 位置， 默认=1，靠左=1，剧中=2，靠右=3 */
	public int titlePostiton = 1;

	public int titleSize = 2;

	public ArrayList<Obj_ViewBase> dataLines = null;

}