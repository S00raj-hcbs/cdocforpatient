package com.choicemmed.s1blelibrary.base;

/**
 * Created by Yu Baoxiang on 2015/4/13.
 */
public interface GattListener {
    void onError(DeviceType deviceType, String errorMsg);
    void onDisconnected(DeviceType deviceType);
    void onInitialized(DeviceType deviceType);
    void onCmdResponse(DeviceType deviceType, byte[] data);
    void onDataResponse(DeviceType deviceType, byte[] data);

}
