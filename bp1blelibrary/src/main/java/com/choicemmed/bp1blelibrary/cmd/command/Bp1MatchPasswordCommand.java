/*    */ package com.choicemmed.bp1blelibrary.cmd.command;
/*    */ 
/*    */ import com.choicemmed.bp1blelibrary.ble.Bp1Ble;
/*    */ 
/*    */ 
/*    */ public class Bp1MatchPasswordCommand
/*    */   extends Bp1BaseCommand
/*    */ {
/*    */   private static final String TAG = "Bp1MatchPasswordCommand";
/*    */   private static final String cmd = "aa5504b10000";
/*    */   
/*    */   public Bp1MatchPasswordCommand(Bp1Ble bp1Ble)
/*    */   {
/* 14 */     super(bp1Ble);
/*    */   }
/*    */   
/*    */   public void execute()
/*    */   {
/* 19 */     this.bp1Ble.sendCmd("aa5504b10000");
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\command\Bp1MatchPasswordCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */