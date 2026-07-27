/*    */ package com.choicemmed.c208blelibrary.cmd.command;
/*    */ 
/*    */ import com.choicemmed.c208blelibrary.ble.C208Ble;
/*    */ 
/*    */ 
/*    */ public class C208MatchPasswordCommand
/*    */   extends C208BaseCommand
/*    */ {
/*    */   private static final String TAG = "C208MatchPasswordCommand";
/*    */   private static final String cmd = "aa5504b10000b5";
/*    */   
/*    */   public C208MatchPasswordCommand(C208Ble c208Ble)
/*    */   {
/* 14 */     super(c208Ble);
/*    */   }
/*    */   
/*    */   public void execute()
/*    */   {
/* 19 */     this.c208Ble.sendCmd("aa5504b10000b5");
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\command\C208MatchPasswordCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */