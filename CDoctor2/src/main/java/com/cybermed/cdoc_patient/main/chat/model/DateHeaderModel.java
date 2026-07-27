package com.cybermed.cdoc_patient.main.chat.model;

import java.util.Date;

public class DateHeaderModel implements ChatItem {
    private Date date;

    public DateHeaderModel(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
