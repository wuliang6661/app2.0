package qyc.library.tool.http;

public class HttpHead {

	public static final String urlHead(String IP, int port) {
		return "http://" + IP + ":" + port + "/";
	}
}
