package com.cybermed.cdoc_patient.login.viewmodel;

public class BaseResponse {
    private int integerVal;
    private String stringVal;
    private boolean boolVal;

    public BaseResponse(String stringVal, int integerVal) {
        this.stringVal = stringVal;
        this.integerVal = integerVal;
    }

    public BaseResponse(String stringVal, boolean boolVal) {
        this.stringVal = stringVal;
        this.boolVal = boolVal;
    }

    public BaseResponse(int integerVal, boolean boolVal) {
        this.integerVal = integerVal;
        this.boolVal = boolVal;
    }

    public BaseResponse(String stringVal) {
        this.stringVal = stringVal;
    }

    public BaseResponse(int integerVal) {
        this.integerVal = integerVal;
    }

    public int getIntegerVal() {
        return integerVal;
    }

    public void setIntegerVal(int integerVal) {
        this.integerVal = integerVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public boolean isBoolVal() {
        return boolVal;
    }

    public void setBoolVal(boolean boolVal) {
        this.boolVal = boolVal;
    }
}
