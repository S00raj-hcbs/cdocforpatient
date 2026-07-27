/*    */ package com.choicemmed.c208blelibrary.ble;
/*    */ 
/*    */ import android.bluetooth.BluetoothDevice;
/*    */ import android.bluetooth.BluetoothGattCallback;
/*    */ import android.content.Context;
import android.util.Log;
/*    */ import com.choicemmed.c208blelibrary.base.BaseBle;
/*    */ import com.choicemmed.c208blelibrary.base.BleListener;
/*    */ import com.choicemmed.c208blelibrary.base.DeviceType;
/*    */ import com.choicemmed.c208blelibrary.gatt.C208GattCallback;
/*    */ import com.choicemmed.c208blelibrary.utils.LogUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class C208Ble
/*    */   extends BaseBle
/*    */ {
/*    */   private static final String DEVICE_UUID_PREFIX = "ba11f08c5f140b0d1080";
/*    */   
/*    */   public C208Ble(Context context, BleListener bleListener)
/*    */   {
/* 21 */     super(context, bleListener);
/*    */   }
/*    */   
/*    */ 
/*    */   protected DeviceType getDeviceType()
/*    */   {
/* 27 */     return DeviceType.C208;
/*    */   }
/*    */   
/*    */   protected BluetoothGattCallback GetGattCallback()
/*    */   {
/* 32 */     return new C208GattCallback(this);
/*    */   }
/*    */   
/*    */   public void sendCmd(String cmd)
/*    */   {
/* 37 */     C208GattCallback.sendCmd(this.mBluetoothGatt, cmd);
/*    */   }
/*    */   
/*    */   public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
/*    */   {
    /* 42 */     final String str = bytes2HexString(scanRecord).replace("-", "").toLowerCase();
/* 43 */     new Thread()
/*    */     {
/*    */       public void run() {
/*    */         try {
/* 47 */           if ((!C208Ble.this.foundDevice) && (device != null) && (str.contains("ba11f08c5f140b0d1080"))) {
/* 48 */             LogUtils.d("onLeScan", "已扫描到蓝牙设备" + device.getAddress());
/* 49 */             C208Ble.this.foundDevice = true;
/* 50 */             C208Ble.this.stopLeScan();
/* 51 */             C208Ble.this.mBleListener.onFoundDevice(C208Ble.this.getDeviceType(), device.getAddress(), device.getName());
/*    */           }
/*    */         } catch (Exception e) {
/* 54 */           e.printStackTrace();
/*    */         }
/*    */       }
/*    */     }.start();
/*    */   }
/*    */   
/*    */   public static String bytes2HexString(byte[] a)
/*    */   {
/* 62 */     int len = a.length;
/* 63 */     byte[] b = new byte[len];
/* 64 */     for (int k = 0; k < len; k++) {
/* 65 */       b[k] = a[(a.length - 1 - k)];
/*    */     }
/*    */     
/* 68 */     String ret = "";
/* 69 */     for (int i = 0; i < len; i++) {
/* 70 */       String hex = Integer.toHexString(b[i] & 0xFF);
/* 71 */       if (hex.length() == 1) {
/* 72 */         hex = '0' + hex;
/*    */       }
/* 74 */       ret = ret + hex.toUpperCase();
/*    */     }
/*    */     
/* 77 */     return ret;
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\ble\C208Ble.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */