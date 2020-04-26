package synway.module_publicaccount.analytical.obj;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 通用的发消息的实体类
 * 
 * @author 刘杰 2015年8月25日下午12:50:10
 * @author 钱园超 2015年8月25日下午12:50:10
 */
public abstract class Obj_Msg implements Serializable {

	/** 用来表示消息在本机唯一性的guid */
	public String guid = null;

	/** 用来表示消息在本机收/发的时间 */
	public long localTime = 0;

	/** 用来表示消息在本机的收/发状态，-1=发送失败，0=发送中,1=发送成功  默认为-2，接受到的消息的状态不用维护，统一为-2*/
	public int sendState = -2;
	
	/** 用来表示消息接收状态，-2=没有状态，0=未接收,1=已接收 */
	public int receiveState = -2;
	
	/** 消息同步值，消息发送成功之后 会返回,默认0 */
	public int msgIndex = 0;

	/** 用户输入的消息内容 */
	public String msg = "";

	/** 消息类型，默认=0， 0=单人，1=群组 */
	public int chatType = 0;

	/** 群组ID，默认="" */
	public String groupID = "";

	/** 默认=null */
	public String toID = null;
	/** 目标名称，单聊=对方名称，群聊=“”，默认=“” */
	public String toName = "";
	/** 目标 区域,默认="",现在全部为"",默认=“” */
	public String toArea = "";
	/** 默认=null */
	public String fromID = null;
	/** 默认=null */
	public String fromName = null;
	/** 默认=null */
	public String fromArea = null;

	/** 消息发送成功之后，反馈的系统时间，默认=0 */
	public long serviceTime = 0;

	/** 消息是否已读， 默认=0， 已读=0， 1=未读， 主要针对，语音音频文件*/
	public int isRead = 0;
	
	/** 播放的长度 */
	public int playLength = 0;

	/**
	 * 请组装发送的MSG部分的JSON
	 * 
	 * @return
	 */
	public abstract JSONObject toMsgJson();

	/** 转换成JSON */
	public JSONObject toJson() {
		JSONObject jRoot = new JSONObject();
		try {
			jRoot.put("MSG_INDEX", msgIndex);
			jRoot.put("CHAT_TYPE", chatType);
			jRoot.put("GROUP_ID", groupID);
			jRoot.put("MSG_GUID", guid);//由发送方生成GUID发送
			jRoot.put("FROM_USER_AREA", fromArea);
			jRoot.put("FROM_USER_ID", fromID);
			jRoot.put("FROM_USER_NAME", fromName);
			jRoot.put("TO_USER_AREA", toArea);
			jRoot.put("TO_USER_ID", toID);
			jRoot.put("TO_USER_NAME", "");// 这里已经不需要了

			jRoot.put("MSG", toMsgJson());

			 
			 
			return jRoot;
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return "Obj_ChatSend [ DBID=" + guid + ", sendState=" + sendState + ", msgIndex=" + msgIndex + ", msg=" + msg
				+ ", chatType=" + chatType + ", groupID=" + groupID + ", toID=" + toID + ", toName=" + toName
				+ ", toArea=" + toArea + ", fromID=" + fromID + ", fromName=" + fromName + ", fromArea=" + fromArea
				+ ", serviceTime=" + serviceTime + ", localTime=" + localTime + " ]";
	}

	/** 得出消息的目标ID */
	public String targetID() {
		if (chatType == 0) {
			return toID;
		} else if (chatType == 1) {
			return groupID;
		} else {
			return toID;
		}
	}
//	public Obj_Msg Clone(Obj_Msg obj_msg){
//		Class clazz = this.getClass();
//		Field[] fields = clazz.getDeclaredFields();
//		for(Field field:fields){
//			String fieldName = field.getName();
//			Class type = field.getType();
//			// 过滤引用类型
//
//			Object fieldValue;
//			try {
//				fieldValue=field.get(type);
//			} catch (IllegalAccessException e) {
//			}
//
//			Class clazzClone = obj_msg.getClass();
//			// 对同名变量设值
//			clazz.
//
//		}
//		return obj_msg;
//	}

}
