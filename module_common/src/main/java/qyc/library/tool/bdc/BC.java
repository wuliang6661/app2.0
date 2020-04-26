package qyc.library.tool.bdc;

/**
 * 二进制计算
 * <p>
 * 作者:钱园超 日期:2013-11-14
 */
public class BC {
	// =============二进制位移=================
	// 低于32位的数据类型,进行位移后会自动转换为int型,而且会保留符号位.
	// 如果正常扩展为32位(左边加0),目前只知道byte可以和0xff(字节最大值)作&运算.
	//
	// 高于等于位移后仍然保持原来的位.
	//
	// 左移:<<,右边用0填补
	// 左移注意点:左移时符号位应该保持0,否则会出现取反补1的情况
	//
	// 右移:
	// >>表示有符号右移,左边如果是正就用0填补,如果是负就用1填补
	// >>>表示无符号右移,左边全部用0填补
	//
	// 计算机存储方向和数学计算方向正好相反.计算机的字节序0,1,2,3
	// 如果打草稿的话,应该是从左边写到右.而对数学计算来说,最左边的数值是最高位.
	// 比如4个字节转一个int,第0个字节的第0位在最左边,相当于32位的最高位.
	// =======================================

	/**
	 * 从一个字节中提取一个二进制,并用bool表示
	 * 
	 * @param b
	 *            字节
	 * @param index
	 *            0-7 因为1个字节为8个位
	 * @return 位
	 * @throws NO
	 */
	public static final boolean getFromByte_ToBool(byte b, int index) {
		// 从8个位的比特中提取1个,如第3个
		// 先把第3个移到最右边,即右移3
		// 然后和第00000001作&运算
		// 使最右个保持原样其他全部为0
		int result = ((b & 0xff) >>> (8 - (index + 1))) & 1;
		return result != 0;
	}

	/**
	 * 从一个字节中提取1-8个二进制,并化为整型来表示
	 * 
	 * @param b
	 *            字节
	 * @param index
	 *            8个位的起始位置(按计算机位置从左往右)
	 * @param length
	 *            长度,必须>0,如果等于0的话意味着不要任何二进制,结果只能是0
	 * @return 值
	 * @throws NO
	 */
	public static final int getFromByte_ToInt(byte b, int index, int length) {
		// 右移到最右边
		int x1 = (b & 0xff) >>> (8 - (index + length));
		// 得到靠右的,length个1整型值
		int x2 = -1 >>> (32 - length);
		// 作与运算,表示保留靠右的length个长度的位,并转换成具体的数值
		int x3 = x1 & x2;
		return x3;
	}

	/**
	 * 2个比特转int
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            起始位
	 * @return 整形值
	 * @throws NO
	 */
	public static final int getFromBytes_ToInt(byte[] b, int offset) {
		// 和0x00作或运算,是为了byte转成int时不要保留符号位
		return ((b[offset] & 0xff) << 8) + (b[offset + 1] & 0xff);
	}

	/**
	 * 4个比特转Long
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            起始位,包含该位
	 * @return 长整型
	 * @throws NO
	 */
	public static final long getFromBytes_ToLong(byte[] b, int offset) {
		// byte&0xff会无符号扩展至int,再将int扩展为long.
		// 由于将字节左移24位正好触及int的符号位,因此必须先long来接收再左移,写到一起就会出问题(变成先用int左移再转long,这就保留了int受影响后的符号位)
		long x1 = b[offset] & 0xff;
		// 左移24位
		x1 = x1 << 24;
		// 而左移16位并不会触及int的符号位,可以直接用int接收
		return x1 + ((b[offset + 1] & 0xff) << 16) + ((b[offset + 2] & 0xff) << 8) + (b[offset + 3] & 0xff);
	}

	/**
	 * int转比特数组
	 * 
	 * @param a
	 *            int值
	 * @param bytesCount
	 *            字节数组长度,int值最大为4,具体要看这个int在协议中表示几个字节.如果只表示一个字节可直接强制转换为byte
	 * @return
	 * @throws NO
	 */
	public static final byte[] toBytes(int a, int bytesCount) {
		byte[] date = new byte[bytesCount];
		for (int i = 0, j = bytesCount - 1; i < bytesCount; i++, j--) {
			date[j] = (byte) (a >>> (i * 8));
		}
		return date;
	}

	/**
	 * long转比特数组
	 * 
	 * @param a
	 *            long值
	 * @param bytesCount
	 *            字节数组长度,long值最大为8,具体要看这个long在协议中表示几个字节.如果只表示一个字节可直接强制转换为byte
	 * @return
	 * @throws
	 */
	public static final byte[] toBytes(long a, int bytesCount) {
		byte[] date = new byte[bytesCount];
		for (int i = 0, j = bytesCount - 1; i < bytesCount; i++, j--) {
			date[j] = (byte) (a >>> (i * 8));
		}
		return date;
	}

}
