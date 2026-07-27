/*     */ package com.choicemmed.bp1blelibrary.gatt;
/*     */ 
/*     */ import android.bluetooth.BluetoothGatt;
/*     */ import android.bluetooth.BluetoothGattCharacteristic;
/*     */ import android.bluetooth.BluetoothGattDescriptor;
/*     */ import android.bluetooth.BluetoothGattService;
/*     */ import android.util.Log;
/*     */ import com.choicemmed.bp1blelibrary.base.BaseGattCallback;
/*     */ import com.choicemmed.bp1blelibrary.base.DeviceType;
/*     */ import com.choicemmed.bp1blelibrary.base.GattListener;
/*     */ import com.choicemmed.bp1blelibrary.utils.ByteUtils;
/*     */ import com.choicemmed.bp1blelibrary.utils.LogUtils;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Bp1GattCallback
/*     */   extends BaseGattCallback
/*     */ {
/*     */   public static final String TAG = "Bp1GattCallback";
/*     */   private static final String DEVICE_UUID_PREFIX = "ba11f08c5f140b0d10a0";
/*  26 */   private static final UUID Characteristic_UUID_CD01 = UUID.fromString("0000cd01-0000-1000-8000-00805f9b34fb");
/*     */   
/*  28 */   private static final UUID Characteristic_UUID_CD02 = UUID.fromString("0000cd02-0000-1000-8000-00805f9b34fb");
/*     */   
/*  30 */   private static final UUID Characteristic_UUID_CD03 = UUID.fromString("0000cd03-0000-1000-8000-00805f9b34fb");
/*     */   
/*  32 */   private static final UUID Characteristic_UUID_CD04 = UUID.fromString("0000cd04-0000-1000-8000-00805f9b34fb");
/*     */   
/*  34 */   private static final UUID Characteristic_UUID_CD20 = UUID.fromString("0000cd20-0000-1000-8000-00805f9b34fb");
/*     */   private static BluetoothGattService s1Service;
/*     */   
/*     */   public Bp1GattCallback(GattListener gattListener)
/*     */   {
/*  39 */     super(gattListener);
/*     */   }
/*     */   
/*     */   protected DeviceType getDeviceType()
/*     */   {
/*  44 */     return DeviceType.Bp1;
/*     */   }
/*     */   
/*     */   public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
/*     */   {
/*  49 */     Log.d("Bp1GattCallback", "onConnectionStateChange");
/*  50 */     super.onConnectionStateChange(gatt, status, newState);
/*  51 */     if (status != 0) {
/*  52 */       Log.d("Bp1GattCallback", "异常：改变蓝牙状态失败，status=  onDisconnected" + status);
/*  53 */       onDisconnected();
/*  54 */       return;
/*     */     }
/*  56 */     switch (newState) {
/*     */     case 2: 
/*  58 */       Log.d("Bp1GattCallback", "蓝牙已连接");
/*  59 */       if (gatt.discoverServices()) {
/*  60 */         Log.d("Bp1GattCallback", "发现服务启动");
/*     */       } else {
/*  62 */         Log.d("Bp1GattCallback", "异常：开始发现服务失败");
/*  63 */         onError("异常：开始发现服务失败");
/*     */       }
/*  65 */       break;
/*     */     
/*     */     case 0: 
/*  68 */       Log.d("Bp1GattCallback", "蓝牙已断开");
/*     */       
/*     */ 
/*  71 */       onDisconnected();
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */   public void onServicesDiscovered(BluetoothGatt gatt, int status)
/*     */   {
/*  79 */     super.onServicesDiscovered(gatt, status);
/*  80 */     if (status != 0) {
/*  81 */       Log.d("BLELog", "异常：发现服务失败，status=" + status);
/*  82 */       onError("异常：发现服务失败，status=" + status);
/*  83 */       return;
/*     */     }
/*  85 */     boolean foundService = false;
/*  86 */     List<BluetoothGattService> service = gatt.getServices();
/*  87 */     for (BluetoothGattService service1 : service) {
/*  88 */       String serviceUUID = service1.getUuid().toString();
/*  89 */       String serviceUUID4Compare = serviceUUID.toLowerCase().replace("-", "");
/*  90 */       if (serviceUUID4Compare.contains("ba11f08c5f140b0d10a0")) {
//            if (serviceUUID4Compare.contains("000015231212efde1523")) {
/*  91 */         foundService = true;
/*  92 */         BluetoothGattCharacteristic characteristic = service1.getCharacteristic(Characteristic_UUID_CD01);
/*  93 */         if (setCharacteristicNotification(gatt, characteristic, true)) {
/*  94 */           s1Service = service1;
/*  95 */           Log.d("BLELog", "开始监听notify1成功");
/*     */         } else {
/*  97 */           Log.d("BLELog", "异常：开始监听Notify1失败");
/*  98 */           onError("异常：开始监听Notify1失败");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 103 */     if (!foundService) {
/* 104 */       Log.d("BLELog", "异常：发现的服务中不包含血氧数据服务");
/* 105 */       onError("异常：发现的服务中不包含血氧数据服务");
/*     */     }
/*     */   }
/*     */   
/*     */   public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
/*     */   {
/* 111 */     super.onCharacteristicChanged(gatt, characteristic);
/* 112 */     byte[] data = characteristic.getValue();
/* 113 */     LogUtils.d("Bp1GattCallback", "<---cmd" + ByteUtils.bytes2HexString(data));
/* 114 */     if ((characteristic.getUuid().equals(Characteristic_UUID_CD04) & data != null & data.length > 0)) {
/* 115 */       onDataReceived(data);
/* 116 */       return;
/*     */     }
/* 118 */     onCommandReceived(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
/*     */   {
/* 125 */     super.onDescriptorWrite(gatt, descriptor, status);
/* 126 */     if (status != 0) {
/* 127 */       Log.d("BLELog", "异常:写描述符失败，status=" + status);
/* 128 */       onError("异常:写描述符失败，status=" + status);
/* 129 */       return;
/*     */     }
/*     */     
/*     */ 
/* 133 */     BluetoothGattService service = descriptor.getCharacteristic().getService();
/* 134 */     if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD01))
/*     */     {
/* 136 */       BluetoothGattCharacteristic characteristic = service.getCharacteristic(Characteristic_UUID_CD02);
/* 137 */       Log.d("BLELog", "监听notify1成功");
/* 138 */       if (setCharacteristicNotification(gatt, characteristic, true)) {
/* 139 */         Log.d("BLELog", "开始监听notify2成功");
/*     */       } else {
/* 141 */         Log.d("BLELog", "异常：开始监听Notify2失败");
/* 142 */         onError("异常：开始监听Notify2失败");
/*     */       }
/* 144 */     } else if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD02))
/*     */     {
/* 146 */       BluetoothGattCharacteristic characteristic = service.getCharacteristic(Characteristic_UUID_CD03);
/* 147 */       Log.d("BLELog", "监听notify2成功");
/* 148 */       if (setCharacteristicNotification(gatt, characteristic, true)) {
/* 149 */         Log.d("BLELog", "开始监听notify3成功");
/*     */       } else {
/* 151 */         Log.d("BLELog", "异常：开始监听Notify3失败");
/* 152 */         onError("异常：开始监听Notify3失败");
/*     */       }
/* 154 */     } else if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD03))
/*     */     {
/* 156 */       BluetoothGattCharacteristic characteristic = service.getCharacteristic(Characteristic_UUID_CD04);
/*     */       
/* 158 */       Log.d("BLELog", "监听notify3成功");
/* 159 */       if (setCharacteristicNotification(gatt, characteristic, true)) {
/* 160 */         Log.d("BLELog", "开始监听notify4成功");
/*     */       } else {
/* 162 */         Log.d("BLELog", "异常：开始监听Notify4失败");
/* 163 */         onError("异常：开始监听Notify4失败");
/*     */       }
/*     */     }
/* 166 */     else if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD04)) {
/* 167 */       Log.d("BLELog", "监听notify4成功");
/* 168 */       onInitialized();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
/*     */   {
/* 176 */     if (status != 0) {
/* 177 */       Log.d("BLELog", "异常：写特征状态失败，status=" + status);
/* 178 */       onError("异常：写特征状态失败，status=" + status);
/* 179 */       return;
/*     */     }
/* 181 */     Log.d("BLELog", "写特征成功");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean sendCmd(BluetoothGatt gatt, String command)
/*     */   {
/* 192 */     BluetoothGattCharacteristic write20 = s1Service.getCharacteristic(Characteristic_UUID_CD20);
/* 193 */     byte[] value = ByteUtils.cmdString2Bytes(command, true);
/* 194 */     write20.setValue(value);
/* 195 */     LogUtils.d("Bp1GattCallback", "--->cmd" + ByteUtils.bytes2HexString(value));
/* 196 */     return gatt.writeCharacteristic(write20);
/*     */   }
/*     */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\gatt\Bp1GattCallback.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */