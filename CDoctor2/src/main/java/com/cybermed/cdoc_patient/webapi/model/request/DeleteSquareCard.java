package com.cybermed.cdoc_patient.webapi.model.request;

public class DeleteSquareCard {

    private String portal_user_id;

    private String card_id;

    public DeleteSquareCard(String portal_user_id, String card_id) {
        this.portal_user_id = portal_user_id;
        this.card_id = card_id;
    }

    public String getPortal_user_id() {
        return portal_user_id;
    }

    public String getCard_id() {
        return card_id;
    }
}
