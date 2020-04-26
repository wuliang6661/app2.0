package synway.module_publicaccount.qrcode;

import android.webkit.JavascriptInterface;

public interface QRCodeI {
	
	/**
	 * 扫描二维码，并返回结果；如果扫描失败，则返回空字符串""
	 * @return
	 */
	@JavascriptInterface
    void QRCodeInterfaceFn(String typeresult);

}
