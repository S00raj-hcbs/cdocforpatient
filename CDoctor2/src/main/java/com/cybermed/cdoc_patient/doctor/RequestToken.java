package com.cybermed.cdoc_patient.doctor;

import com.google.gson.annotations.SerializedName;

public class RequestToken {
    @SerializedName("appID_name")
    private String appIdName;

    @SerializedName("room_number")
    private String roomNumber;

    @SerializedName("uid")
    private String uid;

    public RequestToken(String appIdName, String roomNumber, String uid) {
        this.appIdName = appIdName;
        this.roomNumber = roomNumber;
        this.uid=uid;
    }

}
