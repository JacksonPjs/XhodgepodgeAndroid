package utils;

import android.util.Log;

/**
 * Created by yingc on 2017/11/29 0029.
 */

public class DataTypeUtil {

    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
           char b= hexChars[j];
           int i=b;
           Log.e("'char=",b+"");
        }

        return new String(hexChars);
    }
    /**
     * int 转 byte 数组
     *
     * @param intSource
     * @param byteArrayLen
     * @return
     */
    public static byte[] intToByteArray(int intSource, int byteArrayLen) {
        byte[] bLocalArr = new byte[byteArrayLen];
        for (int i = 0; (i < 4) && (i < byteArrayLen); i++) {
            bLocalArr[i] = (byte) (intSource >> 8 * (3 - i) & 0xFF);
        }
        return bLocalArr;
    }
    public static String decToHex(int dec) {
        String hex = "";
        while(dec != 0) {
            String h = Integer.toString(dec & 0xff, 16);
            if((h.length() & 0x01) == 1)
                h = '0' + h;
            hex = hex + h;
            dec = dec >> 8;
        }
        return hex;
    }


}
