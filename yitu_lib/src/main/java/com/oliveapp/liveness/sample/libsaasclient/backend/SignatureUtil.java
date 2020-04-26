package com.oliveapp.liveness.sample.libsaasclient.backend;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

import com.oliveapp.libcommon.utility.LogUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zmzhang on 10/21/15.
 */
public class SignatureUtil {
    private final static String TAG = SignatureUtil.class.getSimpleName();

    public static class RSAHelper {

        public static class PublicKeyException extends Exception {
            public PublicKeyException() {
            }

            public PublicKeyException(String msg) {
                super(msg);
            }
        }

        /**
         * Load public key from string
         *
         * @param publicKeyStr public key in string format
         * @return PublicKey
         * @throws PublicKeyException
         */
        @TargetApi(Build.VERSION_CODES.FROYO)
        public static PublicKey loadPublicKey(String publicKeyStr) throws PublicKeyException {
            try {
                byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
                KeyFactory keyFactory;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    keyFactory = KeyFactory.getInstance("RSA");     //适配Android P及以后版本，否则报错NoSuchAlgorithmException
//                } else {
                keyFactory = KeyFactory.getInstance("RSA", "BC");
//                }
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
                return (RSAPublicKey) keyFactory.generatePublic(keySpec);
            } catch (Exception e) {
                LogUtil.e(TAG, "Load public key str", e);
                throw new PublicKeyException("Cannot load public key from string");
            }
        }

        /**
         * Load public key from InputStream
         *
         * @param in InputStream of public key
         * @throws PublicKeyException
         */
        public static PublicKey loadPublicKey(InputStream in) throws Exception {
            try {
                return loadPublicKey(readKey(in));
            } catch (Exception e) {
                LogUtil.e(TAG, "Load public key InputStream", e);
                throw new PublicKeyException("Cannot load public key from InputStream");
            }
        }

        /**
         * Generate public key in string format from InputStream
         *
         * @param in InputStream of public key
         * @return string represents the public key
         * @throws IOException
         */
        private static String readKey(InputStream in) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }

            return sb.toString();
        }

        /**
         * Encrypt message using public key
         *
         * @param message   string before encrypt
         * @param publicKey PublicKey object
         * @return byte[] content of the encrypted message
         * @throws PublicKeyException
         */
        public static byte[] encrypt(byte[] message, PublicKey publicKey) throws PublicKeyException {
            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] enBytes = cipher.doFinal(message);

                return enBytes;
            } catch (Exception e) {
                e.printStackTrace();
                throw new PublicKeyException("Cannot encrypt message with public key");
            }
        }
    }

    public static class AESHelper {
        public static String keyPart1 = "YITU.INC";

        public static byte[] encrypt(byte[] message) {
            try {
                SecretKeySpec keySpec = new SecretKeySpec(("CopyRight " + keyPart1 + " 2015!").getBytes(), "AES");

                byte[] IV = "1234567890123456".getBytes();
                IvParameterSpec IVSpec = new IvParameterSpec(IV);
                Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, IVSpec);

                byte[] encrypted = cipher.doFinal(message);

                return encrypted;
            } catch (NoSuchAlgorithmException e) {
                LogUtil.e(TAG, "[AES Encrypt] NoSuchAlgorithmException...", e);
            } catch (NoSuchPaddingException e) {
                LogUtil.e(TAG, "[AES Encrypt] NoSuchPaddingException...", e);
            } catch (InvalidAlgorithmParameterException e) {
                LogUtil.e(TAG, "[AES Encrypt] InvalidAlgorithmParameterException...", e);
            } catch (InvalidKeyException e) {
                LogUtil.e(TAG, "[AES Encrypt] InvalidKeyException...", e);
            } catch (BadPaddingException e) {
                LogUtil.e(TAG, "[AES Encrypt] BadPaddingException...", e);
            } catch (IllegalBlockSizeException e) {
                LogUtil.e(TAG, "[AES Encrypt] IllegalBlockSizeException...", e);
            }
            return null;
        }
    }

    /**
     * Generate signature
     *
     * @param publicKey          public key used to encrypt message
     * @param accessKey          access Key
     * @param bodyString         http body content in string format
     * @param userDefinedContent user defined content, must be fewer than 41 bytes
     * @return generated encrypted message
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String generateSignature(PublicKey publicKey, String accessKey, String bodyString, String userDefinedContent) throws Exception {

        String result = null;

        // Generate Unix timestamp
        int unixTime = (int) (System.currentTimeMillis() / 1000L);
        byte[] unixTimeArray = ByteBuffer.allocate(4).putInt(unixTime).array();

        // Generate random number
        SecureRandom sr = new SecureRandom();
        byte[] rndBytes = new byte[8];
        sr.nextBytes(rndBytes);

        // Combine signature
        byte[] signatureStr = mergeArray(accessKey.getBytes(Charset.forName("UTF-8")), HashUtil.MD5.md5(bodyString).getBytes(Charset.forName("UTF-8")));
        signatureStr = mergeArray(signatureStr, unixTimeArray);
        signatureStr = mergeArray(signatureStr, rndBytes);
        signatureStr = mergeArray(signatureStr, userDefinedContent.getBytes(Charset.forName("UTF-8")));

        // Encrypt signature
        result = HashUtil.Hex.encode(RSAHelper.encrypt(signatureStr, publicKey));

        return result;
    }

    /**
     * Combine two byte arrays
     *
     * @param a
     * @param b
     * @return a + b
     */
    public static byte[] mergeArray(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

}
