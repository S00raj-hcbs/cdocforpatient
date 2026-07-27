/*     */ package com.choicemmed.blelibrary.base;
/*     */ 
/*     */ import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
/*     */ import android.bluetooth.BluetoothAdapter.LeScanCallback;
/*     */ import android.bluetooth.BluetoothDevice;
/*     */ import android.bluetooth.BluetoothGatt;
/*     */ import android.bluetooth.BluetoothGattCallback;
/*     */ import android.bluetooth.BluetoothManager;
/*     */ import android.content.Context;
/*     */ import android.content.pm.PackageManager;
/*     */ import android.os.Handler;
/*     */ import android.os.Message;
/*     */ import android.util.Log;
/*     */ import com.choicemmed.blelibrary.R;
import com.choicemmed.blelibrary.R.string;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BaseBle
/*     */   implements GattListener, BluetoothAdapter.LeScanCallback
/*     */ {
/*     */   protected static final String LogTag_BLE = "BLELog";
/*     */   private Context mContext;
/*     */   protected BleListener mBleListener;
/*     */   private BluetoothAdapter mBluetoothAdapter;
/*     */   private BluetoothDevice mBluetoothDevice;
/*     */   protected BluetoothGatt mBluetoothGatt;
/*     */   private static final long BLE_CONNECT_TIMEOUT = 10000L;
/*     */   private static final long SCAN_PERIOD = 20000L;
/*  33 */   protected boolean foundDevice = false;
/*     */   
/*     */   protected static final int MSG_STOPSCAN = 0;
/*     */   
/*     */   protected static final int MSG_CONNECTTIMEOUT = 1;
/*     */   
/*  39 */   protected Handler bleHandler = new Handler()
/*     */   {
/*     */     public void handleMessage(Message msg) {
/*  42 */       switch (msg.what) {
/*     */       case 0: 
/*  44 */         BaseBle.this.stopLeScan();
/*  45 */         break;
/*     */       case 1: 
/*  47 */         BaseBle.this.mBleListener.onError(BaseBle.this.getDeviceType(), BaseBle.this.mContext.getString(R.string.error_connect_timeout));
/*  48 */         BaseBle.this.resetGatt();
/*  49 */         break;
/*     */       }
/*     */       
/*     */     }
/*     */   };
/*     */   
/*     */   public BaseBle(Context context, BleListener bleListener)
/*     */   {
/*  57 */     this.mContext = context;
/*  58 */     this.mBleListener = bleListener;
/*  59 */     initBluetoothAdapter();
/*     */   }
/*     */   
/*     */   private void initBluetoothAdapter() {
/*  63 */     @SuppressLint("WrongConstant") BluetoothManager bluetoothManager = (BluetoothManager)this.mContext.getSystemService("bluetooth");
/*  64 */     this.mBluetoothAdapter = bluetoothManager.getAdapter();
/*     */   }
/*     */   
/*     */   private class TestBleResult {
/*  68 */     public boolean isAvailable = true;
/*     */     public String errorMsg;
/*     */     
/*     */     private TestBleResult() {} }
/*     */   
/*  73 */   private TestBleResult testBle() { TestBleResult result = new TestBleResult();
/*     */     
/*  75 */     if (this.mBluetoothAdapter == null) {
/*  76 */       result.isAvailable = false;
/*  77 */       result.errorMsg = this.mContext.getString(R.string.error_bluetooth_not_supported);
/*  78 */       return result;
/*     */     }
/*     */     
/*  81 */     if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
/*  82 */       result.isAvailable = false;
/*  83 */       result.errorMsg = this.mContext.getString(R.string.error_ble_not_supported);
/*  84 */       return result;
/*     */     }
/*     */     
/*  87 */     if (!this.mBluetoothAdapter.isEnabled()) {
/*  88 */       result.isAvailable = false;
/*  89 */       result.errorMsg = this.mContext.getString(R.string.error_bluetooth_not_open);
/*  90 */       return result;
/*     */     }
/*     */     
/*  93 */     return result;
/*     */   }
/*     */   
/*     */   protected abstract DeviceType getDeviceType();
/*     */   
/*     */   public void startLeScan() {
/*  99 */     TestBleResult result = testBle();
/* 100 */     if (!result.isAvailable) {
/* 101 */       this.mBleListener.onError(getDeviceType(), result.errorMsg);
/* 102 */       return;
/*     */     }
/*     */     
/* 105 */     Message msg = this.bleHandler.obtainMessage();
/* 106 */     msg.what = 0;
/* 107 */     this.bleHandler.sendMessageDelayed(msg, 20000L);
/*     */     
/* 109 */     this.foundDevice = false;
/* 110 */     this.mBluetoothAdapter.startLeScan(this);
/*     */   }
/*     */   
/*     */   public void stopLeScan() {
/* 114 */     this.mBluetoothAdapter.stopLeScan(this);
/* 115 */     if (!this.foundDevice) {
/* 116 */       this.mBleListener.onScanTimeout(getDeviceType());
/*     */     }
/*     */   }
/*     */   
/*     */   public void connectDevice(String address) {
/* 121 */     Log.d("BLELog", "开始连接……");
/* 122 */     if (address == null) {
/* 123 */       Log.d("BLELog", "参数错误：address为空");
/* 124 */       return;
/*     */     }
/* 126 */     if (this.mBluetoothAdapter == null) {
/* 127 */       Log.d("BLELog", "BluetoothAdapter未初始化");
/* 128 */       return;
/*     */     }
/*     */     try {
/* 131 */       resetGatt();
/* 132 */       this.mBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(address);
/* 133 */       this.mBluetoothGatt = this.mBluetoothDevice.connectGatt(this.mContext, false, GetGattCallback());
/*     */       
/* 135 */       Message msgConnectTimeout = this.bleHandler.obtainMessage(1);
/* 136 */       this.bleHandler.sendMessageDelayed(msgConnectTimeout, 10000L);
/*     */     } catch (Exception e) {
/* 138 */       Log.d("BLELog", "连接出错");
/* 139 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   protected abstract BluetoothGattCallback GetGattCallback();
/*     */   
/*     */   public abstract void sendCmd(String paramString);
/*     */   
/*     */   public void resetGatt() {
/* 148 */     if (this.mBluetoothGatt != null) {
/* 149 */       this.mBluetoothGatt.disconnect();
/* 150 */       this.mBluetoothGatt.close();
/*     */     }
/* 152 */     this.mBluetoothDevice = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onError(DeviceType deviceType, String errorMsg)
/*     */   {
/* 158 */     this.mBleListener.onError(deviceType, this.mContext.getString(R.string.error_device_exception));
/* 159 */     resetGatt();
/*     */   }
/*     */   
/*     */   public void onDisconnected(DeviceType deviceType)
/*     */   {
/* 164 */     this.mBleListener.onDisconnected(deviceType);
/* 165 */     resetGatt();
/*     */   }
/*     */   
/*     */   public void onInitialized(DeviceType deviceType)
/*     */   {
/* 170 */     this.mBleListener.onInitialized(deviceType);
/* 171 */     this.bleHandler.removeMessages(1);
/*     */   }
/*     */   
/*     */   public void onCmdResponse(DeviceType deviceType, byte[] data)
/*     */   {
/* 176 */     this.mBleListener.onCmdResponse(deviceType, data);
/*     */   }
/*     */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\blelibrary\base\BaseBle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */