package com.cybermed.cdoc_patient.me.allergies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseAllergies {

    @SerializedName("data")
    private List<AllergiesData> allergies;

    public List<AllergiesData> getAllergies() {
        return allergies;
    }
}
