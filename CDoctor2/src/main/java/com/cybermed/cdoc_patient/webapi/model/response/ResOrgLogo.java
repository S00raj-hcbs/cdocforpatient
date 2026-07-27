package com.cybermed.cdoc_patient.webapi.model.response;

import com.google.gson.annotations.SerializedName;

public class ResOrgLogo {
    @SerializedName("Org_Fullname")
    private String orgFullname;

    @SerializedName("Base64_file")
    private String base64File;

    public String getOrgFullname() {
        return orgFullname;
    }

    public String getBase64File() {
        return base64File;
    }
}

