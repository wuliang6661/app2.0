package qyc.library.tool.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * HTTP的一些相关操作
 * */
public class HttpClass {

	/**
	 * 根据经纬度获取纠偏信息
	 */
	public static String[] getOffset(double lat, double lon) throws IOException {
		final String httpUrl = "http://www.anttna.com/goffset/goffset1.php?lat=xxxxx&lon=yyyyy";
		String url = httpUrl.replace("xxxxx", String.valueOf(lat));
		url = url.replace("yyyyy", String.valueOf(lon));
		InputStream in = postNull(url, 6000, 10000);
		if (in != null) {
			InputStreamReader inRead = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inRead);
			String str = br.readLine();
			String[] results = str.split(",");
			if (results.length != 2) {
				return null;
			} else {
				return results;
			}
		} else {
			return null;
		}
	}

	/**
	 * 上传文件
	 * */
	public static InputStream postFile(String urlStr, File f) throws Exception {
		if (urlStr == null || f == null) {
			return null;
		}
		HttpClient client = new DefaultHttpClient();

		HttpPost httpPost = new HttpPost(urlStr);
		HttpResponse response = null;
		FileEntity fe = new FileEntity(f, "binary/octet-stream");
		httpPost.setEntity(fe);
		response = client.execute(httpPost);

		// 获得响应状态
		InputStream is = response.getEntity().getContent();
		return is;
	}

	/**
	 * 上传文件
	 * */
	public static InputStream postFile(String urlStr, File f, int connectTimeOut, int soTimeOut)
			throws Exception {
		if (urlStr == null || f == null) {
			return null;
		}
		HttpClient client = new DefaultHttpClient();

		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeOut);// 设置连接超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOut);// 设置数据超时

		HttpPost httpPost = new HttpPost(urlStr);
		HttpResponse response = null;
		FileEntity fe = new FileEntity(f, "binary/octet-stream");
		httpPost.setEntity(fe);
		response = client.execute(httpPost);

		// 获得响应状态
		InputStream is = response.getEntity().getContent();
		return is;
	}

	/**
	 * 根据URL获取应答内容
	 * */
	public static InputStream postNull(String url, int connectTimeOut, int soTimeOut) throws
            IOException {
		InputStream content = null;
		HttpGet httpGet = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeOut);// 设置连接超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOut);// 设置数据超时

		HttpResponse response = httpclient.execute(httpGet);
		content = response.getEntity().getContent();
		return content;
	}

	/**
	 * 根据URL获取应答内容
	 * */
	public static InputStream postNull(String url) throws IOException {
		InputStream content = null;
		HttpGet httpGet = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(httpGet);
		content = response.getEntity().getContent();
		return content;
	}

	/**
	 * POST字符串
	 * */
	public static InputStream postString(String url, String postStr, int connectTimeOut, int soTimeOut)
			throws IOException {
		InputStream content = null;
		HttpPost httpPost = new HttpPost(url);
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeOut);// 设置连接超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOut);// 设置数据超时

		StringEntity se = new StringEntity(postStr);
		httpPost.setEntity(se);
		HttpResponse response = httpclient.execute(httpPost);
		content = response.getEntity().getContent();
		return content;
	}

	/**
	 * POST字符串
	 * */
	public static InputStream postString(String url, String postStr) throws IOException {

		HttpPost httpPost = new HttpPost(url);
		HttpClient httpclient = new DefaultHttpClient();
		StringEntity se = new StringEntity(postStr);
		httpPost.setEntity(se);
		HttpResponse response = httpclient.execute(httpPost);
		return response.getEntity().getContent();
	}

	/**
	 * post比特数组
	 */
	public static InputStream postByte(String url, byte[] postByte, int connectTimeOut, int soTimeOut)
			throws IOException {
		InputStream content = null;
		HttpPost httpPost = new HttpPost(url);
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeOut);// 设置连接超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOut);// 设置数据超时

		ByteArrayEntity se = new ByteArrayEntity(postByte);
		httpPost.setEntity(se);
		HttpResponse response = httpclient.execute(httpPost);
		content = response.getEntity().getContent();
		return content;
	}

	/**
	 * post比特数组
	 */
	public static InputStream postByte(String url, byte[] postStr) throws IOException {

		HttpPost httpPost = new HttpPost(url);
		HttpClient httpclient = new DefaultHttpClient();
		ByteArrayEntity se = new ByteArrayEntity(postStr);
		httpPost.setEntity(se);
		HttpResponse response = httpclient.execute(httpPost);
		return response.getEntity().getContent();
	}

	/* =========C#旧版=========== */
	/** 字符串转Unicode字节数组 */
	// public static byte[] strToUnicodeBytes(String str) {
	// try {
	// byte[] bytes = str.getBytes("Unicode");
	//
	// byte[] unicodeBytes = new byte[bytes.length - 2];
	// for (int i = 0, j = 2; i < unicodeBytes.length; i++, j++) {
	// unicodeBytes[i] = bytes[j];
	// }
	// bytes = null;// 释放内存
	// return unicodeBytes;
	// }
	// catch (UnsupportedEncodingException e) {
	// return null;
	// }
	// }

	/* =========J2EE新版=========== */
	/** 字符串转Unicode字节数组 */
	public static byte[] strToUnicodeBytes(String str) {
		try {
			return str.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	// 流转字符串
	public static String stream2String(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 16 * 1024); // 强制缓存大小为16KB，一般Java类默认为8KB
		StringBuilder sb = new StringBuilder();
		String line = null;
		int i = 0;
		while ((line = reader.readLine()) != null) { // 处理换行符
			if (i != 0) {
				sb.append("\n");
			}
			sb.append(line);
			i++;
		}
		is.close();
		return sb.toString();
	}
}
