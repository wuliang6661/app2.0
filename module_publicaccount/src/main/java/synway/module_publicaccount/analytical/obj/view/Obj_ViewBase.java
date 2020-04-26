package synway.module_publicaccount.analytical.obj.view;

import java.io.Serializable;

public class Obj_ViewBase implements Serializable{

	private static final long serialVersionUID = 3110245357453497184L;

	public String url = null;

	public String urlName = null;

	public String content = null;

	public String color = "#FFFFFF";
//	public String textcolor="#869ABE";
	/** 默认=2，1=小字体，2=标准字体，3=大号字体 */
	public int size = 2;

}