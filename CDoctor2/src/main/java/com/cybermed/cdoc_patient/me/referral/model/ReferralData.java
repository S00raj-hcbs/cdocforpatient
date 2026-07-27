package com.cybermed.cdoc_patient.me.referral.model;

import com.google.gson.annotations.SerializedName;

public class ReferralData {

    @SerializedName("refer_to")
    private String referTo;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("confirmed_date")
    private String confirmedDate;

    @SerializedName("refer_from")
    private String referFrom;

    @SerializedName("refer_date")
    private String referDate;

    @SerializedName("status")
    private String status;

    @SerializedName("referral_id")
    private String referalId;

    public String getReferTo() {
        return referTo;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getConfirmedDate() {
        return confirmedDate;
    }

    public String getReferFrom() {
        return referFrom;
    }

    public String getReferDate() {
        return referDate;
    }

    public String getStatus() {
        return status;
    }

	public String getReferalId() {
		return referalId;
	}
}