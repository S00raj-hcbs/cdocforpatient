package com.cybermed.cdoc_patient.doctor.docDetail.model;

import com.google.gson.annotations.SerializedName;

/**
 * response make appointment
 */
public class ResMakeAppt{

	@SerializedName("reason")
	private String reason;

	@SerializedName("provider_code")
	private String providerCode;

	@SerializedName("appt_id")
	private String apptId;

	@SerializedName("appt_date")
	private String apptDate;

	@SerializedName("org_code")
	private String orgCode;

	@SerializedName("is_reschedule")
	private boolean is_reschedule;

	public String getReason(){
		return reason;
	}

	public String getProviderCode(){
		return providerCode;
	}

	public String getApptId(){
		return apptId;
	}

	public String getApptDate(){
		return apptDate;
	}

	public String getOrgCode(){
		return orgCode;
	}

	public boolean isIs_reschedule() {
		return is_reschedule;
	}

	public void setIs_reschedule(boolean is_reschedule) {
		this.is_reschedule = is_reschedule;
	}

	public void setApptId(String apptId) {
		this.apptId = apptId;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public void setApptDate(String apptDate) {
		this.apptDate = apptDate;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}