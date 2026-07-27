package com.cybermed.cdoc_patient.me.vitalcheck.model;

import com.google.gson.annotations.SerializedName;

public class ReqVitalData {

    @SerializedName("measureTime")
    private String mTimestamp;
    @SerializedName("type")
    private String mType;
    @SerializedName("value")
    private String mValue;

    public String getmTimestamp() {
        return mTimestamp;
    }

    public void setmTimestamp(String mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmValue() {
        return mValue;
    }

    public void setmValue(String mValue) {
        this.mValue = mValue;
    }
}
