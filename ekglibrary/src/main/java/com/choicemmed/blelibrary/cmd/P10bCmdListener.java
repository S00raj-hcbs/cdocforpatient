package com.choicemmed.blelibrary.cmd;

import com.choicemmed.blelibrary.base.DeviceType;

public abstract interface P10bCmdListener
{
  public abstract void onFoundDevice(DeviceType paramDeviceType, String paramString1, String paramString2);
  
  public abstract void onScanTimeout(DeviceType paramDeviceType);
  
  public abstract void onError(DeviceType paramDeviceType, String paramString);
  
  public abstract void onDisconnected(DeviceType paramDeviceType);
  
  public abstract void onLoadBegin();
  
  public abstract void onRecordInfoResponse(String paramString1, String paramString2, int paramInt);
  
  public abstract boolean onEcgDataResponse(String paramString);
  
  public abstract void onLoadEnd(boolean paramBoolean, String paramString);
}


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\blelibrary\cmd\P10bCmdListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */