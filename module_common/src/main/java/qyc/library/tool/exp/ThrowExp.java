package qyc.library.tool.exp;

/**
 * 抛异常的类
 * 
 * @author 钱园超 [2015年8月4日 下午8:44:46]
 */
public class ThrowExp {

	/**
	 * <p>
	 * 让程序崩溃，以便在开发阶段解决该问题，而不是在运行阶段引发BUG
	 * <p>
	 * 一个msg是一行，会在编译器里见红
	 */
	public static final void throwRxp(String... msg) {
		String str = "";
		for (int i = 0; i < msg.length; i++) {
			str += "\n-->";
			str += msg[i];
		}
		throw new RuntimeException(str);
	}
}
