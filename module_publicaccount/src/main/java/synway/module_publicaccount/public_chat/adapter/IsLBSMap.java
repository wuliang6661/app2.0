package synway.module_publicaccount.public_chat.adapter;

class IsLBSMap {

	/**
	 * 没有包含 lon lat 返回 null; 有则返回 String[]{lon,lat} (120.xx,30.xx)
	 * @param url
	 * @return
	 */
	static final String[] isMap(String url) {
		//HXPositionURL://
		String flag = "HXPositionURL://";
		if (url.indexOf(flag) == -1) {
			return null;
		}

		String str = url.replace(flag, "");
		String result[] = new String[2];
		String strs[] = str.split(":", -1);
		if (strs.length < 2) {
			return null;
		}

		result[0] = strs[0];
		result[1] = strs[1];

		return result;
	}

}