package com.cybermed.cdoc_patient.me.vitalcheck.model;

import com.google.gson.annotations.SerializedName;


public class ClinicVitaldata {
    @SerializedName("VitalDate")
    private String VitalDate;
    @SerializedName("Height")
    private String Height;
    @SerializedName("Weight")
    private String Weight;
    @SerializedName("HC")
    private String HC;
    @SerializedName("Temp")
    private String Temp;
    @SerializedName("BP")
    private String BP;
    @SerializedName("Pulse")
    private String Pulse;
    @SerializedName("BMI")
    private String BMI;
    @SerializedName("Glucose")
    private String Glucose;
    @SerializedName("Peak_Flow")
    private String Peak_Flow;
    @SerializedName("HGB")
    private String HGB;

    public String getVitalDate() {
        return VitalDate;
    }

    public String getHeight() {
        return Height;
    }

    public String getWeight() {
        return Weight;
    }

    public String getHC() {
        return HC;
    }

    public String getTemp() {
        return Temp;
    }

    public String getBP() {
        return BP;
    }

    public String getPulse() {
        return Pulse;
    }

    public String getBMI() {
        return BMI;
    }

    public String getGlucose() {
        return Glucose;
    }

    public String getPeak_Flow() {
        return Peak_Flow;
    }

    public String getHGB() {
        return HGB;
    }
}
