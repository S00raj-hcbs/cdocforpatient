/*     */ package com.choicemmed.c208blelibrary.cmd.invoker;
/*     */
/*     */ import android.content.Context;
/*     */ import com.choicemmed.c208blelibrary.Device.C208Device;
/*     */ import com.choicemmed.c208blelibrary.R;
import com.choicemmed.c208blelibrary.R.string;
/*     */ import com.choicemmed.c208blelibrary.base.BleListener;
/*     */ import com.choicemmed.c208blelibrary.base.DeviceType;
/*     */ import com.choicemmed.c208blelibrary.ble.C208Ble;
/*     */ import com.choicemmed.c208blelibrary.cmd.command.C208BaseCommand;
/*     */ import com.choicemmed.c208blelibrary.cmd.command.C208ConnectDeviceCommand;
/*     */ import com.choicemmed.c208blelibrary.cmd.command.C208MatchPasswordCommand;
/*     */ import com.choicemmed.c208blelibrary.cmd.command.C208ObtainDeviceIDCommand;
/*     */ import com.choicemmed.c208blelibrary.cmd.command.C208ObtainDeviceSNCommand;
/*     */ import com.choicemmed.c208blelibrary.cmd.factory.C208ConnectDeviceCommandFactory;
/*     */ import com.choicemmed.c208blelibrary.cmd.factory.C208CreateCommandListener;
/*     */ import com.choicemmed.c208blelibrary.cmd.factory.C208DisconnectDeviceCommandFactory;
/*     */ import com.choicemmed.c208blelibrary.cmd.factory.C208MatchPasswordCommandFactory;
/*     */ import com.choicemmed.c208blelibrary.cmd.factory.C208ObtainDeviceIDCommandFactory;
/*     */ import com.choicemmed.c208blelibrary.cmd.factory.C208ObtainDeviceSNCommandFactory;
/*     */ import com.choicemmed.c208blelibrary.cmd.factory.C208ScanBleCommandFactory;
/*     */ import com.choicemmed.c208blelibrary.cmd.factory.C208StopScanBleCommandFactory;
import com.choicemmed.c208blelibrary.cmd.listener.C208BindDeviceListener;
/*     */ import com.choicemmed.c208blelibrary.cmd.listener.C208CommandListener;
/*     */ import com.choicemmed.c208blelibrary.cmd.listener.C208ConnectDeviceListener;
/*     */ import com.choicemmed.c208blelibrary.cmd.listener.C208DisconnectCommandListener;
/*     */ import com.choicemmed.c208blelibrary.cmd.parse.C208ParseData;
/*     */ import com.choicemmed.c208blelibrary.utils.ByteUtils;
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class C208Invoker
/*     */   implements BleListener
/*     */ {
/*     */   private static final String TAG = "C208Invoker";
/*     */   private Context mContext;
/*     */   private C208Ble c208Ble;
/*     */   private C208Device c208Device;
/*     */   private C208BindDeviceListener c208BindDeviceListener;
/*     */   private C208ConnectDeviceListener c208ConnectDeviceListener;
/*     */   private C208BaseCommand previousCommand;
/*     */   private C208DisconnectCommandListener c208DisconnectCommandListener;
/*     */   private C208CommandListener c208CommandListener;
/*     */   private C208BaseCommand c208Command;
/*     */   public static final int BLUETOOTH_FREE = 0;
/*     */   public static final int BLUETOOTH_SCANNING = 2;
/*     */   public static final int BLUETOOTH_FOUND_DEVICE = 4;
/*     */   public static final int BLUETOOTH_CONNECTING_DEVICE = 8;
/*     */   public static final int BLUETOOTH_GETTING_DEVICE_INFO = 16;
/*     */   public static final int BLUETOOTH_GOT_DEVICE_INFO = 32;
/*     */   public static final int BLUETOOTH_CONNECT_SUCCESS = 64;
/*     */   public static final int BLUETOOTH_MEASURE_COMPLETE = 128;
/*  83 */   private static boolean bindOrConnectState = false;
/*     */
/*     */   public C208Invoker(Context mContext) {
/*  86 */     this.mContext = mContext;
/*  87 */     this.c208Ble = new C208Ble(mContext, this);
/*  88 */     this.c208Device = new C208Device();
/*     */   }
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */   public void bindDevice(C208BindDeviceListener listener)
/*     */   {
/*  98 */     bindOrConnectState = false;
/*  99 */     this.c208BindDeviceListener = listener;
/* 100 */     this.c208CommandListener = listener;
/* 101 */     this.c208Command = getCommand(new C208ScanBleCommandFactory());
/* 102 */     this.c208Command.execute();
/* 103 */     this.c208BindDeviceListener.onStateChanged(0, 2);
/*     */   }
/*     */

    /*     */   public void stopDeviceScan(C208BindDeviceListener listener)
    /*     */   {
        /*  98 */     bindOrConnectState = false;
        /*  99 */     this.c208BindDeviceListener = listener;
        /* 100 */     this.c208CommandListener = listener;
        /* 101 */     this.c208Command = getCommand(new C208StopScanBleCommandFactory());
        /* 102 */     this.c208Command.execute();
        /* 103 */     this.c208BindDeviceListener.onStateChanged(0, 2);
        /*     */   }

/*     */   public void connectDevice(C208Device c208Device, C208ConnectDeviceListener listener)
/*     */   {
/* 112 */     bindOrConnectState = true;
/* 113 */     this.c208ConnectDeviceListener = listener;
/* 114 */     this.c208CommandListener = listener;
/* 115 */     C208ConnectDeviceCommand c208Command = (C208ConnectDeviceCommand)getCommand(new C208ConnectDeviceCommandFactory());
/* 116 */     c208Command.setAddress(c208Device.getDeviceMacAddress());
/* 117 */     c208Command.execute();
/* 118 */     this.c208ConnectDeviceListener.onStateChanged(0, 8);
/*     */   }
/*     */
/*     */
/*     */
/*     */   public void disconnectDevice(C208DisconnectCommandListener listener)
/*     */   {
/* 125 */     this.c208DisconnectCommandListener = listener;
/* 126 */     this.c208Command = getCommand(new C208DisconnectDeviceCommandFactory());
/* 127 */     this.c208Command.execute();
/*     */   }
/*     */
/*     */   public void onFoundDevice(DeviceType deviceType, String address, String deviceName)
/*     */   {
/* 132 */     this.c208BindDeviceListener.onStateChanged(2, 4);
/* 133 */     this.c208Device.setDeviceMacAddress(address);
/* 134 */     this.c208Device.setDeviceName(deviceName);
/* 135 */     C208ConnectDeviceCommand c208Command = (C208ConnectDeviceCommand)getCommand(new C208ConnectDeviceCommandFactory());
/* 136 */     c208Command.setAddress(address);
/* 137 */     c208Command.execute();
/* 138 */     this.c208BindDeviceListener.onStateChanged(4, 8);
/*     */   }
/*     */
/*     */   public void onScanTimeout(DeviceType deviceType)
/*     */   {
/* 143 */     this.c208BindDeviceListener.onScanTimeout(this.mContext.getString(R.string.bind_device_fail));
/*     */   }
/*     */
/*     */   public void onError(DeviceType deviceType, String errorMsg)
/*     */   {
/* 148 */     this.c208CommandListener.onError(errorMsg);
/*     */   }
/*     */
/*     */   public void onDisconnected(DeviceType deviceType)
/*     */   {
/* 153 */     if (this.c208DisconnectCommandListener != null) {
/* 154 */       this.c208DisconnectCommandListener.onDisconnected();
/*     */     }
/*     */   }
/*     */
/*     */   public void onInitialized(DeviceType deviceType)
/*     */   {
/* 160 */     this.c208Command = getCommand(new C208MatchPasswordCommandFactory());
/* 161 */     this.c208Command.execute();
/* 162 */     this.previousCommand = new C208MatchPasswordCommand(this.c208Ble);
/* 163 */     if (!bindOrConnectState) {
/* 164 */       this.c208BindDeviceListener.onStateChanged(8, 16);
/*     */     }
/*     */   }
/*     */
/*     */   public void onCmdResponse(DeviceType deviceType, byte[] result)
/*     */   {
/* 170 */     String data = ByteUtils.bytes2HexString(result);
/*     */
/* 172 */     if ((!bindOrConnectState) && ((this.previousCommand instanceof C208MatchPasswordCommand))) {
/* 173 */       if (!data.contains("55aa")) {
/* 174 */         this.c208BindDeviceListener.onBindDeviceFail(this.mContext.getString(R.string.exception_read_device_sn));
/* 175 */         return;
/*     */       }
/* 177 */       boolean matchSuccess = C208ParseData.parseMatchResult(data);
/* 178 */       if (matchSuccess) {
/* 179 */         this.c208Command = getCommand(new C208ObtainDeviceSNCommandFactory());
/* 180 */         this.c208Command.execute();
/* 181 */         this.previousCommand = new C208ObtainDeviceSNCommand(this.c208Ble);
/*     */       } else {
/* 183 */         this.c208BindDeviceListener.onBindDeviceFail(this.mContext.getString(R.string.exception_match_fail));
/* 184 */         return;
/*     */       }
/*     */     }
/* 187 */     else if ((!bindOrConnectState) && ((this.previousCommand instanceof C208ObtainDeviceSNCommand))) {
/* 188 */       if (!data.contains("55aa")) {
/* 189 */         this.c208BindDeviceListener.onBindDeviceFail(this.mContext.getString(R.string.exception_read_device_sn));
/* 190 */         return;
/*     */       }
/* 192 */       String SN = C208ParseData.parseDeviceSN(data);
/* 193 */       this.c208Device.setDeviceSN(SN);
/* 194 */       this.c208Command = getCommand(new C208ObtainDeviceIDCommandFactory());
/* 195 */       this.c208Command.execute();
/* 196 */       this.previousCommand = new C208ObtainDeviceIDCommand(this.c208Ble);
/* 197 */     } else if ((!bindOrConnectState) && ((this.previousCommand instanceof C208ObtainDeviceIDCommand))) {
/* 198 */       if (!data.contains("55aa")) {
/* 199 */         this.c208BindDeviceListener.onBindDeviceFail(this.mContext.getString(R.string.exception_read_device_ID));
/* 200 */         return;
/*     */       }
/* 202 */       String ID = C208ParseData.parseDeviceID(data);
/* 203 */       this.c208Device.setDeviceID(ID);
/* 204 */       this.c208BindDeviceListener.onStateChanged(16, 32);
/* 205 */       this.c208BindDeviceListener.onBindDeviceSuccess(this.c208Device);
/* 206 */       this.c208BindDeviceListener.onStateChanged(32, 64);
/* 207 */       return;
/*     */     }
/*     */
/*     */
/* 211 */     if ((bindOrConnectState) && ((this.previousCommand instanceof C208MatchPasswordCommand))) {
/* 212 */       if (!data.contains("55aa")) {
/* 213 */         this.c208BindDeviceListener.onBindDeviceFail(this.mContext.getString(R.string.exception_read_device_sn));
/* 214 */         return;
/*     */       }
/* 216 */       boolean matchSuccess = C208ParseData.parseMatchResult(data);
/* 217 */       if (matchSuccess) {
/* 218 */         this.c208ConnectDeviceListener.onConnectedDeviceSuccess();
/* 219 */         this.c208ConnectDeviceListener.onStateChanged(8, 64);
/*     */       } else {
/* 221 */         this.c208ConnectDeviceListener.onConnectedDeviceFail(this.mContext.getString(R.string.exception_match_fail));
/*     */       }
/*     */     }
/*     */   }
/*     */
/*     */
/*     */
/*     */   public void onDataResponse(DeviceType deviceType, byte[] data)
/*     */   {
/* 230 */     String dataResponse = ByteUtils.bytes2HexString(data);
/* 231 */     if ((dataResponse.length() != 12) || (!dataResponse.contains("55aa"))) {
/* 232 */       if (bindOrConnectState) {
/* 233 */         this.c208ConnectDeviceListener.onConnectedDeviceFail(this.mContext.getString(R.string.error_response_data));
/*     */       } else {
/* 235 */         this.c208BindDeviceListener.onBindDeviceFail(this.mContext.getString(R.string.error_response_data));
/*     */       }
/* 237 */       return;
/*     */     }
/* 239 */     if (bindOrConnectState) {
/* 240 */       this.c208ConnectDeviceListener.onDataResponse(C208ParseData.parseSpo(dataResponse), C208ParseData.parsePR(dataResponse),this.c208Device.getDeviceMacAddress());
/* 241 */       this.c208ConnectDeviceListener.onStateChanged(64, 128);
/*     */     } else {
/* 243 */       this.c208BindDeviceListener.onDataResponse(C208ParseData.parseSpo(dataResponse), C208ParseData.parsePR(dataResponse),this.c208Device.getDeviceMacAddress());
/* 244 */       this.c208BindDeviceListener.onStateChanged(64, 128);
/*     */     }
/*     */   }
/*     */
/*     */
/*     */   public C208BaseCommand getCommand(C208CreateCommandListener commandFactory)
/*     */   {
/* 251 */     if ((commandFactory instanceof C208ScanBleCommandFactory)) {
/* 252 */       return new C208ScanBleCommandFactory().createCommand(this.c208Ble);
/*     */     }
    /* 251 */     if ((commandFactory instanceof C208StopScanBleCommandFactory)) {
        /* 252 */       return new C208StopScanBleCommandFactory().createCommand(this.c208Ble);
        /*     */     }
/* 254 */     if ((commandFactory instanceof C208ConnectDeviceCommandFactory)) {
/* 255 */       return new C208ConnectDeviceCommandFactory().createCommand(this.c208Ble);
/*     */     }
/* 257 */     if ((commandFactory instanceof C208DisconnectDeviceCommandFactory)) {
/* 258 */       return new C208DisconnectDeviceCommandFactory().createCommand(this.c208Ble);
/*     */     }
/* 260 */     if ((commandFactory instanceof C208MatchPasswordCommandFactory)) {
/* 261 */       return new C208MatchPasswordCommandFactory().createCommand(this.c208Ble);
/*     */     }
/* 263 */     if ((commandFactory instanceof C208ObtainDeviceIDCommandFactory)) {
/* 264 */       return new C208ObtainDeviceIDCommandFactory().createCommand(this.c208Ble);
/*     */     }
/* 266 */     if ((commandFactory instanceof C208ObtainDeviceSNCommandFactory)) {
/* 267 */       return new C208ObtainDeviceSNCommandFactory().createCommand(this.c208Ble);
/*     */     }
/* 269 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\invoker\C208Invoker.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */