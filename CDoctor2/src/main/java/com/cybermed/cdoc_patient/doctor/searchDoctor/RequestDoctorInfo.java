package com.cybermed.cdoc_patient.doctor.searchDoctor;

import com.google.gson.annotations.SerializedName;

public class RequestDoctorInfo {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("key")
    private String key;
    @SerializedName("is_support")
    private boolean isSupport;

    public RequestDoctorInfo(String userId, String key,boolean isSupport) {
        this.userId = userId;
        this.key = key;
        this.isSupport=isSupport;
    }
}
