package com.choicemmed.c208blelibrary.cmd.listener;

public abstract interface C208CommandListener
{
  public abstract void onDataResponse(int paramInt1, int paramInt2, String macAddress);
  
  public abstract void onError(String paramString);
  
  public abstract void onStateChanged(int paramInt1, int paramInt2);

  public abstract void onScanTimeout(String message);

}


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\listener\C208CommandListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */