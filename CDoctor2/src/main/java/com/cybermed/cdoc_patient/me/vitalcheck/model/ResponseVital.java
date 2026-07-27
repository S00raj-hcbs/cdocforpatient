package com.cybermed.cdoc_patient.me.vitalcheck.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseVital {
    @SerializedName("data")
    private List<ClinicVitaldata> clinicVitaldata;

    public List<ClinicVitaldata> getClinicVitaldata() {
        return clinicVitaldata;
    }
}
