package com.cdfortis.datainterface.soap.model;

import com.cdfortis.datainterface.annotation.DataField;

import org.ksoap2.serialization.SoapObject;

public class PatientRoutine extends SoapObjectData {

    /*These fields cannot be changed to private*/
    @DataField
    public String wakeup_time;
    @DataField
    public String breakfast_time;
    @DataField
    public String lunch_time;
    @DataField
    public String dinner_time;
    @DataField
    public String bed_time;
    @DataField
    public String errorMessage;
    @DataField
    public String response_code;



    public PatientRoutine(SoapObject soapObject) {
        super(soapObject);
    }
}


