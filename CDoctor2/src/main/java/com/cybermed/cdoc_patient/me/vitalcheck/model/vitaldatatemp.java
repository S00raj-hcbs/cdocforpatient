package com.cybermed.cdoc_patient.me.vitalcheck.model;

import com.google.gson.annotations.SerializedName;

public class vitaldatatemp {
    @SerializedName("bphigh")
    private String bphigh;
    @SerializedName("bplow")
    private String bplow;

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
}
