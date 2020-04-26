package cn.synway.app.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/1515:02
 * desc   :
 * version: 1.0
 */
public class AESUtils {

    //初始向量（偏移）
    public static final String iv = "7983B5439EF75A69";   //AES 为16bytes. DES 为8bytes

    //编码方式
    //public static final String bm = "utf-8";

    //私钥  （密钥）
    private static final String key = "7983b5439ef75a69";   //AES固定格式为128/192/256 bits.即：16/24/32bytes。DES固定格式为128bits，即8bytes。

    /**
     * 加密
     *
     * @param data 加密前的字符串
     * @return 加密后的字节数组
     */
    public static byte[] encrypt(String data) {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();

            //判断待加密的字节数组的长度，在此长度基础上扩展一个字节数组的长度为16的倍数
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            //创建需新的待加密的字节数组，将上面的字节数组复制进来，多余的补0
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            //加密后的字节数组
            byte[] encrypted = cipher.doFinal(plaintext);

            return encrypted;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     *
     * @param encrypted1 解密前的字节数组
     * @return 解密后的字符串
     */
    public static String decrypt(byte[] encrypted1) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            //解密后的字节数组
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}