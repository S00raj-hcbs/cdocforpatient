package com.cybermed.cdoc_patient.doctor.docDetail.model;

import com.google.gson.annotations.SerializedName;

/***
 * response for doctor next available
 */
public class ResDocNextAvail {

    @SerializedName("online_status")
    private int onlineStatus;

    @SerializedName("has_avail_today")
    private boolean hasAvailToday;

    @SerializedName("next_available")
    private String nextAvailable;

    @SerializedName("provider_code")
    private String providerCode;

    @SerializedName("org_code")
    private String orgCode;

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public boolean isHasAvailToday() {
        return hasAvailToday;
    }

    public String getNextAvailable() {
        return nextAvailable!=null?nextAvailable:"";
    }

    public String getProviderCode() {
        return providerCode;
    }

    public String getOrgCode() {
        return orgCode;
    }
}