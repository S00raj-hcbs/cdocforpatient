/*    */ package com.choicemmed.bp1blelibrary.cmd.factory;
/*    */ 
/*    */ import com.choicemmed.bp1blelibrary.ble.Bp1Ble;
/*    */ import com.choicemmed.bp1blelibrary.cmd.command.Bp1BaseCommand;
/*    */ import com.choicemmed.bp1blelibrary.cmd.command.Bp1ConnectDeviceCommand;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Bp1ConnectDeviceCommandFactory
/*    */   implements Bp1CommandCreator
/*    */ {
/*    */   private static final String TAG = "Bp1ConnectDeviceCommandFactory";
/*    */   
/*    */   public Bp1BaseCommand createCommand(Bp1Ble bp1Ble)
/*    */   {
/* 16 */     return new Bp1ConnectDeviceCommand(bp1Ble);
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\factory\Bp1ConnectDeviceCommandFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */