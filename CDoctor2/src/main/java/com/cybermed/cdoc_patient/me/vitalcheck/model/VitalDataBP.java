package com.cybermed.cdoc_patient.me.vitalcheck.model;

import com.google.gson.annotations.SerializedName;

public class VitalDataBP {
    @SerializedName("bphigh")
    private String bphigh;
    @SerializedName("bplow")
    private String bplow;
    @SerializedName("entry_date")
    private String entry_date;

    public String getBphigh() {
        return bphigh;
    }

    public void setBphigh(String bphigh) {
        this.bphigh = bphigh;
    }

    public String getBplow() {
        return bplow;
    }

    public void setBplow(String bplow) {
        this.bplow = bplow;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }
}
