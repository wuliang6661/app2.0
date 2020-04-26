package synway.module_publicaccount.analytical;

import java.util.UUID;

import synway.module_publicaccount.analytical.obj.Obj_ChatBaseInfo;
import synway.module_publicaccount.analytical.obj.Obj_Msg;
import synway.module_publicaccount.analytical.obj.Obj_Msg_File;


/**
 * 由界面来形成的消息通用类
 * 
 * @author 钱园超 2015年8月25日下午4:46:33
 */
public class GetMsgObj {



	public static final Obj_Msg newFileMsg(Obj_ChatBaseInfo chatSendPram, String fileNameWhitSufix) {
		Obj_Msg chatSend = new Obj_Msg_File(fileNameWhitSufix);
		chatSend.guid = UUID.randomUUID().toString();
		chatSend.chatType = chatSendPram.chatType;
		chatSend.fromID = chatSendPram.fromUserID;
		chatSend.fromName = chatSendPram.fromUserName;
		chatSend.fromArea = chatSendPram.fromUserArea;
		chatSend.toID = chatSendPram.toUserID;
		chatSend.toName = "";
		chatSend.toArea = "";

		chatSend.groupID = chatSendPram.groupID;
		chatSend.msg = "";
		// chatSend.

//		chatSend.fromID = chatSendPram.fromUserID;
//		chatSend.fromName = chatSendPram.fromUserName;
//		chatSend.fromArea = chatSendPram.fromUserArea;

		return chatSend;
	}
	
	

}
