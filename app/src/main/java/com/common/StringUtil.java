package com.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringUtil {
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1) {
            throw new IllegalArgumentException("this byteArray must not be null or empty");
        }
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 255) < 16) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(byteArray[i] & 255));
        }
        return hexString.toString().toLowerCase();
    }

    public static final byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        while (true) {
            int rc = inStream.read(buff, 0, 100);
            if (rc <= 0) {
                return swapStream.toByteArray();
            }
            swapStream.write(buff, 0, rc);
        }
    }

    public static byte[] toByteArray(String hexString) {
        if (!hexString.isEmpty()) {
            String hexString2 = hexString.toLowerCase();
            byte[] byteArray = new byte[(hexString2.length() / 2)];
            int k = 0;
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) ((((byte) (Character.digit(hexString2.charAt(k), 16) & 255)) << 4) | ((byte) (Character.digit(hexString2.charAt(k + 1), 16) & 255)));
                k += 2;
            }
            return byteArray;
        }
        throw new IllegalArgumentException("this hexString must not be empty");
    }
}