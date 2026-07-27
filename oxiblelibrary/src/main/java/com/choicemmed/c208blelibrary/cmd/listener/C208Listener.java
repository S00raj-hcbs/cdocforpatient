package com.choicemmed.c208blelibrary.cmd.listener;

import com.choicemmed.c208blelibrary.Device.C208Device;

public interface C208Listener {

    public abstract void onBindDeviceSuccess(C208Device paramC208Device);

    public abstract void onBindDeviceFail(String paramString);

    public abstract void onConnectedDeviceSuccess();

    public abstract void onConnectedDeviceFail(String paramString);

    public abstract void onDataResponse(int paramInt1, int paramInt2);

    public abstract void onError(String paramString);

    public abstract void onStateChanged(int paramInt1, int paramInt2);

    public abstract void onDisconnected();


}
