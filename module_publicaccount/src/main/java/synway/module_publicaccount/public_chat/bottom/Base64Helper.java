package synway.module_publicaccount.public_chat.bottom;

import it.sauronsoftware.base64.Base64;

public class Base64Helper {

	// 将 s 进行 BASE64 编码
	public static String getBASE64(String s) {
		try {
			s = Base64.encode(s, "UTF-8");
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}