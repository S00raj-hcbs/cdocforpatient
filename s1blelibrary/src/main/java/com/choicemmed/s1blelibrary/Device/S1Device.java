package com.choicemmed.s1blelibrary.Device;

/**
 * Author：ZhengZhong on 2016/10/26 14:34
 */

public class S1Device {
    private static final String TAG = "S1Device";
    private String deviceName;
    private String deviceID;
    private String deviceSN;
    private String deviceMacAddress;

    public S1Device() {

    }

    public S1Device(String deviceName, String deviceID, String deviceSN, String deviceMacAddress) {
        this.deviceName = deviceName;
        this.deviceID = deviceID;
        this.deviceSN = deviceSN;
        this.deviceMacAddress = deviceMacAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }

    @Override
    public String toString() {
        return "S1Device{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceID='" + deviceID + '\'' +
                ", deviceSN='" + deviceSN + '\'' +
                ", deviceMacAddress='" + deviceMacAddress + '\'' +
                '}';
    }
}