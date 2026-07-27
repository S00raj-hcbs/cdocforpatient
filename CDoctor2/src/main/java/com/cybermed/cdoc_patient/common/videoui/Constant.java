package com.cybermed.cdoc_patient.common.videoui;

import io.agora.rtc2.RtcEngine;

public class Constant {

    public static final String MEDIA_SDK_VERSION;

    static {
        String sdk = "undefined";
        try {
            sdk = RtcEngine.getSdkVersion();
        } catch (Throwable e) {
        }
        MEDIA_SDK_VERSION = sdk;
    }

    public static boolean PRP_ENABLED = true;
    public static float PRP_DEFAULT_LIGHTNESS = 1.1f;
    public static int PRP_DEFAULT_SMOOTHNESS = 12;
    public static final float PRP_MAX_LIGHTNESS = 1.5f;
    public static final int PRP_MAX_SMOOTHNESS = 15;

    public static boolean SHOW_VIDEO_INFO = true;

    public static String SHOW_MESSAGE = "showmessage";
    public static String KEY_REVIEW = "keyreview";
    public static boolean IS_STEMO_CONNECTED = false;
    public static String ishomesnot = "";
    public static String isvitalnot = "";
    public static String ishomefragment = "";
    public static String isvitalrecord = "";
    public static String istabselected = "";
    public static String isSelected = "0";
    public static boolean isSchedule = false;
    public static String istype = "";
    public static String ORG_CODE = "org_code";
    public static String PROVIDER_CODE = "provider_code";
}
