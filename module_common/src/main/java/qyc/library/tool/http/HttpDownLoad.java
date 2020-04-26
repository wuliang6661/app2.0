package qyc.library.tool.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/** HTTP下载 */
public class HttpDownLoad {

	// 2KB一下
	private static final int BUFFER_SIZE = 2048;

	// 默认每2%提示一次
	private int promptInterval = 2;

	// 下载管理表
	private HashMap<Object, DownLoadManager> dManagerMap = new HashMap<Object, DownLoadManager>();

	// 下载监听
	private OnHttpDownLoad onHttpDownLoad = null;

	// 用于测试,将下载速度大大减慢
	private long delayForTest = 0;

	/**
	 * 
	 * @param promptInterval
	 *            百分之几提示一次
	 */
	public HttpDownLoad(int promptInterval) {
		if (promptInterval >= 1) {
			this.promptInterval = promptInterval;
		} else {
			this.promptInterval = 2;
		}
	}

	/**
	 * 开启下载线程
	 * 
	 * @param urlStr
	 *            下载地址
	 * @param filePath
	 *            下载文件路径,如果该文件已经存在,会删除重下
	 * @param key
	 *            下载KEY
	 */
	public synchronized void startDownLoad(String urlStr, String filePath, Object key) {
		File file = new File(filePath);
		startDownLoad(urlStr, file, key);
	}

	/**
	 * 开启下载线程
	 * 
	 * @param urlStr
	 *            下载地址
	 * @param file
	 *            下载文件路径,如果该文件已经存在,会删除重下
	 * @param key
	 *            下载KEY,重复的KEY不会执行下载.STOP后KEY会失效,但不会立即失效,需要到下载线程消亡.
	 *            因此如果STOP后要立即重新下载,请new一个新的KEY
	 */
	public synchronized void startDownLoad(String urlStr, File file, Object key) {
		// 先判断下载是否正在进行
		if (isDownloading(key)) {
			return;
		}

		// 准备开始启动一个新的下载

		// 检查文件,如果文件已经存在,则删除
		if (file.exists()) {
			file.delete();
		}

		// 添加下载管理
		DownLoadManager downLoadManager = new DownLoadManager();
		addManager(key, downLoadManager);

		// 开启下载线程
		new Thread(new DownLoadRunable(urlStr, file, key, downLoadManager)).start();
	}

	/** 该KEY是否正在下载 */
	public boolean isDownloading(Object key) {
		return dManagerMap.containsKey(key);
	}

	/** 停止所有下载 */
	public synchronized void stopDownLoad() {
		synchronized (dManagerMap) {
			Iterator<Object> iterator = dManagerMap.keySet().iterator();
			while (iterator.hasNext()) {
				DownLoadManager manager = dManagerMap.get(iterator.next());
				manager.isContinue = false;
			}
		}
	}

	/**
	 * 停止下载
	 * 
	 * @param key
	 *            停止某一个KEY的下载
	 */
	public synchronized void stopDownLoad(Object key) {
		DownLoadManager manager = dManagerMap.get(key);
		if (manager != null) {
			manager.isContinue = false;
		}
	}

	public void setDelayForTest(long time) {
		this.delayForTest = time;
	}

	/**
	 * 设置HTTP下载监听
	 * 
	 * @param onHttpDownLoad
	 */
	public void setOnHttpDownloadListen(OnHttpDownLoad onHttpDownLoad) {
		this.onHttpDownLoad = onHttpDownLoad;
	}

	/**
	 * 移除HTTP下载监听
	 */
	public void delHttpDownloadListen() {
		this.onHttpDownLoad = null;
	}

	private class DownLoadRunable implements Runnable {
		private String urlStr = null;
		private File saveFile = null;
		private Object key = null;
		private DownLoadManager manager = null;

		public DownLoadRunable(String urlStr, File file, Object key, DownLoadManager manager) {
			this.urlStr = urlStr;
			this.saveFile = file;
			this.key = key;
			this.manager = manager;
		}

		@Override
		public void run() {
			URL url = null;
			HttpURLConnection urlConn = null;

			// 初始化URL
			try {
				url = new URL(urlStr);
			}
			catch (MalformedURLException e) {
				removeManager(key);
				// 反馈错误
				if (onHttpDownLoad != null) {
					onHttpDownLoad.onFinish(key, false, "URL错误", null);
				}
				return;
			}

			// 初始化HTTP连接
			try {
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setConnectTimeout(6000);
				urlConn.setReadTimeout(6000);
			}
			catch (IOException e) {
				removeManager(key);
				// 反馈错误
				if (onHttpDownLoad != null) {
					onHttpDownLoad.onFinish(key, false, "无法打开HTTP连接", null);
				}
				return;
			}

			// 获取文件长度
			int fileLength = urlConn.getContentLength();
			if (fileLength <= 0) {
				removeManager(key);
				urlConn.disconnect();
				// 反馈错误
				if (onHttpDownLoad != null) {
					onHttpDownLoad.onFinish(key, false, "无法获取服务端文件大小,请检查网络", null);
				}
				return;
			}

			// 开始有效下载

			// 初始化下载结束时的反馈内容
			boolean result = false;
			String msg = null;

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
					msg = "HTTP下载失败:\n" + e.toString();
					break download;
				}

				// 初始化文件输出流
				try {
					outputStream = new FileOutputStream(saveFile);
				}
				catch (FileNotFoundException e) {
					result = false;
					msg = "文件创建失败:\n" + e.toString();
					break download;
				}

				// 反馈下载开始
				if (onHttpDownLoad != null) {
					onHttpDownLoad.onStart(key, fileLength, saveFile);
				}

				// 开始写文件
				int progress = 0;
				int percentage = 0;// 百分比值
				int lastPercentage = 0;// 上一次提示的百分比值
				boolean isDownloadFinish = false;// 下载是否完成

				byte[] buffer = new byte[BUFFER_SIZE];

				try {
					while (manager.isContinue) {
						// 大幅度降低下载速度,测试用
						if (delayForTest > 0) {
							Thread.sleep(delayForTest);
						}
						int downLoadSize = inputStream.read(buffer);
						if (downLoadSize == -1) {
							isDownloadFinish = true;
							break;
						} else {
							outputStream.write(buffer, 0, downLoadSize);
							// 计算百分比值
							progress += downLoadSize;
							percentage = progress * 100 / fileLength;
							// 百分值超过间隔,或者等于100才提示一次
							if (percentage - lastPercentage > promptInterval || percentage == 100) {
								lastPercentage = percentage;

								// 提示
								if (onHttpDownLoad != null) {
									onHttpDownLoad.onProgressChanged(key, percentage);
								}
							}
						}
					}
				}
				catch (Exception e) {
					result = false;
					msg = "下载失败:\n" + e.toString();
					break download;
				}

				// 结束下载循环后,设置下载结果
				if (isDownloadFinish) {
					result = true;
					msg = "下载成功";
				} else {
					result = true;
					msg = "停止下载";
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
				saveFile.delete();
			}

			// 从列表移除管理类
			removeManager(key);

			// 反馈下载完成
			if (onHttpDownLoad != null) {
				onHttpDownLoad.onFinish(key, result, msg, saveFile);
			}
		}
	}

	// 添加下载管理类
	private void addManager(Object key, DownLoadManager downLoadManager) {
		synchronized (dManagerMap) {
			dManagerMap.put(key, downLoadManager);
		}
	}

	// 移除下载管理类
	private void removeManager(Object key) {
		synchronized (dManagerMap) {
			dManagerMap.remove(key);
		}
	}

	// 下载管理类
	private class DownLoadManager {
		boolean isContinue = true;
		// HttpURLConnection httpURLConnection = null;
	}

	public interface OnHttpDownLoad {
		/**
		 * 下载开始
		 * 
		 * @param key
		 *            表示一个下载任务的对象
		 * @param downloadLength
		 *            表示即将要下载的文件有多大
		 * @param filePath
		 *            表示文件在本地的地址(已创建该文件)
		 */
        void onStart(Object key, long downloadLength, File filePath);

		/**
		 * 进度改变
		 * 
		 * @param key
		 *            表示一个下载任务的对象
		 * @param progress
		 *            下载进度 0-100
		 */
        void onProgressChanged(Object key, int progress);

		/**
		 * 下载完成
		 * 
		 * @param msg
		 */
		/**
		 * 下载完成
		 * 
		 * @param key
		 *            表示一个下载任务的对象
		 * @param result
		 *            下载结果
		 * @param msg
		 *            结果描述,只有在下载结果为false的时候才会有这个结果描述
		 * @param filePath
		 *            文件所在的路径
		 */
        void onFinish(Object key, boolean result, String msg, File filePath);
	}

}
