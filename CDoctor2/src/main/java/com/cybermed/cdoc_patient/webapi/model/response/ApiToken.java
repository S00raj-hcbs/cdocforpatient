package com.cybermed.cdoc_patient.webapi.model.response;

public class ApiToken {

    private String access_token;

    private String token_type;

    private int expires_in;

    private String refresh_token;

    private String refresh_token_expires;

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getRefresh_token_expires() {
        return refresh_token_expires;
    }

}

