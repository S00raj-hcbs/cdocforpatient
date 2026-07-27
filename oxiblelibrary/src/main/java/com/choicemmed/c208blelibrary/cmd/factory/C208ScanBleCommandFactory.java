/*    */ package com.choicemmed.c208blelibrary.cmd.factory;
/*    */ 
/*    */ import com.choicemmed.c208blelibrary.ble.C208Ble;
/*    */ import com.choicemmed.c208blelibrary.cmd.command.C208BaseCommand;
/*    */ import com.choicemmed.c208blelibrary.cmd.command.C208ScanBleCommand;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class C208ScanBleCommandFactory
/*    */   implements C208CreateCommandListener
/*    */ {
/*    */   private static final String TAG = "C208ScanBleCommandFactory";
/*    */   
/*    */   public C208BaseCommand createCommand(C208Ble c208Ble)
/*    */   {
/* 16 */     return new C208ScanBleCommand(c208Ble);
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\factory\C208ScanBleCommandFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */