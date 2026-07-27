/*    */ package com.choicemmed.c208blelibrary.cmd.command;
/*    */ 
/*    */ import com.choicemmed.c208blelibrary.ble.C208Ble;
/*    */ 
/*    */ 
/*    */ public class C208DisconnectDeviceCommand
/*    */   extends C208BaseCommand
/*    */ {
/*    */   private static final String TAG = "C208DisconnectDeviceCommand";
/*    */   
/*    */   public C208DisconnectDeviceCommand(C208Ble c208Ble)
/*    */   {
/* 13 */     super(c208Ble);
/*    */   }
/*    */   
/*    */   public void execute()
/*    */   {
/* 18 */     this.c208Ble.resetGatt();
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\command\C208DisconnectDeviceCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */