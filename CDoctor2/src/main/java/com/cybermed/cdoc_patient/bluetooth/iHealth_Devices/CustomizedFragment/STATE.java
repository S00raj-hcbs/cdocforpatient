package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment;

public enum STATE {
    SCANNING,
    CONNECTING,
    CONNECTED,
    MEASURING,
    RESULT,
    /*Only for glucose*/
    STRIP_IN,
    DISCONNECTED;

    private String value;
    private String index = "-1";

    STATE() {
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}