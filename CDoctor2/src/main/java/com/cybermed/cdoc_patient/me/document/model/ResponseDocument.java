package com.cybermed.cdoc_patient.me.document.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseDocument {
    @SerializedName("data")
    private List<doc_model> data;

    public List<doc_model> getData() {
        return data;
    }
}
