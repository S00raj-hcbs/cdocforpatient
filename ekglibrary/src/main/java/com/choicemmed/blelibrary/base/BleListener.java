package com.choicemmed.blelibrary.base;

public abstract interface BleListener
{
  public abstract void onFoundDevice(DeviceType paramDeviceType, String paramString1, String paramString2);
  
  public abstract void onScanTimeout(DeviceType paramDeviceType);
  
  public abstract void onError(DeviceType paramDeviceType, String paramString);
  
  public abstract void onDisconnected(DeviceType paramDeviceType);
  
  public abstract void onInitialized(DeviceType paramDeviceType);
  
  public abstract void onCmdResponse(DeviceType paramDeviceType, byte[] paramArrayOfByte);
}


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\blelibrary\base\BleListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */