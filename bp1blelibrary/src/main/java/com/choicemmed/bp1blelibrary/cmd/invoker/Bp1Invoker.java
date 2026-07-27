/*     */ package com.choicemmed.bp1blelibrary.cmd.invoker;
/*     */
/*     */ import android.content.Context;
/*     */ import com.choicemmed.bp1blelibrary.Device.Bp1Device;
/*     */ import com.choicemmed.bp1blelibrary.base.BleListener;
/*     */ import com.choicemmed.bp1blelibrary.base.DeviceType;
/*     */ import com.choicemmed.bp1blelibrary.ble.Bp1Ble;
/*     */ import com.choicemmed.bp1blelibrary.cmd.command.Bp1BaseCommand;
/*     */ import com.choicemmed.bp1blelibrary.cmd.command.Bp1ConnectDeviceCommand;
/*     */ import com.choicemmed.bp1blelibrary.cmd.command.Bp1MatchPasswordCommand;
/*     */ import com.choicemmed.bp1blelibrary.cmd.factory.Bp1CommandCreator;
/*     */ import com.choicemmed.bp1blelibrary.cmd.factory.Bp1ConnectDeviceCommandFactory;
/*     */ import com.choicemmed.bp1blelibrary.cmd.factory.Bp1DisconnectDeviceCommandFactory;
/*     */ import com.choicemmed.bp1blelibrary.cmd.factory.Bp1MatchPasswordCommandFactory;
/*     */ import com.choicemmed.bp1blelibrary.cmd.factory.Bp1ScanBleCommandFactory;
/*     */ import com.choicemmed.bp1blelibrary.cmd.factory.Bp1StopScanBleCommandFactory;
import com.choicemmed.bp1blelibrary.cmd.listener.Bp1Listener;
/*     */ import com.choicemmed.bp1blelibrary.cmd.parse.Bp1ParseData;
/*     */ import com.choicemmed.bp1blelibrary.utils.ByteUtils;
/*     */ import com.choicemmed.bp1blelibrary.utils.LogUtils;
/*     */ import com.choicemmed.bp1blelibrary.R;
/*     */ import java.util.HashMap;
/*     */
/*     */
/*     */
/*     */ public class Bp1Invoker
/*     */ {
/*     */   private static final String TAG = "Bp1Invoker";
/*     */   private final Context mContext;
/*     */   private Bp1Ble bp1Ble;
/*     */   private Bp1Device s1Device;
/*     */   private Bp1Listener bp1Listener;
/*     */   private Bp1BaseCommand previousCommand;
/*     */   private Bp1BaseCommand s1Command;
/*     */   public static final int BLUETOOTH_FREE = 0;
/*     */   public static final int BLUETOOTH_SCANNING = 2;
/*     */   public static final int BLUETOOTH_FOUND_DEVICE = 4;
/*     */   public static final int BLUETOOTH_CONNECTING_DEVICE = 8;
/*     */   public static final int BLUETOOTH_GETTING_DEVICE_INFO = 16;
/*     */   public static final int BLUETOOTH_GOT_DEVICE_INFO = 32;
/*     */   public static final int BLUETOOTH_CONNECT_SUCCESS = 64;
/*     */   public static final int BLUETOOTH_MEASURE_COMPLETE = 128;
/*     */   public static final int ERROR_BIND_DEVICE = -1;
/*     */   private int bleState;
/*  80 */   private static boolean bindOrConnectState = false;
/*     */
/*     */   public Bp1Invoker(final Context mContext, Bp1Listener listener) {
/*  83 */     this.bp1Listener = listener;
/*  84 */     this.mContext = mContext;
/*  85 */     this.bp1Ble = new Bp1Ble(mContext, new BleListener()
/*     */     {
/*     */       public void onFoundDevice(DeviceType deviceType, String address, String deviceName) {
/*  88 */         Bp1Invoker.this.changeState(4);
/*  89 */         Bp1Invoker.this.s1Device.setDeviceMacAddress(address);
/*  90 */         Bp1Invoker.this.s1Device.setDeviceName(deviceName);
/*  91 */         Bp1ConnectDeviceCommand s1Command = (Bp1ConnectDeviceCommand)Bp1Invoker.this.getCommand(new Bp1ConnectDeviceCommandFactory());
/*  92 */         s1Command.setAddress(address);
/*  93 */         s1Command.execute();
/*  94 */         Bp1Invoker.this.changeState(8);
/*     */       }
/*     */
/*     */       public void onScanTimeout(DeviceType deviceType)
/*     */       {
/*  99 */         LogUtils.d("Bp1Invoker", "onScanTimeout....");
///* 100 */         Bp1Invoker.this.bp1Listener.onBindDeviceFail(mContext.getString(R.string.bind_device_fail));
    /* 100 */         Bp1Invoker.this.bp1Listener.onScanTimeout(deviceType);
    /* 101 */         Bp1Invoker.this.changeState(0);
/*     */       }
/*     */
/*     */       public void onError(DeviceType deviceType, String errorMsg)
/*     */       {
/* 106 */         Bp1Invoker.this.bp1Listener.onError(errorMsg);
/* 107 */         Bp1Invoker.this.changeState(0);
/*     */       }
/*     */
/*     */       public void onDisconnected(DeviceType deviceType)
/*     */       {
/* 112 */         LogUtils.d("Bp1Invoker", "onDisconnected....断开");
/* 113 */         Bp1Invoker.this.changeState(0);
/* 114 */         Bp1Invoker.this.bp1Listener.onDisconnected();
/*     */       }
/*     */
/*     */
/*     */       public void onInitialized(DeviceType deviceType)
/*     */       {
/* 120 */         LogUtils.d("Bp1Invoker", "onInitialized....发配对密码命令");
/* 121 */         Bp1Invoker.this.s1Command = Bp1Invoker.this.getCommand(new Bp1MatchPasswordCommandFactory());
/* 122 */         Bp1Invoker.this.s1Command.execute();
/* 123 */         Bp1Invoker.this.previousCommand = new Bp1MatchPasswordCommand(Bp1Invoker.this.bp1Ble);
/* 124 */         if (!Bp1Invoker.bindOrConnectState) {
/* 125 */           Bp1Invoker.this.changeState(16);
/*     */         }
/* 127 */         Bp1Invoker.this.bp1Listener.onBindDeviceSuccess(Bp1Invoker.this.s1Device);
/* 128 */         Bp1Invoker.this.bp1Listener.onConnectedDeviceSuccess();
/*     */       }
/*     */
/*     */       public void onCmdResponse(DeviceType deviceType, byte[] result)
/*     */       {
/* 133 */         String data = ByteUtils.bytes2HexString(result);
/*     */
/* 135 */         if ((Bp1Invoker.this.previousCommand instanceof Bp1MatchPasswordCommand)) {
/* 136 */           if (!data.contains("55aa")) {
/* 137 */             Bp1Invoker.this.bp1Listener.onBindDeviceFail(mContext.getString(R.string.exception_read_device_sn));
/* 138 */             return;
/*     */           }
/*     */
/* 141 */           if ((Bp1Invoker.bindOrConnectState) && (result[2] == 3) && ((result[3] & 0xFF) == 177)) {
/* 142 */             boolean matchSuccess = Bp1ParseData.parseMatchResult(data);
/* 143 */             if (matchSuccess) {
/* 144 */               if (Bp1Invoker.this.bp1Listener != null) {
/* 145 */                 Bp1Invoker.this.bp1Listener.onConnectedDeviceSuccess();
/* 146 */                 Bp1Invoker.this.changeState(64);
/*     */               }
/*     */
/*     */             }
/* 150 */             else if (Bp1Invoker.this.bp1Listener != null) {
/* 151 */               Bp1Invoker.this.bp1Listener.onConnectedDeviceFail(mContext.getString(R.string.exception_match_fail));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */
/*     */       public void onDataResponse(DeviceType deviceType, byte[] data)
/*     */       {
/* 159 */         String dataResponse = ByteUtils.bytes2HexString(data);
/* 160 */         LogUtils.d("Bp1Invoker", dataResponse);
/*     */
/* 162 */         HashMap<Integer, Integer> map = Bp1ParseData.parseBp(data);
/* 163 */         Bp1Invoker.this.bp1Listener.onDataResponse(((Integer)map.get(Integer.valueOf(1))).intValue(),
/* 164 */           ((Integer)map.get(Integer.valueOf(2))).intValue(),
/* 165 */           ((Integer)map.get(Integer.valueOf(3))).intValue(),Bp1Invoker.this.s1Device.getDeviceMacAddress());
/* 166 */         Bp1Invoker.this.changeState(128);
/*     */       }
/* 168 */     });
/* 169 */     this.s1Device = new Bp1Device();
/*     */   }
/*     */
/*     */
/*     */
/*     */   public void bindDevice()
/*     */   {
/* 176 */     bindOrConnectState = false;
/*     */
/* 178 */     this.s1Command = getCommand(new Bp1ScanBleCommandFactory());
/* 179 */     this.s1Command.execute();
/* 180 */     changeState(2);
/*     */   }
/*     */
/*     */   public void stopDeviceScan()
/*     */   {
    /* 176 */     bindOrConnectState = false;
    /* 178 */     this.s1Command = getCommand(new Bp1StopScanBleCommandFactory());
    /* 179 */     this.s1Command.execute();
    /* 180 */     changeState(0);
    /*     */   }






/*     */
/*     */   public void connectDevice(Bp1Device s1Device)
/*     */   {
/* 189 */     bindOrConnectState = true;
/* 190 */     Bp1ConnectDeviceCommand s1Command = (Bp1ConnectDeviceCommand)getCommand(new Bp1ConnectDeviceCommandFactory());
/* 191 */     s1Command.setAddress(s1Device.getDeviceMacAddress());
/* 192 */     s1Command.execute();
/* 193 */     changeState(8);
/*     */   }
/*     */
/*     */
/*     */
/*     */   public void disconnectDevice()
/*     */   {
/* 200 */     this.s1Command = getCommand(new Bp1DisconnectDeviceCommandFactory());
/* 201 */     this.s1Command.execute();
/*     */   }
/*     */
/*     */   public void close() {
/* 205 */     this.bp1Ble.close();
/*     */   }
/*     */
/*     */   private Bp1BaseCommand getCommand(Bp1CommandCreator commandFactory) {
/* 209 */     return commandFactory.createCommand(this.bp1Ble);
/*     */   }
/*     */
/*     */   private synchronized void changeState(int state) {
/* 213 */     if (state == this.bleState) {
/* 214 */       return;
/*     */     }
/* 216 */     this.bp1Listener.onStateChanged(this.bleState, state);
/* 217 */     LogUtils.d("Bp1Invoker", "onStateChanged " + this.bleState + " new state " + state);
/* 218 */     this.bleState = state;
/*     */   }
/*     */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\invoker\Bp1Invoker.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */