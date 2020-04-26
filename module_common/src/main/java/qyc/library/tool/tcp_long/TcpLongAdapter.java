package qyc.library.tool.tcp_long;

public interface TcpLongAdapter {

	/** 要求继续解析则返回NULL,解析完成则返回Obj,该Obj将作为init的返回值 */
    AdapterResult onInitDecode(byte[] data);

	/** 解析完成后将剩余数据返回 */
    AdapterResult onReceiveDecode(byte[] data, int length);

	class AdapterResult {
		public AdapterResult(byte[] overData, Object result) {
			this.overData = overData;
			this.result = result;
		}
		public byte[] overData;
		public Object result;
	}
}
