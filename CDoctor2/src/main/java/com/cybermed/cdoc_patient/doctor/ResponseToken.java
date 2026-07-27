package com.cybermed.cdoc_patient.doctor;

import com.google.gson.annotations.SerializedName;

public class ResponseToken {
    @SerializedName("token")
    private String agoraToken;

    public String getAgoraToken() {
        return agoraToken;
    }
}

