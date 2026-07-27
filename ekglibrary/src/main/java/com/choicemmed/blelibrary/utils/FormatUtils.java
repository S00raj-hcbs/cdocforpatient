/*    */ package com.choicemmed.blelibrary.utils;
/*    */ 
/*    */ import java.text.DecimalFormat;
/*    */ import java.text.ParseException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class FormatUtils
/*    */ {
/*    */   public static final String template_DbDateTime = "yyyy-MM-dd HH:mm:ss";
/*    */   public static final String template_DispalyDateTime = "yyyy/MM/dd HH:mm";
/*    */   public static final String template_DownloadDateTime = "yyyy-MM-ddTHH:mm:ss";
/*    */   public static final String template_Date = "yyyy-MM-dd";
/*    */   public static final String template_Month = "yyyy-MM";
/*    */   
/*    */   public static Date parseDate(String dateString, String template)
/*    */   {
/* 19 */     SimpleDateFormat format = new SimpleDateFormat(template, java.util.Locale.getDefault());
/*    */     try {
/* 21 */       return format.parse(dateString);
/*    */     } catch (ParseException e) {}
/* 23 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   public static String getDateTimeString(Date date, String template)
/*    */   {
/* 29 */     SimpleDateFormat format = new SimpleDateFormat(template, java.util.Locale.getDefault());
/* 30 */     return format.format(date);
/*    */   }
/*    */   
/*    */   public static String getDoubleString(double value, int decimalDigits)
/*    */   {
/* 35 */     if (decimalDigits < 1)
/*    */     {
/* 37 */       decimalDigits = 1;
/*    */     }
/* 39 */     String pattern = "0.";
/* 40 */     for (int i = 0; i < decimalDigits; i++) {
/* 41 */       pattern = pattern + "0";
/*    */     }
/* 43 */     DecimalFormat df = new DecimalFormat(pattern);
/* 44 */     return df.format(value);
/*    */   }
/*    */   
/*    */   public static String getDateTimeString(Long timeInMillis, String template) {
/* 48 */     Calendar c = Calendar.getInstance();
/* 49 */     c.setTimeInMillis(timeInMillis.longValue());
/* 50 */     return getDateTimeString(c.getTime(), "yyyy-MM-dd HH:mm:ss");
/*    */   }
/*    */ }

