/*    */ package com.choicemmed.bp1blelibrary.cmd.factory;
/*    */ 
/*    */ import com.choicemmed.bp1blelibrary.ble.Bp1Ble;
/*    */ import com.choicemmed.bp1blelibrary.cmd.command.Bp1BaseCommand;
/*    */ import com.choicemmed.bp1blelibrary.cmd.command.Bp1ScanBleCommand;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Bp1ScanBleCommandFactory
/*    */   implements Bp1CommandCreator
/*    */ {
/*    */   private static final String TAG = "Bp1ScanBleCommandFactory";
/*    */   
/*    */   public Bp1BaseCommand createCommand(Bp1Ble bp1Ble)
/*    */   {
/* 16 */     return new Bp1ScanBleCommand(bp1Ble);
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\factory\Bp1ScanBleCommandFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */