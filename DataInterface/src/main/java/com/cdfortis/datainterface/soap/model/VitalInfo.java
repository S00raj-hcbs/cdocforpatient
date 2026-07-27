package com.cdfortis.datainterface.soap.model;

import com.cdfortis.datainterface.annotation.DataField;

import org.ksoap2.serialization.SoapObject;


public class VitalInfo extends SoapObjectData {

    @DataField
    public String chief_complaint;
    @DataField
    public String pulse;
    @DataField
    public String temperature;
    @DataField
    public String bph;
    @DataField
    public String bpl;
    @DataField
    public String height;
    @DataField
    public String weight;
    @DataField
    public String BMI;
    @DataField
    public String MedHx;
    @DataField
    public String SocialHx;
    @DataField
    public String allergies;
    @DataField
    public String smoke_status_code;
    @DataField
    public String smoke_status_desc;
    @DataField
    public String spo2;
    @DataField
    public String timestamp;
    @DataField
    public String spo2_timestamp;
    @DataField
    public String pulse_timestamp;
    @DataField
    public String temperature_timestamp;
    @DataField
    public String BP_timestamp;
    @DataField
    public String height_timestamp;
    @DataField
    public String weight_timestamp;

    public VitalInfo(SoapObject soapObject) {
        super(soapObject);
    }
}
