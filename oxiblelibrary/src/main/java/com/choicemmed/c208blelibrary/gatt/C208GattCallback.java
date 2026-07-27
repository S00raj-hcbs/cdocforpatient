/*     */ package com.choicemmed.c208blelibrary.gatt;
/*     */ 
/*     */ import android.bluetooth.BluetoothGatt;
/*     */ import android.bluetooth.BluetoothGattCharacteristic;
/*     */ import android.bluetooth.BluetoothGattDescriptor;
/*     */ import android.bluetooth.BluetoothGattService;
/*     */ import android.util.Log;
/*     */ import com.choicemmed.c208blelibrary.base.BaseGattCallback;
/*     */ import com.choicemmed.c208blelibrary.base.DeviceType;
/*     */ import com.choicemmed.c208blelibrary.base.GattListener;
/*     */ import com.choicemmed.c208blelibrary.utils.ByteUtils;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class C208GattCallback
/*     */   extends BaseGattCallback
/*     */ {
/*     */   private static final String DEVICE_UUID_PREFIX = "ba11f08c5f140b0d1080";
/*  23 */   private static final UUID Characteristic_UUID_CD01 = UUID.fromString("0000cd01-0000-1000-8000-00805f9b34fb");
/*     */   
/*  25 */   private static final UUID Characteristic_UUID_CD02 = UUID.fromString("0000cd02-0000-1000-8000-00805f9b34fb");
/*     */   
/*  27 */   private static final UUID Characteristic_UUID_CD03 = UUID.fromString("0000cd03-0000-1000-8000-00805f9b34fb");
/*     */   
/*  29 */   private static final UUID Characteristic_UUID_CD04 = UUID.fromString("0000cd04-0000-1000-8000-00805f9b34fb");
/*     */   
/*  31 */   private static final UUID Characteristic_UUID_CD20 = UUID.fromString("0000cd20-0000-1000-8000-00805f9b34fb");
/*     */   public static final String TAG = "C208GattCallback";
/*     */   private static BluetoothGattService c208Service;
/*     */   
/*     */   public C208GattCallback(GattListener gattListener) {
/*  36 */     super(gattListener);
/*     */   }
/*     */   
/*     */   protected DeviceType getDeviceType()
/*     */   {
/*  41 */     return DeviceType.C208;
/*     */   }
/*     */   
/*     */   public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
/*     */   {
/*  46 */     super.onConnectionStateChange(gatt, status, newState);
/*  47 */     if (status != 0) {
/*  48 */       Log.d("C208GattCallback", "异常：改变蓝牙状态失败，status=" + status);
/*  49 */       onDisconnected();
/*  50 */       return;
/*     */     }
/*  52 */     switch (newState) {
/*     */     case 2: 
/*  54 */       Log.d("C208GattCallback", "蓝牙已连接");
/*  55 */       if (gatt.discoverServices()) {
/*  56 */         Log.d("C208GattCallback", "发现服务启动");
/*     */       } else {
/*  58 */         Log.d("C208GattCallback", "异常：开始发现服务失败");
/*  59 */         onError("异常：开始发现服务失败");
/*     */       }
/*  61 */       break;
/*     */     
/*     */     case 0: 
/*  64 */       Log.d("C208GattCallback", "蓝牙已断开");
/*  65 */       onDisconnected();
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */   public void onServicesDiscovered(BluetoothGatt gatt, int status)
/*     */   {
/*  73 */     super.onServicesDiscovered(gatt, status);
/*  74 */     if (status != 0) {
/*  75 */       Log.d("BLELog", "异常：发现服务失败，status=" + status);
/*  76 */       onError("异常：发现服务失败，status=" + status);
/*  77 */       return;
/*     */     }
/*  79 */     boolean foundService = false;
/*  80 */     List<BluetoothGattService> service = gatt.getServices();
/*  81 */     for (BluetoothGattService service1 : service) {
/*  82 */       String serviceUUID = service1.getUuid().toString();
/*  83 */       String serviceUUID4Compare = serviceUUID.toLowerCase().replace("-", "");
/*  84 */       if (serviceUUID4Compare.contains("ba11f08c5f140b0d1080")) {
/*  85 */         foundService = true;
/*  86 */         BluetoothGattCharacteristic characteristic = service1.getCharacteristic(Characteristic_UUID_CD01);
/*  87 */         if (setCharacteristicNotification(gatt, characteristic, true)) {
/*  88 */           c208Service = service1;
/*  89 */           Log.d("BLELog", "开始监听notify1成功");
/*     */         } else {
/*  91 */           Log.d("BLELog", "异常：开始监听Notify1失败");
/*  92 */           onError("异常：开始监听Notify1失败");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  97 */     if (!foundService) {
/*  98 */       Log.d("BLELog", "异常：发现的服务中不包含血氧数据服务");
/*  99 */       onError("异常：发现的服务中不包含血氧数据服务");
/*     */     }
/*     */   }
/*     */   
/*     */   public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
/*     */   {
/* 105 */     super.onCharacteristicChanged(gatt, characteristic);
/* 106 */     byte[] data = characteristic.getValue();
/*     */     
/* 108 */     if ((characteristic.getUuid().equals(Characteristic_UUID_CD04) & data != null & data.length > 0)) {
/* 109 */       onDataReceived(data);
/* 110 */       return;
/*     */     }
/* 112 */     onCommandReceived(data);
/*     */   }
/*     */   
/*     */ 
/*     */   public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
/*     */   {
/* 118 */     super.onDescriptorWrite(gatt, descriptor, status);
/* 119 */     if (status != 0) {
/* 120 */       Log.d("BLELog", "异常:写描述符失败，status=" + status);
/* 121 */       onError("异常:写描述符失败，status=" + status);
/* 122 */       return;
/*     */     }
/*     */     
/*     */ 
/* 126 */     BluetoothGattService service = descriptor.getCharacteristic().getService();
/* 127 */     if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD01))
/*     */     {
/* 129 */       BluetoothGattCharacteristic characteristic = service.getCharacteristic(Characteristic_UUID_CD02);
/* 130 */       Log.d("BLELog", "监听notify1成功");
/* 131 */       if (setCharacteristicNotification(gatt, characteristic, true)) {
/* 132 */         Log.d("BLELog", "开始监听notify2成功");
/*     */       } else {
/* 134 */         Log.d("BLELog", "异常：开始监听Notify2失败");
/* 135 */         onError("异常：开始监听Notify2失败");
/*     */       }
/* 137 */     } else if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD02))
/*     */     {
/* 139 */       BluetoothGattCharacteristic characteristic = service.getCharacteristic(Characteristic_UUID_CD03);
/* 140 */       Log.d("BLELog", "监听notify2成功");
/* 141 */       if (setCharacteristicNotification(gatt, characteristic, true)) {
/* 142 */         Log.d("BLELog", "开始监听notify3成功");
/*     */       } else {
/* 144 */         Log.d("BLELog", "异常：开始监听Notify3失败");
/* 145 */         onError("异常：开始监听Notify3失败");
/*     */       }
/* 147 */     } else if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD03))
/*     */     {
/* 149 */       BluetoothGattCharacteristic characteristic = service.getCharacteristic(Characteristic_UUID_CD04);
/*     */       
/* 151 */       Log.d("BLELog", "监听notify3成功");
/* 152 */       if (setCharacteristicNotification(gatt, characteristic, true)) {
/* 153 */         Log.d("BLELog", "开始监听notify4成功");
/*     */       } else {
/* 155 */         Log.d("BLELog", "异常：开始监听Notify4失败");
/* 156 */         onError("异常：开始监听Notify4失败");
/*     */       }
/*     */     }
/* 159 */     else if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD04)) {
/* 160 */       Log.d("BLELog", "监听notify4成功");
/* 161 */       onInitialized();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
/*     */   {
/* 169 */     if (status != 0) {
/* 170 */       Log.d("BLELog", "异常：写特征状态失败，status=" + status);
/* 171 */       onError("异常：写特征状态失败，status=" + status);
/* 172 */       return;
/*     */     }
/* 174 */     Log.d("BLELog", "写特征成功");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean sendCmd(BluetoothGatt gatt, String command)
/*     */   {
/* 185 */     BluetoothGattCharacteristic write20 = c208Service.getCharacteristic(Characteristic_UUID_CD20);
/* 186 */     byte[] value = ByteUtils.cmdString2Bytes(command, true);
/* 187 */     write20.setValue(value);
/* 188 */     return gatt.writeCharacteristic(write20);
/*     */   }
/*     */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\gatt\C208GattCallback.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */