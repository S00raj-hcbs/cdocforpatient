package com.cybermed.cdoc_patient.webapi.model.request;

public class ApiCredentials {

    private String grant_type;

    private String app_id;

    private String api_key;

    public ApiCredentials(String grant_type, String app_id, String api_key) {
        this.grant_type = grant_type;
        this.app_id = app_id;
        this.api_key = api_key;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getApi_key() {
        return api_key;
    }

}

