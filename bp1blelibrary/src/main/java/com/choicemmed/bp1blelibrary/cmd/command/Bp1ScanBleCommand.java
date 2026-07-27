/*    */ package com.choicemmed.bp1blelibrary.cmd.command;
/*    */ 
/*    */ import com.choicemmed.bp1blelibrary.ble.Bp1Ble;
/*    */ 
/*    */ 
/*    */ public class Bp1ScanBleCommand
/*    */   extends Bp1BaseCommand
/*    */ {
/*    */   private static final String TAG = "Bp1ScanBleCommand";
/*    */   
/*    */   public Bp1ScanBleCommand(Bp1Ble bp1Ble)
/*    */   {
/* 13 */     super(bp1Ble);
/*    */   }
/*    */   
/*    */   public void execute()
/*    */   {
/* 18 */     this.bp1Ble.startLeScan();
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\command\Bp1ScanBleCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */