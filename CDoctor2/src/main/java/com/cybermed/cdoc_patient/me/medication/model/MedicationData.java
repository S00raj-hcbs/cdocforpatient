package com.cybermed.cdoc_patient.me.medication.model;

import com.google.gson.annotations.SerializedName;

public class MedicationData {

    @SerializedName("SIG")
    private String sIG;

    @SerializedName("drug_info")
    private String drugInfo;

    @SerializedName("dispense")
    private int dispense;

    @SerializedName("physician")
    private String physician;

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    @SerializedName("entry_date")
    private String entryDate;

    @SerializedName("dosage_frequency_descript")
    private String dosageFrequency;

    @SerializedName("doseage_form")
    private String dosageForm;

    public String getSIG() {
        return sIG;
    }

    public String getDrugInfo() {
        return drugInfo;
    }

    public int getDispense() {
        return dispense;
    }

    public String getPhysician() {
        return physician;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getDosageFrequency() {
        return dosageFrequency;
    }

    public String getDosageForm() {
        return dosageForm;
    }
}