package synway.common.file.progress;

public interface OnProgress {


	void progress(int progress);


	void progressDetial(int currentCount, int allCount);
	
}
