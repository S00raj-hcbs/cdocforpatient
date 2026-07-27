/*    */ package com.choicemmed.c208blelibrary.cmd.parse;
/*    */ 
/*    */ import com.choicemmed.c208blelibrary.utils.ByteUtils;
/*    */ import com.choicemmed.c208blelibrary.utils.LogUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class C208ParseData
/*    */ {
/*    */   private static final String TAG = "C208ParseData";
/*    */   
/*    */   public static int parseSpo(String data)
/*    */   {
/* 14 */     LogUtils.d("C208ParseData", "spo-->" + data);
/* 15 */     LogUtils.d("C208ParseData", "spo1-->" + Integer.parseInt(data.substring(6, 8), 16));
/* 16 */     return Integer.parseInt(data.substring(6, 8), 16);
/*    */   }
/*    */   
/*    */   public static int parsePR(String data)
/*    */   {
/* 21 */     LogUtils.d("C208ParseData", "pr-->" + data);
/* 22 */     LogUtils.d("C208ParseData", "pr1-->" + Integer.parseInt(data.substring(8, 10), 16));
/* 23 */     return Integer.parseInt(data.substring(8, 10), 16);
/*    */   }
/*    */   
/*    */   public static String parseDeviceSN(String data) {
/* 27 */     LogUtils.d("C208ParseData", "parseDeviceSN-->" + data);
/* 28 */     int cmdLen = Integer.parseInt(data.substring(4, 6), 16);
///* 29 */     LogUtils.d("C208ParseData", "substring-->" + data.substring(8, 8 + (cmdLen - 2) * 2));
    /* 29 */     LogUtils.d("C208ParseData", "substring-->" + data.substring(8, (cmdLen - 2) * 2));

///* 30 */     LogUtils.d("C208ParseData", "substring1-->" + ByteUtils.hexStringReverse(data.substring(8, 8 + (cmdLen - 2) * 2)));
    /* 30 */     LogUtils.d("C208ParseData", "substring1-->" + ByteUtils.hexStringReverse(data.substring(8, (cmdLen - 2) * 2)));
///* 31 */     String deviceSN = data.substring(8, 8 + (cmdLen - 2) * 2);
    /* 31 */     String deviceSN = data.substring(8, (cmdLen - 2) * 2);

/* 32 */     LogUtils.d("C208ParseData", "deviceSN-->" + deviceSN);
/* 33 */     return deviceSN;
/*    */   }
/*    */   
/*    */   public static String parseDeviceID(String data) {
/* 37 */     LogUtils.d("C208ParseData", "parseDeviceID-->" + data);
/* 38 */     String partID = ByteUtils.hexStringReverse(data.substring(12, 20));
/* 39 */     LogUtils.d("C208ParseData", "partID-->" + partID);
/* 40 */     String deviceID = data.substring(8, 12) + partID;
/* 41 */     LogUtils.d("C208ParseData", "deviceID--->" + deviceID);
/* 42 */     return deviceID;
/*    */   }
/*    */   
/*    */   public static boolean parseMatchResult(String data) {
/* 46 */     LogUtils.d("C208ParseData", "parseMatchResult-->" + data);
/* 47 */     int code = Integer.parseInt(data.substring(8, 10), 16);
/* 48 */     LogUtils.d("C208ParseData", "code-->" + code);
/* 49 */     if (code == 0)
/* 50 */       return true;
/* 51 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\parse\C208ParseData.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */