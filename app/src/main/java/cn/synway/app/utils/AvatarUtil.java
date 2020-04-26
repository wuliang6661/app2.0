package cn.synway.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;

import java.util.List;

import cn.synway.app.R;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/810:12
 * desc   :
 * version: 1.0
 */
public class AvatarUtil {

    public static Builder getBuilder(Context context) {
        return new Builder(context);
    }

    public static class Builder {

        private Context mContext;
        private List<Object> mList;                      // 数据源
        private int mWidth = 50;                         // 控件宽度
        private int mHeight = 50;                        // 控件高度

        private int mShape = Shape.CIRCLE;               // 控件形状
        private int mRoundAngel = 10;                    // 圆角大小
        private int mMarginWidth = 4;                    // 图片间隙
        private int mMarginColor = R.color.f_white;         // 图片间隙颜色
        private boolean hasEdge = true;                  // 是否包含边缘

        private float mTextSize = 50;                           // 文字大小
        private int mTextColor = R.color.colorPrimary;          // 文字颜色
        private int mBackGroundColor = R.color.blue_color;     // 文字背景颜色

        private Builder(Context context) {
            this.mContext = context;
        }

        /**
         * 设置数据源
         */
        public Builder setList(List<Object> mList) {
            this.mList = mList;
            return this;
        }

        /**
         * 设置图片尺寸
         */
        public Builder setBitmapSize(int mWidth, int mHeight) {
            if (mWidth > 0) {
                this.mWidth = mWidth;
            }

            if (mHeight > 0) {
                this.mHeight = mHeight;
            }
            return this;
        }

        /**
         * 设置展示类型（圆形、圆角、方形）
         */
        public Builder setShape(int mShape) {
            this.mShape = mShape;
            return this;
        }

        /**
         * 设置圆角角度
         * 当shape设置为Shape.Round时读取改属性
         *
         * @param mRoundAngel 圆角角度
         */
        public Builder setRoundAngel(int mRoundAngel) {
            this.mRoundAngel = mRoundAngel;
            return this;
        }

        /**
         * 设置分割线宽度
         */
        public Builder setMarginWidth(int mMarginWidth) {
            this.mMarginWidth = mMarginWidth;
            return this;
        }

        /**
         * 设置分割线颜色
         */
        public Builder setMarginColor(int mMarginColor) {
            this.mMarginColor = mMarginColor;
            return this;
        }

        /**
         * 设置文字大小
         */
        public Builder setTextSize(int mTextSize) {
            this.mTextSize = mTextSize;
            return this;
        }

        /**
         * 设置文字颜色
         */
        public Builder setTextColor(int mTextColor) {
            this.mTextColor = mTextColor;
            return this;
        }

        public Builder setHasEdge(boolean hasEdge) {
            this.hasEdge = hasEdge;
            return this;
        }

        public Bitmap create() {

            final Bitmap result = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawPath(drawShapePath(), paint);
            float[] marginPath;

            final int listSize = mList.size();
            switch (listSize) {
                case 1:
                    startDraw(canvas, mList.get(0), DrawPosition.WHOLE);
                    break;
                case 2:
                    startDraw(canvas, mList.get(0), DrawPosition.LEFT);
                    startDraw(canvas, mList.get(1), DrawPosition.RIGHT);
                    marginPath = new float[]{mWidth / 2, 0, mWidth / 2, mHeight};
                    drawMarginLine(canvas, marginPath);
                    break;
                case 3:
                    startDraw(canvas, mList.get(0), DrawPosition.LEFT);
                    startDraw(canvas, mList.get(1), DrawPosition.RIGHT_TOP);
                    startDraw(canvas, mList.get(2), DrawPosition.RIGHT_BOTTOM);
                    marginPath = new float[]{mWidth / 2, 0,
                            mWidth / 2, mHeight,
                            mWidth / 2, mHeight / 2,
                            mWidth, mHeight / 2};
                    drawMarginLine(canvas, marginPath);
                    break;
                default:
                    startDraw(canvas, mList.get(0), DrawPosition.LEFT_TOP);
                    startDraw(canvas, mList.get(1), DrawPosition.LEFT_BOTTOM);
                    startDraw(canvas, mList.get(2), DrawPosition.RIGHT_TOP);
                    startDraw(canvas, mList.get(3), DrawPosition.RIGHT_BOTTOM);
                    marginPath = new float[]{mWidth / 2, 0,
                            mWidth / 2, mHeight,
                            0, mHeight / 2,
                            mWidth, mHeight / 2};
                    drawMarginLine(canvas, marginPath);
                    break;
            }
            // 仅方形支持边缘  且单个文字不支持边缘
            if (hasEdge && mShape == Shape.SQUARE && !(mList.size() == 1 && mList.get(0) instanceof String)) {
                drawEdge(canvas);
            }

            return result;
        }

        /**
         * 根据边角配置绘制画布path
         */
        private Path drawShapePath() {
            Path mPath = new Path();
            switch (mShape) {
                case Shape.ROUND:
                    mPath.addRoundRect(new RectF(0, 0, mHeight, mWidth), mRoundAngel, mRoundAngel, Path.Direction.CCW);
                    break;
                case Shape.SQUARE:
                    mPath.addRect(new RectF(0, 0, mHeight, mWidth), Path.Direction.CCW);
                    break;
                case Shape.CIRCLE:
                    int radius = Math.max(mWidth, mHeight) / 2;
                    mPath.addCircle(mWidth / 2, mHeight / 2, radius, Path.Direction.CCW);
                    break;
            }

            return mPath;
        }

        /**
         * 根据数据源类型区分绘制图片或文字
         */
        private void startDraw(Canvas canvas, Object resource, int position) {
            if (resource instanceof Bitmap) {
                drawBitmap(canvas, (Bitmap) resource, position);
            } else if (resource instanceof String) {
                drawText(canvas, (String) resource, position);
            }
        }

        /**
         * 绘制图片
         * 最多支持四张图
         */
        private void drawBitmap(Canvas canvas, Bitmap bitmap, int mode) {

            int left, top;
            int x, y, width, height;
            int dstWidth, dstHeight;

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            if (mode == DrawPosition.WHOLE) {
                // 比例缩放
                Bitmap bmp = Bitmap.createScaledBitmap(bitmap, mWidth, mHeight, false);
                canvas.drawBitmap(bmp, 0, 0, paint);
            } else if (mode == DrawPosition.LEFT) {
                dstWidth = mWidth;
                dstHeight = mHeight;

                x = mWidth / 4 + mMarginWidth / 4;
                y = 0;
                width = mWidth / 2 - mMarginWidth / 4;
                height = mHeight;

                left = 0;
                top = 0;

                // 比例缩放
                Bitmap bmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
                // 裁取中间部分(从x点裁取置顶距离)
                Bitmap dstBmp = Bitmap.createBitmap(bmp, x, y, width, height);
                // 绘图
                canvas.drawBitmap(dstBmp, left, top, paint);
            } else if (mode == DrawPosition.RIGHT) {
                dstWidth = mWidth;
                dstHeight = mHeight;

                x = mWidth / 4 + mMarginWidth / 4;
                y = 0;
                width = mWidth / 2 - mMarginWidth / 4;
                height = mHeight;

                left = mWidth / 2 + mMarginWidth / 4;
                top = 0;

                // 比例缩放
                Bitmap bmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
                // 裁取中间部分(从x点裁取置顶距离)
                Bitmap dstBmp = Bitmap.createBitmap(bmp, x, y, width, height);
                // 绘图
                canvas.drawBitmap(dstBmp, left, top, paint);
            } else if (mode == DrawPosition.LEFT_TOP) {
                dstWidth = mWidth / 2 - mMarginWidth / 4;
                dstHeight = mHeight / 2 - mMarginWidth / 4;

                left = 0;
                top = 0;

                // 比例缩放
                Bitmap bmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
                // 绘图
                canvas.drawBitmap(bmp, left, top, paint);
            } else if (mode == DrawPosition.LEFT_BOTTOM) {
                dstWidth = mWidth / 2 - mMarginWidth / 4;
                dstHeight = mHeight / 2 - mMarginWidth / 4;

                left = 0;
                top = mHeight / 2 + mMarginWidth / 4;

                // 比例缩放
                Bitmap bmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
                // 绘图
                canvas.drawBitmap(bmp, left, top, paint);
            } else if (mode == DrawPosition.RIGHT_TOP) {
                dstWidth = mWidth / 2 - mMarginWidth / 4;
                dstHeight = mHeight / 2 - mMarginWidth / 4;

                left = mWidth / 2 + mMarginWidth / 4;
                top = 0;

                // 比例缩放
                Bitmap bmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
                // 绘图
                canvas.drawBitmap(bmp, left, top, paint);
            } else if (mode == DrawPosition.RIGHT_BOTTOM) {
                dstWidth = mWidth / 2 - mMarginWidth / 4;
                dstHeight = mHeight / 2 - mMarginWidth / 4;

                left = mWidth / 2 + mMarginWidth / 4;
                top = mHeight / 2 + mMarginWidth / 4;

                // 比例缩放
                Bitmap bmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
                // 绘图
                canvas.drawBitmap(bmp, left, top, paint);
            }
        }

        /**
         * 绘制文字
         */
        private void drawText(Canvas canvas, String text, int mode) {
            float bgLeft = 0, bgTop = 0, bgRight = 0, bgBottom = 0;
            float textSize = mTextSize;

            Paint textBgPaint = new Paint();
            textBgPaint.setColor(ContextCompat.getColor(mContext, mBackGroundColor));
            textBgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            if (mode == DrawPosition.WHOLE) {
                bgLeft = 0;
                bgTop = 0;
                bgRight = mWidth;
                bgBottom = mHeight;
                textSize = mWidth / 2;
            } else if (mode == DrawPosition.LEFT) {
                bgLeft = 0;
                bgTop = 0;
                bgRight = mWidth / 2 - mMarginWidth / 4;
                bgBottom = mHeight;
                textSize = mWidth / 4;
            } else if (mode == DrawPosition.RIGHT) {
                bgLeft = mWidth / 2 + mMarginWidth / 4;
                bgTop = 0;
                bgRight = mWidth;
                bgBottom = mHeight;
                textSize = mWidth / 4;
            } else if (mode == DrawPosition.LEFT_TOP) {
                bgLeft = 0;
                bgTop = 0;
                bgRight = mWidth / 2 - mMarginWidth / 4;
                bgBottom = mHeight / 2 - mMarginWidth / 4;
                textSize = mWidth / 5;
            } else if (mode == DrawPosition.LEFT_BOTTOM) {
                bgLeft = 0;
                bgTop = mHeight / 2 + mMarginWidth / 4;
                bgRight = mWidth / 2 - mMarginWidth / 4;
                bgBottom = mHeight;
                textSize = mWidth / 5;
            } else if (mode == DrawPosition.RIGHT_TOP) {
                bgLeft = mWidth / 2 + mMarginWidth / 4;
                bgTop = 0;
                bgRight = mWidth;
                bgBottom = mHeight / 2 - mMarginWidth / 4;
                textSize = mWidth / 5;
            } else if (mode == DrawPosition.RIGHT_BOTTOM) {
                bgLeft = mWidth / 2 + mMarginWidth / 4;
                bgTop = mHeight / 2 + mMarginWidth / 4;
                bgRight = mWidth;
                bgBottom = mHeight;
                textSize = mWidth / 5;
            }

            RectF rect = new RectF(bgLeft, bgTop, bgRight, bgBottom);
            canvas.drawRect(rect, textBgPaint);

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setAntiAlias(true);
            textPaint.setColor(ContextCompat.getColor(mContext, mTextColor));
            textPaint.setTextSize(Math.min(mTextSize, textSize));

            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();

            int baseline = (int) ((bgBottom + bgTop - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(text, rect.centerX(), baseline, textPaint);
        }

        /**
         * 绘制边缘线
         */
        private void drawEdge(Canvas canvas) {
            Paint edgePaint = new Paint();
            edgePaint.setStrokeWidth(mMarginWidth);
            edgePaint.setStyle(Paint.Style.STROKE);
            edgePaint.setColor(ContextCompat.getColor(mContext, mMarginColor));

            Path mPath = new Path();
            mPath.moveTo(0, 0);
            mPath.lineTo(0, mHeight);
            mPath.lineTo(mWidth, mHeight);
            mPath.lineTo(mWidth, 0);
            mPath.close();

            canvas.drawPath(mPath, edgePaint);
        }

        /**
         * 绘制分割线
         */
        private void drawMarginLine(Canvas canvas, float[] path) {
            Paint marginPaint = new Paint();
            marginPaint.setStrokeWidth(mMarginWidth / 2);
            marginPaint.setColor(ContextCompat.getColor(mContext, mMarginColor));
            canvas.drawLines(path, marginPaint);
        }
    }

    public interface Shape {
        int ROUND = 0X33;
        int CIRCLE = 0X11;
        int SQUARE = 0X22;
    }

    interface DrawPosition {
        int WHOLE = 0;
        int LEFT = 1;
        int RIGHT = 2;
        int LEFT_TOP = 3;
        int LEFT_BOTTOM = 4;
        int RIGHT_TOP = 5;
        int RIGHT_BOTTOM = 6;
    }
}

