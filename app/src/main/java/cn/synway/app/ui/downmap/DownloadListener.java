package cn.synway.app.ui.downmap;


interface DownloadListener {
	void onStart(int[] parent);
	void onFinish(int state, int[] parent);
    void onDownload(long downloaded_size, Integer status, int process, int[] parent);
    void onFail(String reason, int[] parent);
	
}  