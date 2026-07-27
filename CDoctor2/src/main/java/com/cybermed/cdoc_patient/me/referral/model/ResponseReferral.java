package com.cybermed.cdoc_patient.me.referral.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ResponseReferral{

	@SerializedName("referral")
	private List<ReferralData> referral;

	public List<ReferralData> getReferral(){
		return referral;
	}
}