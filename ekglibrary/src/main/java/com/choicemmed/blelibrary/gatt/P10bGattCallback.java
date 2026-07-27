/*     */ package com.choicemmed.blelibrary.gatt;
/*     */ 
/*     */ import android.bluetooth.BluetoothGatt;
/*     */ import android.bluetooth.BluetoothGattCharacteristic;
/*     */ import android.bluetooth.BluetoothGattDescriptor;
/*     */ import android.bluetooth.BluetoothGattService;
/*     */ import android.util.Log;
/*     */ import com.choicemmed.blelibrary.base.BaseGattCallback;
/*     */ import com.choicemmed.blelibrary.base.DeviceType;
/*     */ import com.choicemmed.blelibrary.base.GattListener;
/*     */ import com.choicemmed.blelibrary.utils.ByteUtils;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class P10bGattCallback
/*     */   extends BaseGattCallback
/*     */ {
/*     */   private static final String Service_UUID = "0000ff00-0000-1000-8000-00805f9b34fb";
/*     */   private static final String Characteristic_UUID_Notify = "0000ff01-0000-1000-8000-00805f9b34fb";
/*     */   private static final String Characteristic_UUID_Write = "0000ff02-0000-1000-8000-00805f9b34fb";
/*     */   
/*     */   public P10bGattCallback(GattListener listener)
/*     */   {
/*  29 */     super(listener);
/*     */   }
/*     */   
/*     */   protected DeviceType getDeviceType()
/*     */   {
/*  34 */     return DeviceType.P10b;
/*     */   }
/*     */   
/*     */   public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
/*     */   {
/*  39 */     if (status != 0) {
/*  40 */       Log.d("BLELog", "异常：改变蓝牙状态失败");
/*  41 */       return;
/*     */     }
/*  43 */     switch (newState) {
/*     */     case 2: 
/*  45 */       Log.d("BLELog", "蓝牙已连接");
/*  46 */       if (gatt.discoverServices()) {
/*  47 */         Log.d("BLELog", "发现服务启动");
/*     */       } else {
/*  49 */         Log.d("BLELog", "异常：开始发现服务失败");
/*  50 */         onError("异常：开始发现服务失败");
/*     */       }
/*  52 */       break;
/*     */     
/*     */     case 0: 
/*  55 */       Log.d("BLELog", "蓝牙已断开");
/*  56 */       onDisconnected();
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */   public void onServicesDiscovered(BluetoothGatt gatt, int status)
/*     */   {
/*  64 */     if (status != 0) {
/*  65 */       Log.d("BLELog", "异常：发现服务失败，status=" + status);
/*  66 */       onError("异常：发现服务失败，status=" + status);
/*  67 */       return;
/*     */     }
/*  69 */     Log.d("BLELog", "发现服务完毕");
/*  70 */     List<BluetoothGattService> gattServices = gatt.getServices();
/*  71 */     boolean foundService = false;
/*  72 */     for (BluetoothGattService service : gattServices) {
/*  73 */       String uuid = service.getUuid().toString().toLowerCase(Locale.getDefault());
/*  74 */       if (uuid.startsWith("0000ff00-0000-1000-8000-00805f9b34fb".toLowerCase(Locale.getDefault()))) {
/*  75 */         foundService = true;
/*  76 */         BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"));
/*  77 */         if (setCharacteristicNotification(gatt, characteristic, true)) {
/*  78 */           Log.d("BLELog", "开始监听ff01成功");
/*     */         } else {
/*  80 */           Log.d("BLELog", "异常：开始监听ff01失败");
/*  81 */           onError("异常：开始监听ff01失败");
/*     */         }
/*     */       }
/*     */     }
/*  85 */     if (!foundService) {
/*  86 */       Log.d("BLELog", "异常：发现的服务中不包含心电服务");
/*  87 */       onError("异常：发现的服务中不包含心电服务");
/*     */     }
/*     */   }
/*     */   
/*     */   public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
/*     */   {
/*  93 */     if (status != 0) {
/*  94 */       Log.d("BLELog", "异常:写描述符失败，status=" + status);
/*  95 */       onError("异常:写描述符失败，status=" + status);
/*  96 */       return;
/*     */     }
/*  98 */     Log.d("BLELog", "写描述符成功");
/*  99 */     if (descriptor.getCharacteristic().getUuid().equals(UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"))) {
/* 100 */       Log.d("BLELog", "监听ff01成功");
/* 101 */       onInitialized();
/*     */     }
/*     */   }
/*     */   
/*     */   public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
/*     */   {
/* 107 */     if (status != 0) {
/* 108 */       Log.d("BLELog", "异常：写特征状态失败，status=" + status);
/* 109 */       onError("异常：写特征状态失败，status=" + status);
/* 110 */       return;
/*     */     }
/* 112 */     Log.d("BLELog", "写特征成功");
/*     */   }
/*     */   
/*     */   public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
/*     */   {
/* 117 */     byte[] data = characteristic.getValue();
/* 118 */     if ((data != null) && (data.length > 0)) {
/* 119 */       String dataString = ByteUtils.bytes2HexString(data);
/* 120 */       Log.d("BLELog", "接收数据：" + dataString);
/* 121 */       onDataRecived(data);
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean sendCmd(BluetoothGatt gatt, String cmd) {
/* 126 */     Log.d("BLELog", "发送命令开始：" + cmd);
/* 127 */     byte[] value = ByteUtils.cmdString2Bytes(cmd, true);
/* 128 */     BluetoothGattService service = gatt.getService(UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb"));
/* 129 */     BluetoothGattCharacteristic writeCharacteristic = service.getCharacteristic(UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb"));
/* 130 */     writeCharacteristic.setValue(value);
/* 131 */     boolean result = gatt.writeCharacteristic(writeCharacteristic);
/* 132 */     Log.d("BLELog", "发送命令完毕：" + cmd);
/* 133 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\blelibrary\gatt\P10bGattCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */