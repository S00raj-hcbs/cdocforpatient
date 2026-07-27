package com.cybermed.cdoc_patient.login;

import com.google.gson.annotations.SerializedName;

public class ResponseAppId {
    @SerializedName("appID")
    private String appId;

    public String getAppId() {
        return appId;
    }
}
