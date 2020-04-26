package synway.module_publicaccount.public_chat.adapter;

class ImageScaleDeal {

	/**
	 * @param width
	 *            图片宽度 [像素|dp]
	 * @param height
	 *            图片宽度 [像素|dp]
	 * @return result[0] imageView宽度(dp) result[1] imageview高度(dp)
	 */
	static final int[] getImageViewScale(int width, int height) {
		int result[] = new int[2];

		if (width > height) {
			double s = (double) width / (double) height;
			int resultWidth = 200;
			int resultHeight = (int) (resultWidth / s);

			result[0] = resultWidth;
			result[1] = resultHeight;
		} else {
			double s = (double) width / (double) height;
			int resultHeight = 40;
			int resultWidth = (int) (s * resultHeight);

			result[0] = resultWidth;
			result[1] = resultHeight;
		}

		return result;
	}
	
	/**
	 * @param width
	 *            图片宽度 [像素|dp]
	 * @param height
	 *            图片宽度 [像素|dp]
	 * @return result[0] imageView宽度(dp) result[1] imageview高度(dp)
	 */
	static final int[] getImageViewScale(int width, int height,int maxWidth) {
		int result[] = new int[2];

		if (width > height) {
			double s = (double) width / (double) height;
			int resultWidth = maxWidth;
			int resultHeight = (int) (resultWidth / s);

			result[0] = resultWidth;
			result[1] = resultHeight;
		} else {
			double s = (double) width / (double) height;
			int resultHeight = 40;
			int resultWidth = (int) (s * resultHeight);

			result[0] = resultWidth;
			result[1] = resultHeight;
		}

		return result;
	}

}
