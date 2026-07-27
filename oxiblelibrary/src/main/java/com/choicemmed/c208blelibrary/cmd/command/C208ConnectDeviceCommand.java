/*    */ package com.choicemmed.c208blelibrary.cmd.command;
/*    */ 
/*    */ import com.choicemmed.c208blelibrary.ble.C208Ble;
/*    */ 
/*    */ 
/*    */ public class C208ConnectDeviceCommand
/*    */   extends C208BaseCommand
/*    */ {
/*    */   private static final String TAG = "C208ConnectDeviceCommand";
/*    */   private String address;
/*    */   
/*    */   public C208ConnectDeviceCommand(C208Ble c208Ble)
/*    */   {
/* 14 */     super(c208Ble);
/*    */   }
/*    */   
/*    */   public void setAddress(String address)
/*    */   {
/* 19 */     this.address = address;
/*    */   }
/*    */   
/*    */   public void execute()
/*    */   {
/* 24 */     if (this.address != null) {
/* 25 */       this.c208Ble.connectDevice(this.address);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\command\C208ConnectDeviceCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */