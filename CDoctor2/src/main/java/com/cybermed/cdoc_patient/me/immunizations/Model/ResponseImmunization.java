package com.cybermed.cdoc_patient.me.immunizations.Model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseImmunization {
    @SerializedName("immunizations")
    private List<ImmunizationData> immunizations;

    public List<ImmunizationData> getImmunizations() {
        return immunizations;
    }
}
