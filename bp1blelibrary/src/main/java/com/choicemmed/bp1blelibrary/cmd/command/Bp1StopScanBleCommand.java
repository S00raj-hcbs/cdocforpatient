/*    */ package com.choicemmed.bp1blelibrary.cmd.command;
/*    */ 
/*    */ import com.choicemmed.bp1blelibrary.ble.Bp1Ble;

/*    */
/*    */ 
/*    */ public class Bp1StopScanBleCommand
/*    */   extends Bp1BaseCommand
/*    */ {
/*    */   private static final String TAG = "Bp1StopScanBleCommand";
/*    */
/*    */   public Bp1StopScanBleCommand(Bp1Ble bp1Ble)
/*    */   {
/* 13 */     super(bp1Ble);
/*    */   }
/*    */   
/*    */   public void execute()
/*    */   {
/* 18 */     this.bp1Ble.stopLeScan();
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\command\Bp1ScanBleCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */