package com.cybermed.cdoc_patient.me.referral.model;

import com.cdfortis.datainterface.soap.model.SoapObjectData;
import com.google.gson.annotations.SerializedName;

import org.ksoap2.serialization.SoapObject;

public class ResponsePdf extends SoapObjectData {

    @SerializedName("base64")
    private String pdf;

    @SerializedName("errorMsg")
    private String errrorMessage;

    public String getErrrorMessage() {
        return errrorMessage;
    }

    public String getPdf() {
        return pdf;
    }

    public ResponsePdf(SoapObject soapObject) {
        super(soapObject);
    }
}