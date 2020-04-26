package synway.module_publicaccount.fingerprint;

import android.webkit.JavascriptInterface;

/**
 *
 */
public interface FingerPrintI {

	
	/**
	 * TODO 经验证，该方法在调用时，只要本地没有指纹数据，就直接奔溃，报错信息确实指纹验证失败
	 * 弹出一个对话框引导用户进行指纹验证，验证失败对话框不会消息，用户可以手动关闭对话框，此时当做验证失败处理
	 * @return 验证成功返回指纹对应UniqueID；验证失败返回""
     */
	@JavascriptInterface
    String getFingerprintUniqueID();


	/** 不显示对话框进行验证，验证只能执行一次，不论成功失败，本次验证结束 */
    String identifyWithoutDialog();
	

	/** 判断当前设备是否支持指纹识别 */
    boolean isFingerprintSupported();

	/** 进行指纹采集，采集成功后返回指纹ID；采集失败返回"" */
    String register();

	/** 取消本次验证 */
    void cancelIdentify();

	boolean hasRegisteredFinger();
	

}
