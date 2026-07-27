package com.cybermed.cdoc_patient.doctor.docDetail.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProviderLocationListModel implements Serializable {

    @SerializedName("office_location")
    private String office_location;
    @SerializedName("facility_name")
    private String facility_name;
    @SerializedName("facility_addr1")
    private String facility_addr1;
    @SerializedName("facility_addr2")
    private String facility_addr2;
    @SerializedName("facility_city")
    private String facility_city;
    @SerializedName("facility_state")
    private String facility_state;
    @SerializedName("facility_zip")
    private String facility_zip;

    public String getOffice_location() {
        return office_location;
    }

    public void setOffice_location(String office_location) {
        this.office_location = office_location;
    }

    public String getFacility_name() {
        return facility_name;
    }

    public void setFacility_name(String facility_name) {
        this.facility_name = facility_name;
    }

    public String getFacility_addr1() {
        return facility_addr1;
    }

    public void setFacility_addr1(String facility_addr1) {
        this.facility_addr1 = facility_addr1;
    }

    public String getFacility_addr2() {
        return facility_addr2;
    }

    public void setFacility_addr2(String facility_addr2) {
        this.facility_addr2 = facility_addr2;
    }

    public String getFacility_city() {
        return facility_city;
    }

    public void setFacility_city(String facility_city) {
        this.facility_city = facility_city;
    }

    public String getFacility_state() {
        return facility_state;
    }

    public void setFacility_state(String facility_state) {
        this.facility_state = facility_state;
    }

    public String getFacility_zip() {
        return facility_zip;
    }

    public void setFacility_zip(String facility_zip) {
        this.facility_zip = facility_zip;
    }
}
