package qyc.library.tool.qrcode;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class QRCode {

	/**
	 * 将二维码文本转换成图片,默认使用500*500像素
	 * 
	 * @param str
	 *            二维码文本
	 * @return
	 */
	public static final Bitmap createQRImage(String str) {
		return createQRImage(str, 500);
	}

	/**
	 * 将二维码文本转换成图片,使用自己定义的像素
	 * 
	 * @param str
	 *            二维码文本
	 * @param wh
	 *            宽和高，像素.宽高必须一致
	 * @return
	 */
	public static final Bitmap createQRImage(String str, int wh) {
		int QRWidth = wh;
		int QRHeight = wh;

		try {
			// 判断URL合法性
			if (str == null || "".equals(str) || str.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, QRWidth, QRHeight, hints);
			int[] pixels = new int[QRWidth * QRHeight];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QRHeight; y++) {
				for (int x = 0; x < QRWidth; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QRWidth + x] = 0xff000000;
					} else {
						pixels[y * QRWidth + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QRWidth, QRHeight, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QRWidth, 0, 0, QRWidth, QRHeight);
			// 显示到一个ImageView上面
			return bitmap;
		}
		catch (WriterException e) {
			return null;
		}
	}

}
