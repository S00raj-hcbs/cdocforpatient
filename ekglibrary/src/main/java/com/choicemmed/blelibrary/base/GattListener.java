package com.choicemmed.blelibrary.base;

public abstract interface GattListener
{
  public abstract void onError(DeviceType paramDeviceType, String paramString);
  
  public abstract void onDisconnected(DeviceType paramDeviceType);
  
  public abstract void onInitialized(DeviceType paramDeviceType);
  
  public abstract void onCmdResponse(DeviceType paramDeviceType, byte[] paramArrayOfByte);
}


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\blelibrary\base\GattListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */