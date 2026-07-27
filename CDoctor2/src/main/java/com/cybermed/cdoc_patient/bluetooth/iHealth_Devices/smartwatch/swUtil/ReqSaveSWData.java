
package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ReqSaveSWData {

    @SerializedName("bpm")
    private String mBpm;
    @SerializedName("device_mac_address")
    private String mDeviceMacAddress;
    @SerializedName("hub_mac_address")
    private String mHubMacAddress;
    @SerializedName("measureTime")
    private String mMeasureTime;
    @SerializedName("measureTimeFormatted")
    private String mMeasureTimeFormatted;
    @SerializedName("timestamp")
    private String mTimestamp;
    @SerializedName("type")
    private String mType;
    @SerializedName("value")
    private String mValue;

    public String getBpm() {
        return mBpm;
    }

    public void setBpm(String bpm) {
        mBpm = bpm;
    }

    public String getDeviceMacAddress() {
        return mDeviceMacAddress;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        mDeviceMacAddress = deviceMacAddress;
    }

    public String getHubMacAddress() {
        return mHubMacAddress;
    }

    public void setHubMacAddress(String hubMacAddress) {
        mHubMacAddress = hubMacAddress;
    }

    public String getMeasureTime() {
        return mMeasureTime;
    }

    public void setMeasureTime(String measureTime) {
        mMeasureTime = measureTime;
    }

    public String getMeasureTimeFormatted() {
        return mMeasureTimeFormatted;
    }

    public void setMeasureTimeFormatted(String measureTimeFormatted) {
        mMeasureTimeFormatted = measureTimeFormatted;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

}
