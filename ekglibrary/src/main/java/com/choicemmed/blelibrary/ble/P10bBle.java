/*    */ package com.choicemmed.blelibrary.ble;
/*    */ 
/*    */ import android.bluetooth.BluetoothDevice;
/*    */ import android.bluetooth.BluetoothGattCallback;
/*    */ import android.content.Context;
/*    */ import android.os.Handler;
/*    */ import android.os.Message;
/*    */ import android.util.Log;
/*    */ import com.choicemmed.blelibrary.base.BaseBle;
/*    */ import com.choicemmed.blelibrary.base.BleListener;
/*    */ import com.choicemmed.blelibrary.base.DeviceType;
/*    */ import com.choicemmed.blelibrary.gatt.P10bGattCallback;
/*    */ import java.util.Locale;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class P10bBle
/*    */   extends BaseBle
/*    */ {
/*    */   public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
/*    */   {
/* 38 */     new Thread()
/*    */     {
/*    */       public void run()
/*    */       {
/*    */         try
/*    */         {
/* 26 */           if ((!P10bBle.this.foundDevice) && (device != null) && (device.getName() != null) && (device.getName().toUpperCase(Locale.getDefault()).contains("P10-B"))) {
/* 27 */             Log.d("BLELog", "搜索到P10-B设备，address：" + device.getAddress());
/* 28 */             P10bBle.this.mBleListener.onFoundDevice(P10bBle.this.getDeviceType(), device.getAddress(), device.getName());
/* 29 */             P10bBle.this.foundDevice = true;
/*    */             
/* 31 */             P10bBle.this.bleHandler.removeMessages(0);
/* 32 */             P10bBle.this.bleHandler.obtainMessage(0).sendToTarget();
/*    */           }
/*    */         } catch (Exception e) {
/* 35 */           e.printStackTrace();
/*    */         }
/*    */       }
/*    */     }.start();
/*    */   }
/*    */   
/*    */   public P10bBle(Context context, BleListener bleListener) {
/* 42 */     super(context, bleListener);
/*    */   }
/*    */   
/*    */   protected DeviceType getDeviceType() {
/* 46 */     return DeviceType.P10b;
/*    */   }
/*    */   
/*    */   protected BluetoothGattCallback GetGattCallback()
/*    */   {
/* 51 */     return new P10bGattCallback(this);
/*    */   }
/*    */   
/*    */   public void sendCmd(String cmd)
/*    */   {
/* 56 */     P10bGattCallback.sendCmd(this.mBluetoothGatt, cmd);
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\blelibrary\ble\P10bBle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */