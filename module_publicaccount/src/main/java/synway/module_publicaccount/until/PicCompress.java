package synway.module_publicaccount.until;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;

public class PicCompress {
    /**
     * 压缩图片
     *
     * @param bitmap          原图
     * @param maxMemmorrySize 转后上限 单位KB
     * @return 压缩后的bitmap
     */
    public static Bitmap compressQ(Bitmap bitmap, int maxMemmorrySize) {
        //进行有损压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options_ = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options_, baos);
        //质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
        int baosLength = baos.toByteArray().length;
        while (baosLength / 1024 > maxMemmorrySize) {//循环判断如果压缩后图片是否大于maxMemmorrySize,大于继续压缩
            baos.reset();
            //重置baos即让下一次的写入覆盖之前的内容
            options_ = Math.max(0, options_ - 10);
            // 图片质量每次减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options_, baos);//将压缩后的图片保存到baos中
            baosLength = baos.toByteArray().length;
            if (options_ == 0)//如果图片的质量已降到最低则，不再进行压缩
                break;
        }
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
    }


    /**
     * 压缩图片 尺寸+质量
     *
     * @param path
     * @return
     */
    public static Bitmap compressSize(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap==null) return null;
        //设置缩放比
        int wr = bitmap.getWidth() / 240;
        int hr = bitmap.getHeight() / 240;
        int radio = bitmap.getWidth() / 240 > bitmap.getHeight() / 240 ? bitmap.getWidth() / 240 : bitmap.getHeight() / 240;
        if(radio<=0) radio =1;
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth() / radio, bitmap.getHeight() / radio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        RectF rectF = new RectF(0, 0, bitmap.getWidth() / radio, bitmap.getHeight() / radio);
        //将原图画在缩放之后的矩形上
        canvas.drawBitmap(bitmap, null, rectF, null);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        result.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return result;
//        return BitmapFactory.decodeByteArray(bos.toByteArray(),0,bos.toByteArray().length);
    }
}
