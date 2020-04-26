package synway.module_publicaccount.analytical.obj;

/**
 * 消息发送的基本参数
 * 
 * @author 钱园超 2015年8月25日上午10:46:18
 */
public class Obj_ChatBaseInfo {

	/**
	 * 0=单人 1=群组 2=公众
	 */
	public int chatType = 0;
	public String groupID = "";
	public String fromUserArea = "";
	public String fromUserID = "";
	public String fromUserName = "";
	public String toUserArea = "";
	public String toUserID = "";
	@Deprecated
	public String toUserName = "";
	public String msg = "";

}
