/*    */ package com.choicemmed.bp1blelibrary.cmd.parse;
/*    */ 
/*    */ import com.choicemmed.bp1blelibrary.utils.LogUtils;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Bp1ParseData
/*    */ {
/*    */   private static final String TAG = "Bp1ParseData";
/*    */   public static final int BP_SYSTOLIC = 1;
/*    */   public static final int BP_DIASTOLIC = 2;
/*    */   public static final int BP_HEARTRATE = 3;
/*    */   
/*    */   public static boolean parseMatchResult(String data)
/*    */   {
/* 20 */     int code = Integer.parseInt(data.substring(8, 10), 16);
/* 21 */     if (code == 0)
/* 22 */       return true;
/* 23 */     return false;
/*    */   }
/*    */   
/*    */   public static HashMap<Integer, Integer> parseBp(byte[] data) {
/* 27 */     if ((data != null) && (data.length > 0) && 
/* 28 */       (data.length > 9)) {
/* 29 */       HashMap<Integer, Integer> map = new HashMap();
/* 30 */       int bpSystolic = ((data[4] & 0xFF) << 8) + (data[5] & 0xFF);
/* 31 */       int bpDiastolic = ((data[6] & 0xFF) << 8) + (data[7] & 0xFF);
/* 32 */       int bpHeartRate = ((data[8] & 0xFF) << 8) + (data[9] & 0xFF);
/* 33 */       LogUtils.d("Bp1ParseData", "高压：" + bpSystolic + "；低压：" + bpDiastolic + "；脉率：" + bpHeartRate);
/*    */       
/*    */ 
/* 36 */       map.put(Integer.valueOf(1), Integer.valueOf(bpSystolic));
/* 37 */       map.put(Integer.valueOf(2), Integer.valueOf(bpDiastolic));
/* 38 */       map.put(Integer.valueOf(3), Integer.valueOf(bpHeartRate));
/* 39 */       return map;
/*    */     }
/*    */     
/* 42 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\parse\Bp1ParseData.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */