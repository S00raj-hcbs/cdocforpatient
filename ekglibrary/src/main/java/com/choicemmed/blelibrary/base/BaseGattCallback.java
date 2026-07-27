/*    */ package com.choicemmed.blelibrary.base;
/*    */ 
/*    */ import android.bluetooth.BluetoothGatt;
/*    */ import android.bluetooth.BluetoothGattCallback;
/*    */ import android.bluetooth.BluetoothGattCharacteristic;
/*    */ import android.bluetooth.BluetoothGattDescriptor;
/*    */ import java.util.UUID;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class BaseGattCallback
/*    */   extends BluetoothGattCallback
/*    */ {
/*    */   protected static final String LogTag_BLE = "BLELog";
/* 16 */   private static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
/*    */   private GattListener mGattListener;
/*    */   
/*    */   protected BaseGattCallback(GattListener gattListener)
/*    */   {
/* 21 */     this.mGattListener = gattListener;
/*    */   }
/*    */   
/*    */   protected abstract DeviceType getDeviceType();
/*    */   
/*    */   protected void onError(String errorMsg) {
/* 27 */     if (this.mGattListener != null) {
/* 28 */       this.mGattListener.onError(getDeviceType(), errorMsg);
/*    */     }
/*    */   }
/*    */   
/*    */   protected void onDisconnected() {
/* 33 */     if (this.mGattListener != null) {
/* 34 */       this.mGattListener.onDisconnected(getDeviceType());
/*    */     }
/*    */   }
/*    */   
/*    */   protected void onInitialized() {
/* 39 */     if (this.mGattListener != null) {
/* 40 */       this.mGattListener.onInitialized(getDeviceType());
/*    */     }
/*    */   }
/*    */   
/*    */   protected void onDataRecived(byte[] data) {
/* 45 */     if (this.mGattListener != null) {
/* 46 */       this.mGattListener.onCmdResponse(getDeviceType(), data);
/*    */     }
/*    */   }
/*    */   
/*    */   protected static boolean setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enabled) {
/* 51 */     if ((gatt == null) || (characteristic == null)) {
/* 52 */       return false;
/*    */     }
/* 54 */     if (!gatt.setCharacteristicNotification(characteristic, enabled)) {
/* 55 */       return false;
/*    */     }
/*    */     
/* 58 */     BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
/* 59 */     if (descriptor != null) {
/* 60 */       descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
/* 61 */       gatt.writeDescriptor(descriptor);
/*    */     }
/* 63 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\blelibrary\base\BaseGattCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */