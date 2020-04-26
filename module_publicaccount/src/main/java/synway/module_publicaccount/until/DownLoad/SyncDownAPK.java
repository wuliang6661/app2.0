package synway.module_publicaccount.until.DownLoad;

import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SyncDownAPK {

	private boolean isDown = false;

	public void start(String path, String fileName, String urlStr) {
		this.isDown = true;

		new Thread(new mRunnable(path, fileName, urlStr)).start();
	}

	public void stop() {
		this.isDown = false;
		this.lsn = null;
	}

	private class mRunnable implements Runnable {

		private String path = null;
		private String fileName = null;
		private String urlStr = null;
		private File saveFile = null;
		private static final int FILESIZE = 2048;

		private Handler handler = null;

		private mRunnable(String path, String fileName, String urlStr) {
			this.urlStr = urlStr;
			this.path = path;
			this.fileName = fileName;
			
			this.handler = new Handler();
		}

		@Override
		public void run() {

			saveFile = new File(path + "/" + fileName);
			if (saveFile.exists()) {
				saveFile.delete();
			}
			new File(path).mkdirs();// 创建文件夹
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (null != lsn) {
						lsn.onStart();
					}
				}
			});

			InputStream inputStream = null;
			FileOutputStream outputStream = null;
			try {
				// 连接HTTP
				URL url = new URL(urlStr);
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				//设置超时时间
				urlConn.setConnectTimeout(6000);
				urlConn.setReadTimeout(6000);

				// 获取文件长度
				int fileLength = urlConn.getContentLength();
				if (fileLength <= 0) {
					// 退出
					if (null != lsn) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								lsn.onFail("无法获取服务端文件大小,请检查网络");
							}
						});
					}
				} else {
					// 获取输入流
					inputStream = urlConn.getInputStream();
					// 初始化文件输出流
					outputStream = new FileOutputStream(saveFile);
					// 开始写文件
					int progress = 0;
					int percentage = 0;// 百分比值
					int lastPercentage = 0;// 上一次提示的百分比值
					byte[] buffer = new byte[FILESIZE];



					while (isDown) {
						int downLoadSize = inputStream.read(buffer);
						if (downLoadSize == -1) {
							percentage = 100;
							break;
						} else {
							outputStream.write(buffer, 0, downLoadSize);
							// 计算百分比值
							progress += downLoadSize;
							percentage = progress * 100 / fileLength;
//							System.out.println("当前进度:" + percentage);
							// 百分值超过2,或者等于100才提示一次
							if (percentage - lastPercentage > 2
									|| percentage == 100) {
//								System.out.println("提示");
								lastPercentage = percentage;
								// setProgress(percentage);

								final int p = percentage;
								handler.post(new Runnable() {
									@Override
									public void run() {
										if (null != lsn) {
											lsn.onProgress(p);
										}

									}
								});
									

							}
						}
					}

					inputStream.close();
					outputStream.close();

					handler.post(new Runnable() {
						@Override
						public void run() {
							if (null != lsn) {
								if (isDown) {
									lsn.onFinish(saveFile);
								}
							}
						}
					});

					// startInstall(saveFile);

				}

			} catch (final Exception e) {
//				System.out.println("更新异常:" + e.toString());
				try {
					inputStream.close();
					outputStream.close();
				} catch (Exception e2) {

				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						if(null != lsn){
							lsn.onFail(e.toString());
						}
					}
				});
				
			}

		}
	}

	private IOnDownApkLsn lsn = null;

	public void setLsn(IOnDownApkLsn lsn) {
		this.lsn = lsn;
	}

	public interface IOnDownApkLsn {
		void onStart();
		
		void onProgress(int progress);

		void onFinish(File resultFile);
		
		void onFail(String error);
	}

}