package synway.module_publicaccount.analytical.obj;

import java.io.Serializable;

public class Obj_PublicMsgBase implements Serializable{

	private static final long serialVersionUID = 1L;

	public String publicGUID = null;
	
	public String msgID = null;
	
	public String showTime = null;
	
	public String showSDFTime = null;
	
	public long localTime = 0;
	public int MsgType;
}