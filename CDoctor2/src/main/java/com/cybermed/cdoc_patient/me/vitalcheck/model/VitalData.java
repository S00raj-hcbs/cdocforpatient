package com.cybermed.cdoc_patient.me.vitalcheck.model;

import com.google.gson.annotations.SerializedName;

public class VitalData {
    @SerializedName("value")
    private String value;
    @SerializedName("entry_date")
    private String entry_date;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }
}
