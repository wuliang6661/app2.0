package synway.common.file.progress;

/**
 * 用于管理压缩进度的类
 * 
 * @author 钱园超 2017年4月6日 下午6:54:00
 */
public class ProgressHelp {

	// 当前解压/压缩数量
	private int currentIndex = 0;
	// 当前解压/压缩百分比
	private int lastPercentage = 0;
	// 文件总数量
	private int fileCount;
	// 进度反馈接口
	private OnProgress onProgress = null;

	public ProgressHelp(int fileCount, OnProgress onProgress) {
		this.fileCount = fileCount;
		this.onProgress = onProgress;
	}

	/** 0和100不提示,需要外部自行提示 */
	public void add() {
		currentIndex++;
		onProgress.progressDetial(currentIndex, fileCount);
		int percentage = currentIndex * 100 / fileCount;
		if (percentage > lastPercentage && percentage < 100) {
			lastPercentage = percentage;
			onProgress.progress(percentage);
		}
	}

}
