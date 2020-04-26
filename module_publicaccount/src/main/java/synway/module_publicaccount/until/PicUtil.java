package synway.module_publicaccount.until;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import synway.module_interface.config.BaseUtil;

import static synway.module_interface.config.BaseUtil.getFolderPath;

/**
 * Created by admin on 2017/4/10.
 */

public class PicUtil {
    public static  String getPath(String id) {
        return BaseUtil.FILE_HEAD_IMG_THU + "/" + id;
    }
    public static  String getPublicBigPath(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_ORI + "/" + id;
    }
    public static String getApkPath(){
        return getFolderPath()+"/PublicAccount/apk";
    }
    /****
     * 替换网址中的ip和port
     * @param url 原地址
     * @param ip 需要替换的Ip   不能为空
     * @param port  需要替换的port  不能为空
     * @return 更改过后的网址（如果传进来的网址为空，则返回的也是空）
     */
    public  static String getIpPortFromUrl(String url,String ip,int port) {
        // 1.判断是否为空
        if (url == null || url.trim().equals("")) {
            return null;
        }
        String host = "";
        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+(:\\d{0,5})?");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group() ;
        }
        return url.replace(host,ip+":"+port);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 通过Base32将path转换成Base64字符串
     */
    public static String Bitmap2StrByBase64(String path) {
        Bitmap bit = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
