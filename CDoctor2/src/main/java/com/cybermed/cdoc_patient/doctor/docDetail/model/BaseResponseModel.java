package com.cybermed.cdoc_patient.doctor.docDetail.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @param <T> model type
 */
public class BaseResponseModel<T> {
    @SerializedName("data")
    private T object;

    public T getObject() {
        return object;
    }

    /**
     * parameter for appointment list count
     */
    @SerializedName("totalRecords")
    private int totalRecords;

    public int getTotalRecords() {
        return totalRecords;
    }
}
