package com.choicemmed.c208blelibrary.cmd.listener;

public abstract interface C208ConnectDeviceListener
  extends C208CommandListener
{
  public abstract void onConnectedDeviceSuccess();
  
  public abstract void onConnectedDeviceFail(String paramString);
}


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\listener\C208ConnectDeviceListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */