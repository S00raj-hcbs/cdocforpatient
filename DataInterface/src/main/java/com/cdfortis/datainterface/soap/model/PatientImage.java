package com.cdfortis.datainterface.soap.model;

import com.cdfortis.datainterface.annotation.DataField;

import org.ksoap2.serialization.SoapObject;

public class PatientImage extends SoapObjectData{
    @DataField
    public String image_index;
    @DataField
    public String image_body;
    @DataField
    public String image_location;
    @DataField
    public String image_dateAndtime;
    @DataField
    public String image_desc;
    @DataField
    public String errorMsg;

    public PatientImage(SoapObject soapObject) {
        super(soapObject);
    }
}
