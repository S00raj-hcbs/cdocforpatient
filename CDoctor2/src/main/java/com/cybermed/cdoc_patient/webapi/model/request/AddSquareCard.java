package com.cybermed.cdoc_patient.webapi.model.request;

public class AddSquareCard {

    private String portal_user_id;

    private String nonce;

    public AddSquareCard(String portal_user_id, String nonce) {
        this.portal_user_id = portal_user_id;
        this.nonce = nonce;
    }

    public String getPortal_user_id() {
        return portal_user_id;
    }

    public String getNonce() {
        return nonce;
    }
}
