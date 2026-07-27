package com.choicemmed.s1blelibrary.cmd.parse;

/**
 * Author：ZhengZhong on 2016/10/27 09:45
 */

public class S1ParseData {
    private static final String TAG = "S1ParseData";


    //
    public static boolean parseMatchResult(String data) {
        int code = Integer.parseInt(data.substring(8, 10), 16);
        if (code == 0)
            return true;
        return false;
    }

    public static double parseWeight(byte[] data) {
        if (data.length > 6) {
            return (((data[6] & 0xff) << 8) + (data[5] & 0xff)) * 0.1;
        }
        return 0;
    }

    public static boolean parseUnitResult(byte[] data) {
        return (data[3] & 0xff) == 0xf0;
    }
}