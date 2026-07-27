package com.cybermed.cdoc_patient.me.allergies.model;

import com.google.gson.annotations.SerializedName;

public class AllergiesData {

    @SerializedName("allergy_name")
    private String allergy_name;
    @SerializedName("allergy_status")
    private String allergy_status;
    @SerializedName("reaction_notes")
    private String reaction_notes;
  /*  @SerializedName("compositeID")
    private String source;
    @SerializedName("source")
    private String conceptID;
    @SerializedName("conceptID")
    private String compositeID;
    @SerializedName("conceptTypeID")
    private String conceptTypeID;
    @SerializedName("severity_ID")
    private String severity_ID;*/
    @SerializedName("severity_name")
    private String severity_name;

    public String getAllergy_name() {
        return allergy_name;
    }

    public String getAllergy_status() {
        return allergy_status;
    }

    public String getReaction_notes() {
        return reaction_notes;
    }

    public String getSeverity_name() {
        return severity_name;
    }
}
