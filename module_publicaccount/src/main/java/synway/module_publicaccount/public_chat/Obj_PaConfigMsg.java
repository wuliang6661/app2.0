package synway.module_publicaccount.public_chat;

import java.io.Serializable;

public class Obj_PaConfigMsg implements Serializable {

	/** 公众号ID */
	public String ID = null;

	/**
	 * weex备注存放地址
	 */
	public String sourceUrl=null;

	/**
	 * 只有一个主入口时的类型 html:0,weex:1,2本地原生应用
	 */
	public String publicUrlType;

	/**
	 * 公众号url
	 */
	public String publicUrl;
	@Override
	public String toString() {
		return "Obj_PaConfigMsg [ID=" + ID + ", sourceUrl=" + sourceUrl + "]";
	}
	
}