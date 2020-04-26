package com.oliveapp.liveness.sample.libsaasclient.backend;

import android.annotation.TargetApi;
import android.os.Build;

import com.oliveapp.libcommon.utility.LogUtil;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Locale;

/**
 * Created by zmzhang on 10/21/15.
 */
public class HashUtil {

    public static class Hex {

        private static final char[] toDigit = ("0123456789ABCDEF").toCharArray();
        private final static String sHexStr = "0123456789ABCDEF";

        public static String encode(byte[] bytes) {
            char[] chars = new char[2 * bytes.length];
            int j = 0;

            for (int i = 0; i < bytes.length; ++i) {
                byte bits = bytes[i];

                chars[j++] = toDigit[((bits >>> 4) & 0xF)];
                chars[j++] = toDigit[(bits & 0xF)];
            }

            return new String(chars);
        }

        public static byte[] decode(String hexStr) {
            hexStr = hexStr.toString().trim().replace(" ", "").toUpperCase(Locale.US);
            char[] hexs = hexStr.toCharArray();
            byte[] bytes = new byte[hexStr.length() / 2];
            int iTmp = 0x00;;

            for (int i = 0; i < bytes.length; i++){
                iTmp = sHexStr.indexOf(hexs[2 * i]) << 4;
                iTmp |= sHexStr.indexOf(hexs[2 * i + 1]);
                bytes[i] = (byte) (iTmp & 0xFF);
            }
            return bytes;
        }
    }

    public static class MD5 {

        private final static String TAG = MD5.class.getSimpleName();

        public static class Md5EncodingException extends Exception
        {
            public Md5EncodingException() {}
            public Md5EncodingException(String msg)
            {
                super(msg);
            }
        }

        /**
         * Generate MD5 hash code from a given string
         * @param message, message to be encoded
         * @return encoded MD5 hash string, 32 bytes in hex code
         * @throws Md5EncodingException
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        public static String md5(String message) throws Md5EncodingException {
            try {
                // Generate MD5 hash code (16 bytes)
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(message.getBytes(Charset.forName("UTF-8")));
                byte messageDigest[] = digest.digest();

                // Encode the 16 bytes hash code to 32 bytes hex code.
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < messageDigest.length; i++)
                    hexString.append(Integer.toHexString((messageDigest[i] & 0xFF) | 0x100).substring(1,3));

                return hexString.toString();
            } catch (Exception e) {
                LogUtil.e(TAG, "md5 encoding: ", e);
                throw new Md5EncodingException("Cannot generate MD5 hash string.");
            }
        }
    }


}
