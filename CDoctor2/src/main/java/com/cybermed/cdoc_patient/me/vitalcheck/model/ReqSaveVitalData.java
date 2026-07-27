package com.cybermed.cdoc_patient.me.vitalcheck.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReqSaveVitalData {
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("vital_records")
    private List<ReqVitalData> mVitalRecord;

    @SerializedName("device_type")
    private String device_type;
    public List<ReqVitalData> getmVitalRecord() {
        return mVitalRecord;
    }

    public void setmVitalRecord(List<ReqVitalData> mVitalRecord) {
        this.mVitalRecord = mVitalRecord;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }
}
