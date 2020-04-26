package cn.synway.app.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/2017:13
 * desc   :
 * version: 1.0
 */
public class ImageUtils {


    /**
     * byteArr转bitmap
     *
     * @param bytes 字节数组
     * @return bitmap
     */
    public static Bitmap bytes2Bitmap(byte[] bytes) {
        return (bytes == null || bytes.length == 0) ? null : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Bitmap 通过Base64 转换为字符串
     *
     * @param bitmap
     * @return
     */
    public static String getBitmapStrBase64(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bytes = bos.toByteArray();
        String string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

}
