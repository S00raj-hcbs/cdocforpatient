/*    */ package com.choicemmed.c208blelibrary.cmd.command;
/*    */ 
/*    */ import com.choicemmed.c208blelibrary.ble.C208Ble;
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class C208BaseCommand
/*    */ {
/*    */   private static final String TAG = "C208BaseCommand";
/*    */   protected C208Ble c208Ble;
/*    */   
/*    */   public C208BaseCommand(C208Ble c208Ble)
/*    */   {
/* 14 */     this.c208Ble = c208Ble;
/*    */   }
/*    */   
/*    */   public abstract void execute();
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\command\C208BaseCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */