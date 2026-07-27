package com.choicemmed.bp1blelibrary.cmd.listener;

import com.choicemmed.bp1blelibrary.Device.Bp1Device;
import com.choicemmed.bp1blelibrary.base.DeviceType;

public interface Bp1Listener
{
  void onBindDeviceSuccess(Bp1Device s1Device);
  
  void onBindDeviceFail(String failMessage);
  
  void onConnectedDeviceSuccess();
  
  void onConnectedDeviceFail(String failMessage);
  
  void onDataResponse(int paramInt1, int paramInt2, int paramInt3, String macAddress);
  
  void onError(String paramString);
  
  void onDisconnected();

  void onStateChanged(int bleState, int state);

  void onScanTimeout(DeviceType deviceType);
}


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\listener\Bp1Listener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */