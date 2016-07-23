package org.taoningyu.tools;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

import org.apache.commons.io.FileUtils;

/**
 * Md5工具类
 */
public class Md5Utils {

    public static String getMd5(byte[] byteArray) {

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String getMd5(File tmpFile) throws IOException {

        char[] charArray = FileUtils.readFileToString(tmpFile, "utf-8").toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }

        return getMd5(byteArray);

    }
}
