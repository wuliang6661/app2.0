package synway.module_publicaccount.push;

import synway.module_interface.config.BaseUtil;

class GetFolder {

	static final String getThu(String loginUserID, String targetID){
		return BaseUtil.ChatFileUtil.getChatPicSmallPath(loginUserID, targetID);
	}

}