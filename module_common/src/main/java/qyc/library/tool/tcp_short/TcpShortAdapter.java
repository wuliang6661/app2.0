package qyc.library.tool.tcp_short;

public interface TcpShortAdapter {
	/** 如果返回了null,表示仍要继续接收 如果返回对象,表示不再接收 */
    Object onTcpShortDecoder(byte[] data);
}
