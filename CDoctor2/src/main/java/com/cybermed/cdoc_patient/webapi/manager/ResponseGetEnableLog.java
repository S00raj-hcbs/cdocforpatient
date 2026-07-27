package com.cybermed.cdoc_patient.webapi.manager;

import com.google.gson.annotations.SerializedName;

public class ResponseGetEnableLog {

    @SerializedName("org_code")
    private String orgCode;

    @SerializedName("userID")
    private String userId;

    @SerializedName("enableLog")
    private boolean enableLog;

    public String getOrgCode() {
        return orgCode;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isEnableLog() {
        return enableLog;
    }
}
