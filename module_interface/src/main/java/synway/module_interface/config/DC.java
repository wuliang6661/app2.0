package synway.module_interface.config;


/**
 * <p>
 * 十进制计算
 * <p>
 * 作者:钱园超
 * <p>
 * 日期:2013-11-20
 */
public class DC {

	/**
	 * 将固定长度的比特数组从原数据中抠出来
	 * 
	 * @param d
	 * @param index
	 * @param length
	 * @return 抠出的字节数据
	 * @throws NO
	 */
	public static final byte[] bytesDigout(byte[] d, int index, int length) {
		byte[] newByte = new byte[length];
		System.arraycopy(d, index, newByte, 0, length);
		return newByte;
	}

	/**
	 * 字节数组的合并
	 * @param byte1 第一段字节数据
	 * @param byte1Length 第一段字节数据需要合并的长度
	 * @param byte2 第二段字节数据
	 * @param byte2Length 第二段字节数据需要合并的长度
	 * @return 合并后的字节数据
	 * @throws NO
	 */
	public static final byte[] bytesCopy(byte[] byte1, int byte1Length,
			byte[] byte2, int byte2Length) {
		byte[] byteTemp_receiveByte = new byte[byte1Length + byte2Length];
		System.arraycopy(byte1, 0, byteTemp_receiveByte, 0, byte1Length);
		System.arraycopy(byte2, 0, byteTemp_receiveByte, byte1Length,
				byte2Length);
		return byteTemp_receiveByte;
	}
	
	/** 从byteTemp中,从头开始删除固定长度的值 */
	public static byte[] byteDelete(byte[] byteTemp, int length) {
		int overLength = byteTemp.length - length;// 剩余长度
		if (overLength > 0) {
			byte[] overByte = new byte[overLength];
			System.arraycopy(byteTemp, length, overByte, 0, overLength);
			return overByte;
		} else {
			return new byte[0];
		}
	}

}
