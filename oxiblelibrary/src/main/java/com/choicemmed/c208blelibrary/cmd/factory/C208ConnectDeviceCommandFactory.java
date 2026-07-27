/*    */ package com.choicemmed.c208blelibrary.cmd.factory;
/*    */ 
/*    */ import com.choicemmed.c208blelibrary.ble.C208Ble;
/*    */ import com.choicemmed.c208blelibrary.cmd.command.C208BaseCommand;
/*    */ import com.choicemmed.c208blelibrary.cmd.command.C208ConnectDeviceCommand;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class C208ConnectDeviceCommandFactory
/*    */   implements C208CreateCommandListener
/*    */ {
/*    */   private static final String TAG = "C208ConnectDeviceCommandFactory";
/*    */   
/*    */   public C208BaseCommand createCommand(C208Ble c208Ble)
/*    */   {
/* 16 */     return new C208ConnectDeviceCommand(c208Ble);
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\factory\C208ConnectDeviceCommandFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */