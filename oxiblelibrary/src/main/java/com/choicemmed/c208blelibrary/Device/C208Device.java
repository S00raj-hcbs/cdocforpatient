/*    */ package com.choicemmed.c208blelibrary.Device;
/*    */ 
/*    */ 
/*    */ public class C208Device
/*    */ {
/*    */   private static final String TAG = "C208Device";
/*    */   
/*    */   private String deviceName;
/*    */   
/*    */   private String deviceID;
/*    */   
/*    */   private String deviceSN;
/*    */   private String deviceMacAddress;
/*    */   
/*    */   public C208Device() {}
/*    */   
/*    */   public C208Device(String deviceName, String deviceID, String deviceSN, String deviceMacAddress)
/*    */   {
/* 19 */     this.deviceName = deviceName;
/* 20 */     this.deviceID = deviceID;
/* 21 */     this.deviceSN = deviceSN;
/* 22 */     this.deviceMacAddress = deviceMacAddress;
/*    */   }
/*    */   
/*    */   public String getDeviceName() {
/* 26 */     return this.deviceName;
/*    */   }
/*    */   
/*    */   public void setDeviceName(String deviceName) {
/* 30 */     this.deviceName = deviceName;
/*    */   }
/*    */   
/*    */   public String getDeviceSN() {
/* 34 */     return this.deviceSN;
/*    */   }
/*    */   
/*    */   public void setDeviceSN(String deviceSN) {
/* 38 */     this.deviceSN = deviceSN;
/*    */   }
/*    */   
/*    */   public String getDeviceID() {
/* 42 */     return this.deviceID;
/*    */   }
/*    */   
/*    */   public void setDeviceID(String deviceID) {
/* 46 */     this.deviceID = deviceID;
/*    */   }
/*    */   
/*    */   public String getDeviceMacAddress() {
/* 50 */     return this.deviceMacAddress;
/*    */   }
/*    */   
/*    */   public void setDeviceMacAddress(String deviceMacAddress) {
/* 54 */     this.deviceMacAddress = deviceMacAddress;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 59 */     return "C208Device{deviceName='" + this.deviceName + '\'' + ", deviceID='" + this.deviceID + '\'' + ", deviceSN='" + this.deviceSN + '\'' + ", deviceMacAddress='" + this.deviceMacAddress + '\'' + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\Device\C208Device.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */