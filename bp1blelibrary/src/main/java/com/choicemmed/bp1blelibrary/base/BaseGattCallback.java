/*    */ package com.choicemmed.bp1blelibrary.base;
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
/*    */   protected void onDataReceived(byte[] data) {
/* 45 */     if (this.mGattListener != null) {
/* 46 */       this.mGattListener.onDataResponse(getDeviceType(), data);
/*    */     }
/*    */   }
/*    */   
/*    */   protected void onCommandReceived(byte[] data) {
/* 51 */     if (this.mGattListener != null) {
/* 52 */       this.mGattListener.onCmdResponse(getDeviceType(), data);
/*    */     }
/*    */   }
/*    */   
/*    */   protected static boolean setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enabled) {
/* 57 */     if ((gatt == null) || (characteristic == null)) {
/* 58 */       return false;
/*    */     }
/* 60 */     if (!gatt.setCharacteristicNotification(characteristic, enabled)) {
/* 61 */       return false;
/*    */     }
/*    */     
/* 64 */     BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
/* 65 */     if (descriptor != null) {
/* 66 */       descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
/* 67 */       gatt.writeDescriptor(descriptor);
/*    */     }
/* 69 */     return true;
/*    */   }
/*    */   
/*    */   protected static boolean setCharacteristicIndication(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enabled) {
/* 73 */     if ((gatt == null) || (characteristic == null)) {
/* 74 */       return false;
/*    */     }
/* 76 */     if (!gatt.setCharacteristicNotification(characteristic, enabled)) {
/* 77 */       return false;
/*    */     }
/*    */     
/* 80 */     BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
/* 81 */     if (descriptor != null) {
/* 82 */       descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
/* 83 */       gatt.writeDescriptor(descriptor);
/*    */     }
/* 85 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\base\BaseGattCallback.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */