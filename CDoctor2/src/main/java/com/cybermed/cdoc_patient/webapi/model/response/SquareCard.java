package com.cybermed.cdoc_patient.webapi.model.response;

public class SquareCard {

    private String id;

    private String last_4;

    private String card_brand;

    private int exp_month;

    private int exp_year;

    private String cardholder_name;

    public String getId() {
        return id;
    }

    public String getLast_4() {
        return last_4;
    }

    public String getCard_brand() {
        return card_brand;
    }

    public int getExp_month() {
        return exp_month;
    }

    public int getExp_year() {
        return exp_year;
    }

    public String getCardholder_name() {
        return cardholder_name;
    }
}
