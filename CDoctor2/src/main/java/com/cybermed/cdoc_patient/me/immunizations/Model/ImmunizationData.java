package com.cybermed.cdoc_patient.me.immunizations.Model;

import com.google.gson.annotations.SerializedName;

public class ImmunizationData {
    @SerializedName("vaccine_group_id")
    private String vaccine_group_id;


    @SerializedName("vaccine_group_name")
    private String vaccine_group_name;


    @SerializedName("vaccine_name")
    private String vaccine_name;


    @SerializedName("dose_number")
    private String dose_number;

    @SerializedName("dose_date")
    private String dose_date;

    public String getVaccine_group_id() {
        return vaccine_group_id;
    }

    public String getVaccine_group_name() {
        return vaccine_group_name;
    }

    public String getVaccine_name() {
        return vaccine_name;
    }

    public String getDose_number() {
        return dose_number;
    }

    public String getDose_date() {
        return dose_date;
    }
}
