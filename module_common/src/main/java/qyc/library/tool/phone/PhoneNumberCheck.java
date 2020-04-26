package qyc.library.tool.phone;

public class PhoneNumberCheck {

	/** 中国移动 */
	public static final int CMCC = 1;

	/** 中国联通 */
	public static final int CUCC = 2;

	/** 中国电信 */
	public static final int CTCC = 4;

	/**
	 * 获取号码类型
	 * */
	public static int check(String phoneNumber) {
		// 移动
		if (phoneNumber.startsWith("134")) {
			return 1;
		} else if (phoneNumber.startsWith("135")) {
			return 1;
		} else if (phoneNumber.startsWith("136")) {
			return 1;
		} else if (phoneNumber.startsWith("137")) {
			return 1;
		} else if (phoneNumber.startsWith("138")) {
			return 1;
		} else if (phoneNumber.startsWith("139")) {
			return 1;
		} else if (phoneNumber.startsWith("150")) {
			return 1;
		} else if (phoneNumber.startsWith("151")) {
			return 1;
		} else if (phoneNumber.startsWith("152")) {
			return 1;
		} else if (phoneNumber.startsWith("157")) {
			return 1;
		} else if (phoneNumber.startsWith("158")) {
			return 1;
		} else if (phoneNumber.startsWith("159")) {
			return 1;
		} else if (phoneNumber.startsWith("187")) {
			return 1;
		} else if (phoneNumber.startsWith("188")) {
			return 1;
		} else if (phoneNumber.startsWith("182")) {
			return 1;
		} else if (phoneNumber.startsWith("183")) {
			return 1;
		} else if (phoneNumber.startsWith("147")) {
			return 1;
		}
		// 联通
		else if (phoneNumber.startsWith("130")) {
			return 2;
		} else if (phoneNumber.startsWith("131")) {
			return 2;
		} else if (phoneNumber.startsWith("132")) {
			return 2;
		} else if (phoneNumber.startsWith("155")) {
			return 2;
		} else if (phoneNumber.startsWith("156")) {
			return 2;
		} else if (phoneNumber.startsWith("185")) {
			return 2;
		} else if (phoneNumber.startsWith("186")) {
			return 2;
		} else if (phoneNumber.startsWith("145")) {
			return 2;
		}
		// 电信
		else if (phoneNumber.startsWith("133")) {
			return 4;
		} else if (phoneNumber.startsWith("153")) {
			return 4;
		} else if (phoneNumber.startsWith("189")) {
			return 4;
		} else if (phoneNumber.startsWith("180")) {
			return 4;
		} else if (phoneNumber.startsWith("181")) {
			return 4;
		} else if (phoneNumber.startsWith("1349")) {
			return 4;
		} else if (phoneNumber.startsWith("142")) {
			return 4;
		} else if (phoneNumber.startsWith("149")) {
			return 4;
		} else if (phoneNumber.startsWith("148")) {
			return 4;
		} else if (phoneNumber.startsWith("146")) {
			return 4;
		} else if (phoneNumber.startsWith("144")) {
			return 4;
		} else {
			return -1;
		}
	}
}
