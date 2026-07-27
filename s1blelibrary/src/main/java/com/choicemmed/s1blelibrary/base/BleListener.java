package com.choicemmed.s1blelibrary.base;

/**
 * Created by Yu Baoxiang on 2015/3/27.
 */
public interface BleListener {
    void onFoundDevice(DeviceType deviceType, String address, String deviceName);
    void onScanTimeout(DeviceType deviceType);
    void onError(DeviceType deviceType, String errorMsg);
    void onDisconnected(DeviceType deviceType);
    void onInitialized(DeviceType deviceType);
    void onCmdResponse(DeviceType deviceType, byte[] result);
    void onDataResponse(DeviceType deviceType, byte[] data);
}
