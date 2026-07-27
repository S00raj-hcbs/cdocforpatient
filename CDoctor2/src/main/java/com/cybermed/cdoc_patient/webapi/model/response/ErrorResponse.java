package com.cybermed.cdoc_patient.webapi.model.response;

public class ErrorResponse {

    private String error;

    public String getError() {
        return error!=null?error:"Error";
    }
}
