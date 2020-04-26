package qyc.library.tool.drawbitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class DrawBmp_Circle {
	private Canvas canvas;
	private Paint p;
	private int radius_px;

	public DrawBmp_Circle(int color, int width_px, int radius_px) {
		this.radius_px = radius_px;
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
	public Bitmap drawBitmap() {
		// 建立一张新图片,大小为圆的外切正方形
		Bitmap bitmap = Bitmap.createBitmap(radius_px, radius_px,
				Bitmap.Config.ARGB_8888);
		// 新图片放到画布上,旧图被取走时就意味着已经不能再用了
		canvas.setBitmap(bitmap);
		// 开始画
		paintCircle();
		return bitmap;
	}

	/** 画圆 */
	protected void paintCircle() {
		RectF rectF = new RectF(0, 0, radius_px, radius_px);
		canvas.drawArc(rectF, 0, 360, false, p);
	}
}
