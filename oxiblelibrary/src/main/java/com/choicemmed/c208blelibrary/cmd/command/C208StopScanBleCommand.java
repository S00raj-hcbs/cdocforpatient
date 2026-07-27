/*    */ package com.choicemmed.c208blelibrary.cmd.command;
/*    */ 
/*    */ import com.choicemmed.c208blelibrary.ble.C208Ble;

/*    */
/*    */ 
/*    */ public class C208StopScanBleCommand
/*    */   extends C208BaseCommand
/*    */ {
/*    */   private static final String TAG = "C208StopScanBleCommand";
/*    */
/*    */   public C208StopScanBleCommand(C208Ble c208Ble)
/*    */   {
/* 13 */     super(c208Ble);
/*    */   }
/*    */   
/*    */   public void execute()
/*    */   {
/* 18 */     this.c208Ble.stopLeScan();
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\command\C208ScanBleCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */