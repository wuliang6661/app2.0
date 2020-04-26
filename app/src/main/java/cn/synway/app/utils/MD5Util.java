package cn.synway.app.utils;

public class MD5Util {

    public static void main(String[] args) {

    }

    public static String encode(String str) {
        if (str == null) {
            return null;
        }
        try {
            int i = 0, j = 0, k = 0, len = 0;
            char key[] = {0x55, 0x3c, 0x1b, 0x37, 0x7a, 0x48, 0x7f, 0x5c, 0x34, 0x07, 0x00}; // 5c
            len = str.length();
            for (i = 0; i < len; i++) {
                if (str.charAt(i) < 0) {
                    return null;
                }
            }
            char strArray[] = new char[20];
            for (i = 0; i < str.length() && i < 20; i++) {
                strArray[i] = str.charAt(i);
            }
            for (i = str.length(); i < 20; i++) {
                strArray[i] = '\0';
            }
            for (i = 0, j = 0; i < len && i < 20; i++) {
                strArray[i] = (char) (strArray[i] ^ key[j]);
                if (strArray[i] == 0x00 || strArray[i] == 0x14 || strArray[i] == 39 || strArray[i] == 22 || strArray[i] == 37 || strArray[i] == 42 || strArray[i] == 32) {
                    for (k = len; k > i; k--) {
                        strArray[k] = strArray[k - 1];
                    }
                    strArray[i + 1] = (char) (strArray[i] + 1);
                    strArray[i] = 0x14;
                    len++;
                    i++;
                }
                if (++j >= 10) {
                    j = 0;
                }
            }
            strArray[i] = '\0';
            String Kcvg_cn = String.valueOf(strArray).trim();
            return Kcvg_cn;
        } catch (Exception e) {
//			System.out.print("Encrypt error: " + e.getMessage());
            return null;
        }
    }

}
