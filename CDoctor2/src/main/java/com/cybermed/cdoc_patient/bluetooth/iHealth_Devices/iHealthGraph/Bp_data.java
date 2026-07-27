package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph;

import com.cdfortis.datainterface.annotation.DataField;
import com.cdfortis.datainterface.soap.model.SoapObjectData;

import org.ksoap2.serialization.SoapObject;

public class Bp_data extends SoapObjectData {
    /*These fields cannot be changed to private*/
    @DataField
    public String BPH;
    @DataField
    public String BPL;
    @DataField
    public String BP_timestamp;

    public Bp_data(SoapObject soapObject) {
        super(soapObject);
    }
}
