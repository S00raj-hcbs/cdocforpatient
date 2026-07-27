/*     */ package com.choicemmed.c208blelibrary.utils;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ByteUtils
/*     */ {
/*     */   public static byte[] cmdString2Bytes(String cmd, boolean withSumCode)
/*     */   {
/*  12 */     byte[] value = hexString2Bytes(cmd);
/*  13 */     if (withSumCode) {
/*  14 */       byte verifySum = 0;
/*  15 */       for (int i = 2; i < value.length; i++) {
/*  16 */         verifySum = (byte)(verifySum + value[i]);
/*     */       }
/*  18 */       byte[] values = new byte[value.length + 1];
/*  19 */       for (int i = 0; i < value.length; i++) {
/*  20 */         values[i] = value[i];
/*     */       }
/*  22 */       values[value.length] = verifySum;
/*  23 */       return values;
/*     */     }
/*  25 */     return value;
/*     */   }
/*     */   
/*     */   public static byte getSum(String cmd)
/*     */   {
/*  30 */     int sum = 0;
/*  31 */     byte[] value = hexString2Bytes(cmd);
/*  32 */     for (int i = 2; i < value.length; i++) {
/*  33 */       sum += value[i];
/*     */     }
/*  35 */     return (byte)(sum & 0xFF);
/*     */   }
/*     */   
/*     */   public static byte[] reverseBytes(byte[] a)
/*     */   {
/*  40 */     int len = a.length;
/*  41 */     byte[] b = new byte[len];
/*  42 */     for (int k = 0; k < len; k++) {
/*  43 */       b[k] = a[(a.length - 1 - k)];
/*     */     }
/*  45 */     return b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String bytes2HexString(byte[] bytes)
/*     */   {
/*  56 */     String result = "";
/*  57 */     for (int i = 0; i < bytes.length; i++) {
/*  58 */       String hex = Integer.toHexString(bytes[i] & 0xFF);
/*  59 */       if (hex.length() == 1) {
/*  60 */         hex = '0' + hex;
/*     */       }
/*  62 */       result = result + hex.toLowerCase(Locale.getDefault());
/*     */     }
/*  64 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] hexString2Bytes(String hexString)
/*     */   {
/*  74 */     int len = hexString.length() / 2;
/*  75 */     char[] chars = hexString.toCharArray();
/*  76 */     String[] hexStr = new String[len];
/*  77 */     byte[] bytes = new byte[len];
/*  78 */     int i = 0; for (int j = 0; j < len; j++) {
/*  79 */       hexStr[j] = ("" + chars[i] + chars[(i + 1)]);
/*  80 */       bytes[j] = ((byte)Integer.parseInt(hexStr[j], 16));i += 2;
/*     */     }
/*     */     
/*  82 */     return bytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String hexStringReverse(String hexString)
/*     */   {
/*  91 */     int len = hexString.length() / 2;
/*  92 */     char[] chars = hexString.toCharArray();
/*  93 */     String[] hexStr = new String[len];
/*  94 */     String result = "";
/*  95 */     int i = 0; for (int j = len - 1; j >= 0; j--) {
/*  96 */       hexStr[j] = ("" + chars[i] + chars[(i + 1)]);
/*  97 */       result = hexStr[j] + result;i += 2;
/*     */     }
/*     */     
/*     */ 
/* 100 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ArrayList<Byte> hexString2List(String hexString)
/*     */   {
/* 110 */     int len = hexString.length() / 2;
/* 111 */     char[] chars = hexString.toCharArray();
/* 112 */     String[] hexStr = new String[len];
/* 113 */     ArrayList<Byte> list = new ArrayList();
/* 114 */     int i = 0; for (int j = 0; j < len; j++) {
/* 115 */       hexStr[j] = ("" + chars[i] + chars[(i + 1)]);
/* 116 */       list.add(Byte.valueOf((byte)Integer.parseInt(hexStr[j], 16)));i += 2;
/*     */     }
/*     */     
/* 118 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] intToBytes(int value)
/*     */   {
/* 129 */     byte[] src = new byte[4];
/* 130 */     src[3] = ((byte)(value >> 24 & 0xFF));
/* 131 */     src[2] = ((byte)(value >> 16 & 0xFF));
/* 132 */     src[1] = ((byte)(value >> 8 & 0xFF));
/* 133 */     src[0] = ((byte)(value & 0xFF));
/* 134 */     return src;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] intToBytes2(int value)
/*     */   {
/* 142 */     byte[] src = new byte[2];
/* 143 */     src[1] = ((byte)(value >> 8 & 0xFF));
/* 144 */     src[0] = ((byte)(value & 0xFF));
/* 145 */     return src;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String makeChecksum(String data)
/*     */   {
/* 156 */     int total = 0;
/* 157 */     int len = data.length();
/* 158 */     int num = 0;
/* 159 */     while (num < len) {
/* 160 */       String s = data.substring(num, num + 2);
/*     */       
/* 162 */       total += Integer.parseInt(s, 16);
/* 163 */       num += 2;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 168 */     int mod = total % 256;
/* 169 */     String hex = Integer.toHexString(mod);
/* 170 */     len = hex.length();
/*     */     
/* 172 */     if (len < 2) {
/* 173 */       hex = "0" + hex;
/*     */     }
/* 175 */     return hex;
/*     */   }
/*     */ }
