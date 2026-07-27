package com.choicemmed.c208blelibrary.cmd.listener;

import com.choicemmed.c208blelibrary.Device.C208Device;

public abstract interface C208BindDeviceListener
  extends C208CommandListener
{
  public abstract void onBindDeviceSuccess(C208Device paramC208Device);
  
  public abstract void onBindDeviceFail(String paramString);
}


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\listener\C208BindDeviceListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */