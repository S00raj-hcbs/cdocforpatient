/*    */ package com.choicemmed.blelibrary.utils;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Locale;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ByteUtils
/*    */ {
/*    */   public static byte[] cmdString2Bytes(String cmd, boolean withSumCode)
/*    */   {
/* 12 */     byte[] value = hexString2Bytes(cmd);
/* 13 */     if (withSumCode)
/*    */     {
/* 15 */       byte verifySum = 0;
/* 16 */       for (int i = 2; i < value.length; i++) {
/* 17 */         verifySum = (byte)(verifySum + value[i]);
/*    */       }
/* 19 */       byte[] values = new byte[value.length + 1];
/* 20 */       for (int i = 0; i < value.length; i++) {
/* 21 */         values[i] = value[i];
/*    */       }
/* 23 */       values[value.length] = verifySum;
/* 24 */       return values;
/*    */     }
/*    */     
/*    */ 
/* 28 */     return value;
/*    */   }
/*    */   
/*    */ 
/*    */   public static byte[] reverseBytes(byte[] a)
/*    */   {
/* 34 */     int len = a.length;
/* 35 */     byte[] b = new byte[len];
/* 36 */     for (int k = 0; k < len; k++) {
/* 37 */       b[k] = a[(a.length - 1 - k)];
/*    */     }
/* 39 */     return b;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static String bytes2HexString(byte[] bytes)
/*    */   {
/* 48 */     String result = "";
/* 49 */     for (int i = 0; i < bytes.length; i++) {
/* 50 */       String hex = Integer.toHexString(bytes[i] & 0xFF);
/* 51 */       if (hex.length() == 1) {
/* 52 */         hex = '0' + hex;
/*    */       }
/* 54 */       result = result + hex.toLowerCase(Locale.getDefault());
/*    */     }
/* 56 */     return result;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static byte[] hexString2Bytes(String hexString)
/*    */   {
/* 65 */     int len = hexString.length() / 2;
/* 66 */     char[] chars = hexString.toCharArray();
/* 67 */     String[] hexStr = new String[len];
/* 68 */     byte[] bytes = new byte[len];
/* 69 */     int i = 0; for (int j = 0; j < len; j++) {
/* 70 */       hexStr[j] = ("" + chars[i] + chars[(i + 1)]);
/* 71 */       bytes[j] = ((byte)Integer.parseInt(hexStr[j], 16));i += 2;
/*    */     }
/*    */     
/* 73 */     return bytes;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static ArrayList<Byte> hexString2List(String hexString)
/*    */   {
/* 82 */     int len = hexString.length() / 2;
/* 83 */     char[] chars = hexString.toCharArray();
/* 84 */     String[] hexStr = new String[len];
/* 85 */     ArrayList<Byte> list = new ArrayList();
/* 86 */     int i = 0; for (int j = 0; j < len; j++) {
/* 87 */       hexStr[j] = ("" + chars[i] + chars[(i + 1)]);
/* 88 */       list.add(Byte.valueOf((byte)Integer.parseInt(hexStr[j], 16)));i += 2;
/*    */     }
/*    */     
/* 90 */     return list;
/*    */   }
/*    */ }
