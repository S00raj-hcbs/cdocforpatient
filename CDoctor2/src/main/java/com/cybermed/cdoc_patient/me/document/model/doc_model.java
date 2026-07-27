package com.cybermed.cdoc_patient.me.document.model;

import com.google.gson.annotations.SerializedName;

public class doc_model {
    @SerializedName("doc_id")
    private int doc_id;

    @SerializedName("category_id")
    private int category_id;

    @SerializedName("referral_id")
    private int referral_id;

    @SerializedName("category")
    private String category;

    @SerializedName("file_type")
    private String file_type;

    @SerializedName("doc_title")
    private String doc_title;

    @SerializedName("doc_url")
    private String doc_url;

    @SerializedName("buffer")
    private String buffer;

    @SerializedName("size_bytes")
    private int size_bytes;

    @SerializedName("is_locked")
    private String is_locked;

    @SerializedName("entry_date")
    private String entry_date;

    @SerializedName("change_date")
    private String change_date;

    public int getDoc_id() {
        return doc_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getReferral_id() {
        return referral_id;
    }

    public String getCategory() {
        return category;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getDoc_title() {
        return doc_title;
    }

    public String getDoc_url() {
        return doc_url;
    }

    public String getBuffer() {
        return buffer;
    }

    public int getSize_bytes() {
        return size_bytes;
    }

    public String getIs_locked() {
        return is_locked;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public String getChange_date() {
        return change_date;
    }
}
