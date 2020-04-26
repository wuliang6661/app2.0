package synway.module_publicaccount.push;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class ImageCompression {

//	/** 根据预设的DP值得到像素 */
//	public static final int dip2px(float resDensity, float dipValue) {
//		return (int) (dipValue * resDensity + 0.5f);
//	}

	/**
	 * 获取图片缩略图
	 *
	 * @param targetPath
	 *            缩略图存放的地址
	 * @param oriPath
	 *            原图地址
	 * @param maxLength
	 *            要生产缩略图最长边的值
	 * @return 成功返回缩略图地址， 失败返回null
	 */
	public static final String compJpg(String targetPath, String oriPath,
			int maxLength) {
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		try {
			// 获取原图的宽
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(oriPath, options);
			int width = options.outWidth;
			int height = options.outHeight;

			// 先获取长宽比例
			double scale = (double) width / height;

			// 根据较长边,来决定缩放比例
			int length = 0;
			if (width > height) {
				length = width;
				width = maxLength;
				height = (int) Math.round(width / scale);
			} else {
				length = height;
				height = maxLength;
				width = (int) Math.round(height * scale);
			}

			// 设置缩放比例
			options = new BitmapFactory.Options();
			if (length > maxLength) {
				options.inSampleSize = length / maxLength;
				// 获取图片,这是根据简单比例对折获取的图片,要么正好要么比预定尺寸偏大.
				bitmap = BitmapFactory.decodeFile(oriPath, options);
				// 这是微调
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);
				// 图片转正
				int degree = readPictureDegree(oriPath);
				bitmap = rotaingImageView(degree,bitmap);

				if (bitmap != null) {
					File picFile = new File(targetPath);
					picFile.delete();
					fos = new FileOutputStream(picFile);
					bitmap.compress(CompressFormat.JPEG, 80, fos);
					fos.flush();
				}
			}else{
				// 图片转正
				bitmap = BitmapFactory.decodeFile(oriPath, options);
				int degree = readPictureDegree(oriPath);
				bitmap = rotaingImageView(degree,bitmap);
				if (bitmap != null) {
					File picFile = new File(targetPath);
					picFile.delete();
					fos = new FileOutputStream(picFile);
					bitmap.compress(CompressFormat.JPEG, 100, fos);
					fos.flush();
				}

			}
		} catch (Exception e) {
			return null;
		} finally {
			if (null != bitmap) {
				bitmap.recycle();
				bitmap = null;
			}
			if (null != fos) {
				try {
					fos.close();
					fos = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return targetPath;
	}

	/**
	 * 旋转图片
	 * @param bitmap
	 * @return Bitmap
	 */
	private static Bitmap rotaingImageView(int degree, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
//		 
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	private static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

}
