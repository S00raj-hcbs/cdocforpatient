package com.choicemmed.s1blelibrary.utils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Yu Baoxiang on 2015/3/27.
 */
public class ByteUtils {

    public static byte[] cmdString2Bytes(String cmd, boolean withSumCode) {
        byte[] value = hexString2Bytes(cmd);
        if (withSumCode) {
            byte verifySum = 0;
            for (int i = 2; i < value.length; i++) {
                verifySum += value[i];
            }
            byte[] values = new byte[value.length + 1];
            for (int i = 0; i < value.length; i++) {
                values[i] = value[i];
            }
            values[value.length] = verifySum;
            return values;
        } else {
            return value;
        }
    }

    public static byte getSum(String cmd) {
        int sum = 0;
        byte[] value = hexString2Bytes(cmd);
        for (int i = 2; i < value.length; i++) {
            sum += value[i];
        }
        return (byte) (sum & 0x000000ff);
    }


    public static byte[] reverseBytes(byte[] a) {
        int len = a.length;
        byte[] b = new byte[len];
        for (int k = 0; k < len; k++) {
            b[k] = a[a.length - 1 - k];
        }
        return b;
    }


    /**
     * byte数组转十六进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bytes2HexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xff);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result += hex.toLowerCase(Locale.getDefault());
        }
        return result;
    }

    /**
     * 十六进制字符串转byte数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexString2Bytes(String hexString) {
        int len = hexString.length() / 2;
        char[] chars = hexString.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }
    /**
     * 十六进制字符串倒叙
     *
     * @param hexString
     * @return
     */
    public static String hexStringReverse(String hexString) {
        int len = hexString.length() / 2;
        char[] chars = hexString.toCharArray();
        String[] hexStr = new String[len];
        String result = "";
        for (int i = 0, j = len - 1; j >= 0; i += 2, j--) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            result = hexStr[j] + result;
        }

        return result;
    }

    /**
     * 十六进制字符串转ArrayList<Byte>
     *
     * @param hexString
     * @return
     */
    public static ArrayList<Byte> hexString2List(String hexString) {
        int len = hexString.length() / 2;
        char[] chars = hexString.toCharArray();
        String[] hexStr = new String[len];
        ArrayList<Byte> list = new ArrayList<Byte>();
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            list.add((byte) Integer.parseInt(hexStr[j], 16));
        }
        return list;
    }


    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }


    /**
     * 将int数值转换为占2个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
     */
    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[2];
        src[1] = (byte) (((value) >> 8) & 0xff);
        src[0] = (byte) (value & 0xff);
        return src;
    }


    /**
     * 生成16进制累加和校验码
     *
     * @param data 除去校验位的数据
     * @return
     */
    public static String makeChecksum(String data) {
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
//            System.out.println(s);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        //如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;
    }


}
