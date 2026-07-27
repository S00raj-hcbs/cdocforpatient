/*    */ package com.choicemmed.bp1blelibrary.cmd.command;
/*    */ 
/*    */ import android.text.TextUtils;
/*    */ import com.choicemmed.bp1blelibrary.ble.Bp1Ble;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Bp1ConnectDeviceCommand
/*    */   extends Bp1BaseCommand
/*    */ {
/*    */   private static final String TAG = "Bp1ConnectDeviceCommand";
/*    */   private String address;
/*    */   
/*    */   public Bp1ConnectDeviceCommand(Bp1Ble bp1Ble)
/*    */   {
/* 16 */     super(bp1Ble);
/*    */   }
/*    */   
/*    */   public void setAddress(String address)
/*    */   {
/* 21 */     this.address = address;
/*    */   }
/*    */   
/*    */   public void execute()
/*    */   {
/* 26 */     if (!TextUtils.isEmpty(this.address)) {
/* 27 */       this.bp1Ble.connectDevice(this.address);
/*    */     } else {
/* 29 */       throw new RuntimeException("macAddress is empty or null");
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\command\Bp1ConnectDeviceCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */