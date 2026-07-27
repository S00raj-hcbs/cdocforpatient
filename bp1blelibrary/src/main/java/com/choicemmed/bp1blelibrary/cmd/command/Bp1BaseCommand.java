/*    */ package com.choicemmed.bp1blelibrary.cmd.command;
/*    */ 
/*    */ import com.choicemmed.bp1blelibrary.ble.Bp1Ble;
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Bp1BaseCommand
/*    */ {
/*    */   private static final String TAG = "Bp1BaseCommand";
/*    */   protected Bp1Ble bp1Ble;
/*    */   
/*    */   public Bp1BaseCommand(Bp1Ble bp1Ble)
/*    */   {
/* 14 */     this.bp1Ble = bp1Ble;
/*    */   }
/*    */   
/*    */   public abstract void execute();
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\command\Bp1BaseCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */