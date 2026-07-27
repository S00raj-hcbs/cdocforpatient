/*    */ package com.choicemmed.bp1blelibrary.ble;
/*    */ 
/*    */ import android.bluetooth.BluetoothDevice;
/*    */ import android.bluetooth.BluetoothGattCallback;
/*    */ import android.content.Context;
/*    */ import com.choicemmed.bp1blelibrary.base.BaseBle;
/*    */ import com.choicemmed.bp1blelibrary.base.BleListener;
/*    */ import com.choicemmed.bp1blelibrary.base.DeviceType;
/*    */ import com.choicemmed.bp1blelibrary.gatt.Bp1GattCallback;
/*    */ import com.choicemmed.bp1blelibrary.utils.LogUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Bp1Ble
/*    */   extends BaseBle
/*    */ {
/*    */   private static final String DEVICE_UUID_PREFIX = "ba11f08c5f140b0d10a0";
/* 18 */   private static final String TAG = Bp1Ble.class.getSimpleName();
/*    */   private Bp1GattCallback bp1GattCallback;
/*    */   
/*    */   public Bp1Ble(Context context, BleListener bleListener) {
/* 22 */     super(context, bleListener);
/*    */   }
/*    */   
/*    */ 
/*    */   protected DeviceType getDeviceType()
/*    */   {
/* 28 */     return DeviceType.Bp1;
/*    */   }
/*    */   
/*    */   protected BluetoothGattCallback GetGattCallback()
/*    */   {
/* 33 */     this.bp1GattCallback = new Bp1GattCallback(this);
/* 34 */     return this.bp1GattCallback;
/*    */   }
/*    */   
/*    */   public void sendCmd(String cmd)
/*    */   {
/*    */     try {
/* 40 */       this.bp1GattCallback.sendCmd(this.mBluetoothGatt, cmd);
/*    */     } catch (Exception e) {
/* 42 */       onError(DeviceType.Bp1, "发送命令失败");
/* 43 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */   
/*    */   public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
/*    */   {
/* 49 */     final String str = bytes2HexString(scanRecord).replace("-", "").toLowerCase();
/* 50 */     new Thread()
/*    */     {
/*    */       public void run() {
/*    */         try {
/* 54 */           if ((!Bp1Ble.this.foundDevice) && (device != null) && (str.contains("ba11f08c5f140b0d10a0"))) {
//            if ((!Bp1Ble.this.foundDevice) && (device != null) && (str.contains("000015231212efde1523"))) {
/* 55 */             LogUtils.d("onLeScan", "已扫描到蓝牙设备" + device.getAddress());
/* 56 */             Bp1Ble.this.foundDevice = true;
/* 57 */             Bp1Ble.this.stopLeScan();
/* 58 */             Bp1Ble.this.mBleListener.onFoundDevice(Bp1Ble.this.getDeviceType(), device.getAddress(), device.getName());
/*    */           }
/*    */         } catch (Exception e) {
/* 61 */           e.printStackTrace();
/*    */         }
/*    */       }
/*    */     }.start();
/*    */   }
/*    */   
/*    */   public static String bytes2HexString(byte[] a)
/*    */   {
/* 69 */     int len = a.length;
/* 70 */     byte[] b = new byte[len];
/* 71 */     for (int k = 0; k < len; k++) {
/* 72 */       b[k] = a[(a.length - 1 - k)];
/*    */     }
/*    */     
/* 75 */     String ret = "";
/* 76 */     for (int i = 0; i < len; i++) {
/* 77 */       String hex = Integer.toHexString(b[i] & 0xFF);
/* 78 */       if (hex.length() == 1) {
/* 79 */         hex = '0' + hex;
/*    */       }
/* 81 */       ret = ret + hex.toUpperCase();
/*    */     }
/*    */     
/* 84 */     return ret;
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\ble\Bp1Ble.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */