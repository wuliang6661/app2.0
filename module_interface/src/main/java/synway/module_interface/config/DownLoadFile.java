package synway.module_interface.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownLoadFile {
	private static final int BUFFER_SIZE = 2048;
	public static final boolean download(File file, String urlStr) {

		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		}
		catch (IOException e1) {
			return false;
		}

		URL url = null;
		HttpURLConnection urlConn = null;

		// 初始化URL
		try {
			url = new URL(urlStr);
		}
		catch (MalformedURLException e) {
			return false;
		}

		// 初始化HTTP连接
		try {
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout(6000);
			urlConn.setReadTimeout(6000);
		}
		catch (IOException e) {
			return false;
		}

		// 获取文件长度
		int fileLength = urlConn.getContentLength();
		if (fileLength <= 0) {
			urlConn.disconnect();
			return false;
		}

		// 开始有效下载

		// 初始化下载结束时的反馈内容
		boolean result = false;
		// String msg = null;

		// 保存HTTP连接
		// manager.httpURLConnection = urlConn;

		InputStream inputStream = null;
		FileOutputStream outputStream = null;

		download : {
			// 获取输入流
			try {
				inputStream = urlConn.getInputStream();
			}
			catch (IOException e) {
				result = false;
				// msg = "HTTP下载失败:\n" + e.toString();
				break download;
			}

			// 初始化文件输出流
			try {
				outputStream = new FileOutputStream(file);
			}
			catch (FileNotFoundException e) {
				result = false;
				// msg = "文件创建失败:\n" + e.toString();
				break download;
			}

			// 开始写文件
			boolean isDownloadFinish = false;// 下载是否完成

			byte[] buffer = new byte[BUFFER_SIZE];

			try {
				while (true) {
					int downLoadSize = inputStream.read(buffer);
					if (downLoadSize == -1) {
						isDownloadFinish = true;
						break;
					} else {
						outputStream.write(buffer, 0, downLoadSize);
					}
				}
			}
			catch (Exception e) {
				result = false;
				// msg = "下载失败:\n" + e.toString();
				break download;
			}

			// 结束下载循环后,设置下载结果
			if (isDownloadFinish) {
				result = true;
				// msg = "下载成功";
			} else {
				result = true;
				// msg = "停止下载";
			}
		}// download块

		try {
			inputStream.close();
		}
		catch (Exception e) {
		}

		try {
			outputStream.close();
		}
		catch (Exception e) {
		}

		// 断开HTTP连接
		urlConn.disconnect();

		// 下载完成

		// 如果失败的话删除文件
		if (!result) {
			file.delete();
		}

		return result;
	}
}
