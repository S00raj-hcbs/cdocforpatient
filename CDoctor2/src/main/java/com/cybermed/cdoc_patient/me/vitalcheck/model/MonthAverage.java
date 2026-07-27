package com.cybermed.cdoc_patient.me.vitalcheck.model;

public class MonthAverage {
    private String monthYear;
    private double average;

    public MonthAverage(String monthYear, double average) {
        this.monthYear = monthYear;
        this.average = average;
    }
    public String getMonthYear() {
        return monthYear;
    }


    public double getAverage() {
        return average;
    }


}
