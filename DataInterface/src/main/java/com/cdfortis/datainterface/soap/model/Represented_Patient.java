package com.cdfortis.datainterface.soap.model;

import com.cdfortis.datainterface.annotation.DataField;

import org.ksoap2.serialization.SoapObject;

public class Represented_Patient extends SoapObjectData {

    @DataField
    public String user_id;

    @DataField
    public String first_name;

    @DataField
    public String last_name;

    @DataField
    public String errorMsg;

    public Represented_Patient(String user_id) {
        this.user_id = user_id;
    }

    public Represented_Patient(SoapObject soapObject) {
        super(soapObject);
    }
}
