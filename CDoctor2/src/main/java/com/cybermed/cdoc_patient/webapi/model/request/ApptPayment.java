package com.cybermed.cdoc_patient.webapi.model.request;

public class ApptPayment {

    private String org_code;

    private String appt_id;

    private String card_id;

    public ApptPayment(String org_code, String appt_id, String card_id) {
        this.org_code = org_code;
        this.appt_id = appt_id;
        this.card_id = card_id;
    }

    public String getOrg_code() {
        return org_code;
    }

    public String getAppt_id() {
        return appt_id;
    }

    public String getCard_id() {
        return card_id;
    }
}
