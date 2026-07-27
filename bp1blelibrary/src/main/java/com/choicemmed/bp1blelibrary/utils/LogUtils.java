/*    */ package com.choicemmed.bp1blelibrary.utils;
/*    */ 
/*    */ import android.util.Log;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LogUtils
/*    */ {
/*    */   private LogUtils()
/*    */   {
/* 17 */     throw new UnsupportedOperationException("cannot be instantiated");
/*    */   }
/*    */   
/* 20 */   public static boolean isDebug = true;
/*    */   
/*    */   private static final String TAG = "way";
/*    */   
/*    */   public static void i(String msg)
/*    */   {
/* 26 */     if (isDebug) {
/* 27 */       Log.i("way", msg);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void d(String msg) {
/* 32 */     if (isDebug) {
/* 33 */       Log.d("way", msg);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void e(String msg) {
/* 38 */     if (isDebug) {
/* 39 */       Log.e("way", msg);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void v(String msg) {
/* 44 */     if (isDebug) {
/* 45 */       Log.v("way", msg);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void w(String msg) {
/* 50 */     if (isDebug) {
/* 51 */       Log.w("way", msg);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void i(String tag, String msg)
/*    */   {
/* 57 */     if (isDebug) {
/* 58 */       Log.i(tag, msg);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void d(String tag, String msg) {
/* 63 */     if (isDebug) {
/* 64 */       Log.d(tag, msg);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void e(String tag, String msg) {
/* 69 */     if (isDebug) {
/* 70 */       Log.e(tag, msg);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void v(String tag, String msg) {
/* 75 */     if (isDebug) {
/* 76 */       Log.v(tag, msg);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void w(String tag, String msg) {
/* 81 */     if (isDebug) {
/* 82 */       Log.w(tag, msg);
/*    */     }
/*    */   }
/*    */ }

