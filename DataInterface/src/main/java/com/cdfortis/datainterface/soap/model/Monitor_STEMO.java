package com.cdfortis.datainterface.soap.model;

import com.cdfortis.datainterface.annotation.DataField;

import org.ksoap2.serialization.SoapObject;

public class Monitor_STEMO extends SoapObjectData {

    /*These fields cannot be changed to private*/
    @DataField
    public String Stemoscope_timestamp;

    public Monitor_STEMO(SoapObject soapObject) {
        super(soapObject);
    }
}
