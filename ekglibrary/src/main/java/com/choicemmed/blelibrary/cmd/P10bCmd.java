/*     */ package com.choicemmed.blelibrary.cmd;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.os.Handler;
/*     */ import android.os.Message;
/*     */ import android.util.Log;
/*     */ import com.choicemmed.blelibrary.R;
import com.choicemmed.blelibrary.R.string;
/*     */ import com.choicemmed.blelibrary.base.BleListener;
/*     */ import com.choicemmed.blelibrary.base.DeviceType;
/*     */ import com.choicemmed.blelibrary.ble.P10bBle;
/*     */ import com.choicemmed.blelibrary.utils.ByteUtils;
/*     */ import com.choicemmed.blelibrary.utils.FormatUtils;
/*     */ import java.util.Calendar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class P10bCmd
/*     */   implements BleListener
/*     */ {
/*     */   protected static final String LogTag_BLE = "BLELog";
/*     */   private static final int CMD_RESPONSE_TIMEOUT = 10000;
/*     */   private Context mContext;
/*     */   private P10bCmdListener mP10bCmdListener;
/*     */   private P10bBle mP10bBle;
/*  30 */   private String mRespString = "";
/*  31 */   private int mCurrentIndex = 0;
/*     */   
/*  33 */   private boolean receivedResp = false;
/*     */   
/*     */   private static final int MSG_CHECKRESP = 0;
/*     */   
/*  37 */   private Handler bleHandler = new Handler()
/*     */   {
/*     */     public void handleMessage(Message msg) {
/*  40 */       switch (msg.what) {
/*     */       case 0: 
/*  42 */         if (P10bCmd.this.receivedResp) {
/*  43 */           P10bCmd.this.receivedResp = false;
/*  44 */           P10bCmd.this.bleHandler.sendEmptyMessageDelayed(0, 10000L);
/*     */         }
/*     */         else {
/*  47 */           P10bCmd.this.cmdRespTimeout();
/*     */         }
/*  49 */         break;
/*     */       }
/*     */       
/*     */     }
/*     */   };
/*     */   
/*     */   public P10bCmd(Context context, P10bCmdListener p10bCmdListener)
/*     */   {
/*  57 */     this.mContext = context;
/*  58 */     this.mP10bCmdListener = p10bCmdListener;
/*  59 */     this.mP10bBle = new P10bBle(context, this);
/*     */   }
/*     */   
/*     */   public void startLeScan() {
/*  63 */     this.mP10bBle.startLeScan();
/*     */   }
/*     */   
/*     */   public void connectDevice(String address) {
/*  67 */     this.mP10bBle.connectDevice(address);
/*     */   }
/*     */   
/*     */   public void onFoundDevice(DeviceType deviceType, String address, String deviceName)
/*     */   {
/*  72 */     this.mP10bCmdListener.onFoundDevice(deviceType, address, deviceName);
/*     */   }
/*     */   
/*     */   public void onScanTimeout(DeviceType deviceType)
/*     */   {
/*  77 */     this.mP10bCmdListener.onScanTimeout(deviceType);
/*     */   }
/*     */   
/*     */   public void onError(DeviceType deviceType, String errorMsg)
/*     */   {
/*  82 */     this.mP10bCmdListener.onError(deviceType, errorMsg);
/*     */   }
/*     */   
/*     */   public void onDisconnected(DeviceType deviceType)
/*     */   {
/*  87 */     this.mP10bCmdListener.onDisconnected(deviceType);
/*     */   }
/*     */   
/*     */   public void onInitialized(DeviceType deviceType)
/*     */   {
/*  92 */     Log.d("BLELog", "初始化完毕");
/*  93 */     Calendar calendar = Calendar.getInstance();
/*  94 */     String year = String.format("%02x", new Object[] { Integer.valueOf(calendar.get(1) - 2000) });
/*  95 */     String month = String.format("%02x", new Object[] { Integer.valueOf(calendar.get(2) + 1) });
/*  96 */     String day = String.format("%02x", new Object[] { Integer.valueOf(calendar.get(5)) });
/*  97 */     sendCmd("55aa7a" + year + month + day);
/*  98 */     Log.d("BLELog", "发送[设置日期]命令");
/*     */   }
/*     */   
/*     */   public void onCmdResponse(DeviceType deviceType, byte[] result)
/*     */   {
/* 103 */     this.receivedResp = true;
/* 104 */     if ((result == null) || (result.length <= 0)) {
/* 105 */       return;
/*     */     }
/* 107 */     String resultString = ByteUtils.bytes2HexString(result);
/* 108 */     this.mRespString += resultString;
/* 109 */     Log.d("BLELog", "mRespString.length=" + this.mRespString.length());
/* 110 */     if (this.mRespString.length() > 10) {
/* 111 */       String lenLowByte = this.mRespString.substring(6, 8);
/* 112 */       String lenHighByte = this.mRespString.substring(8, 10);
/* 113 */       int len = Integer.parseInt(lenHighByte + lenLowByte, 16);
/* 114 */       if (this.mRespString.length() != len * 2 + 12) {
/* 115 */         return;
/*     */       }
/*     */     }
/* 118 */     this.bleHandler.removeMessages(0);
/*     */     
/* 120 */     if ((this.mRespString.length() == 20) && (this.mRespString.startsWith("55aa7a")))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 129 */       Calendar calendar = Calendar.getInstance();
/* 130 */       String hour = String.format("%02x", new Object[] { Integer.valueOf(calendar.get(11)) });
/* 131 */       String minute = String.format("%02x", new Object[] { Integer.valueOf(calendar.get(12)) });
/* 132 */       String second = String.format("%02x", new Object[] { Integer.valueOf(calendar.get(13)) });
/* 133 */       sendCmd("55aa7b" + hour + minute + second);
/* 134 */       Log.d("BLELog", "发送[设置时间]命令");
/*     */     }
/* 136 */     else if ((this.mRespString.length() == 20) && (this.mRespString.startsWith("55aa7b")))
/*     */     {
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
/* 149 */       this.mCurrentIndex = 0;
/* 150 */       this.mP10bCmdListener.onLoadBegin();
/* 151 */       sendCmd("55aa60000000");
/* 152 */       Log.d("BLELog", "发送[获取记录条数]命令");
/*     */     }
/* 154 */     else if ((this.mRespString.length() == 14) && (this.mRespString.startsWith("55aa60")))
/*     */     {
/* 156 */       int ecgDataCount = Integer.parseInt(this.mRespString.substring(10, 12), 16);
/* 157 */       if (ecgDataCount == 0) {
/* 158 */         Log.d("BLELog", "设备中没有数据");
/* 159 */         this.mP10bCmdListener.onLoadEnd(false, this.mContext.getString(R.string.error_null_device_data));
/* 160 */         this.mP10bBle.resetGatt();
/* 161 */         return;
/*     */       }
/*     */       
/* 164 */       this.mCurrentIndex = ecgDataCount;
/* 165 */       sendCmd(String.format("55aa61%02x0000", new Object[] { Integer.valueOf(this.mCurrentIndex) }));
/* 166 */       Log.d("BLELog", "发送[获取第" + this.mCurrentIndex + "条记录信息]命令");
/*     */     }
/* 168 */     else if ((this.mRespString.length() == 36) && (this.mRespString.startsWith("55aa61")))
/*     */     {
/* 170 */       int day = Integer.parseInt(this.mRespString.substring(10, 12), 16);
/* 171 */       int month = Integer.parseInt(this.mRespString.substring(12, 14), 16);
/* 172 */       int year = Integer.parseInt(this.mRespString.substring(14, 16), 16) + 2000;
/* 173 */       int second = Integer.parseInt(this.mRespString.substring(16, 18), 16);
/* 174 */       int minute = Integer.parseInt(this.mRespString.substring(18, 20), 16);
/* 175 */       int hour = Integer.parseInt(this.mRespString.substring(20, 22), 16);
/*     */       
/* 177 */       Calendar calendar = Calendar.getInstance();
/* 178 */       calendar.set(year, month - 1, day, hour, minute, second);
/* 179 */       String measureTime = FormatUtils.getDateTimeString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
/*     */       
/* 181 */       String ecgHeartRateL = this.mRespString.substring(30, 32);
/* 182 */       String ecgHeartRateH = this.mRespString.substring(32, 34);
/* 183 */       int ecgHeartRate = Integer.parseInt(ecgHeartRateH + ecgHeartRateL, 16);
/*     */       
/* 185 */       this.mP10bCmdListener.onRecordInfoResponse(this.mRespString, measureTime, ecgHeartRate);
/*     */       
/* 187 */       sendCmd(String.format("55aa69%02x0000", new Object[] { Integer.valueOf(this.mCurrentIndex) }));
/* 188 */       Log.d("BLELog", "发送[获取第" + this.mCurrentIndex + "条记录心电数据]命令");
/*     */     }
/* 190 */     else if ((this.mRespString.length() == 22512) && (this.mRespString.startsWith("55aa69"))) {
/* 191 */       String ecgData = this.mRespString;
/* 192 */       if (this.mP10bCmdListener.onEcgDataResponse(ecgData)) {
/* 193 */         sendCmd(String.format("55aa63%02x0000", new Object[] { Integer.valueOf(this.mCurrentIndex) }));
/* 194 */         Log.d("BLELog", "发送[删除第" + this.mCurrentIndex + "条记录]命令");
/*     */       }
/*     */       else {
/* 197 */         this.mP10bCmdListener.onLoadEnd(false, this.mContext.getString(R.string.error_device_exception));
/*     */       }
/*     */     }
/* 200 */     else if ((this.mRespString.length() == 14) && (this.mRespString.startsWith("55aa63"))) {
/* 201 */       int delResult = Integer.parseInt(this.mRespString.substring(10, 12), 16);
/* 202 */       if (delResult != 0) {
/* 203 */         Log.d("BLELog", "删除数据出错，mCurrentIndex=" + this.mCurrentIndex);
/* 204 */         this.mP10bCmdListener.onLoadEnd(false, this.mContext.getString(R.string.error_device_exception));
/* 205 */         this.mP10bBle.resetGatt();
/* 206 */         return;
/*     */       }
/* 208 */       if (this.mCurrentIndex > 1)
/*     */       {
/*     */ 
/* 211 */         this.mCurrentIndex -= 1;
/* 212 */         sendCmd(String.format("55aa61%02x0000", new Object[] { Integer.valueOf(this.mCurrentIndex) }));
/* 213 */         Log.d("BLELog", "发送[获取第" + this.mCurrentIndex + "条记录信息]命令");
/*     */       }
/*     */       else
/*     */       {
/* 217 */         this.mP10bCmdListener.onLoadEnd(true, "");
/* 218 */         this.mP10bBle.resetGatt();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void sendCmd(String cmd) {
/* 224 */     this.mRespString = "";
/* 225 */     this.receivedResp = false;
/* 226 */     this.bleHandler.sendEmptyMessageDelayed(0, 10000L);
/* 227 */     this.mP10bBle.sendCmd(cmd);
/*     */   }
/*     */   
/*     */   private void cmdRespTimeout()
/*     */   {
/* 232 */     Log.d("BLELog", "等待命令响应超时");
/* 233 */     this.mP10bCmdListener.onLoadEnd(false, this.mContext.getString(R.string.error_device_exception));
/* 234 */     this.mP10bBle.resetGatt();
/*     */   }
/*     */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\blelibrary\cmd\P10bCmd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */