package qyc.library.tool.drawbitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DrawBmp_Arrow {

	private Canvas canvas;
	private Paint p;
	private int length_px;

	public DrawBmp_Arrow(int color, int width_px, int length_px) {
		this.length_px = length_px;
		canvas = new Canvas();
		canvas.drawColor(Color.argb(0, 0, 0, 0));

		p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true);
		p.setColor(color);
		p.setStrokeWidth(width_px);
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStyle(Paint.Style.STROKE);
	} 

	// 建立新图片是不可避免的
	// 如果画布每次画在新的图片上,当图片被取走,画布便需要重新使用一张新图,旧图不可再画
	// 如果画布每次画在同一张图上,那么被取走的则必须是图片副本,所以仍然要建立新图片.
	public Bitmap drawBitmap(int screenAngle) {
		// 建立一张新图片,大小为以箭头长度为半径的圆的外切正方形
		Bitmap bitmap = Bitmap.createBitmap(length_px * 2, length_px * 2,
				Bitmap.Config.ARGB_8888);
		// 新图片放到画布上,旧图被取走时就意味着已经不能再用了
		canvas.setBitmap(bitmap);
		// 开始画
		double screenRadius = Math.PI * 2 * screenAngle / 360;
		// 先转为弧度,再得出正弦值和余弦值 360度=2π
		double SINE = Math.sin(screenRadius);
		double COS = Math.cos(screenRadius);
		// length_px既是中心点的坐标,又是画出的线的长度
		float newPointX = (float) (length_px + length_px * COS);
		float newPointY = (float) (length_px - length_px * SINE);

		paintArrow(length_px, length_px, newPointX, newPointY);
		return bitmap;
	}

	/*
	 * =========================画箭头=========================
	 */
	private static final float ARROW_LENG = 20; // 箭头的长度
	// 由X轴长度和高度得出箭头其中一条线相对于直线的角度
	private static final float ARROW_AWARD1 = (float) (Math.PI / 6);// 30°表示的弧度
	private static final float ARROW_ANG_SINE1 = (float) Math.sin(ARROW_AWARD1);// 角度的正弦值
	private static final float ARROW_ANG_COS1 = (float) Math.cos(ARROW_AWARD1);// 角度的余弦值
	// 该弧度反向取值即箭头在直线另一边的那条线
	private static final float ARROW_AWARD2 = -ARROW_AWARD1;
	private static final float ARROW_ANG_SINE2 = (float) Math.sin(ARROW_AWARD2);// 角度的正弦值
	private static final float ARROW_ANG_COS2 = (float) Math.cos(ARROW_AWARD2);// 角度的余弦值

	/** 画箭头 */
	protected void paintArrow(float x1, float y1, float x2, float y2) {
		float midX = x2;
		float midY = y2;

		float newx1 = (midX - x1) * ARROW_ANG_COS1 - (midY - y1)
				* ARROW_ANG_SINE1;
		float newy1 = (midX - x1) * ARROW_ANG_SINE1 + (midY - y1)
				* ARROW_ANG_COS1;
		float d = (float) Math.sqrt(newx1 * newx1 + newy1 * newy1);
		newx1 = newx1 / d * ARROW_LENG;
		newy1 = newy1 / d * ARROW_LENG;

		float newx2 = (midX - x1) * ARROW_ANG_COS2 - (midY - y1)
				* ARROW_ANG_SINE2;
		float newy2 = (midX - x1) * ARROW_ANG_SINE2 + (midY - y1)
				* ARROW_ANG_COS2;
		newx2 = newx2 / d * ARROW_LENG;
		newy2 = newy2 / d * ARROW_LENG;

		// 画线
		canvas.drawLine(x1, y1, x2, y2, p);
		// 画箭头的一半
		canvas.drawLine(midX, midY, midX - newx1, midY - newy1, p);
		// 画箭头的另一半
		canvas.drawLine(midX, midY, midX - newx2, midY - newy2, p);
	}

}
