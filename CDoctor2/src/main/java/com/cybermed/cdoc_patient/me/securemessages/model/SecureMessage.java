package com.cybermed.cdoc_patient.me.securemessages.model;

import com.google.gson.annotations.SerializedName;

public class SecureMessage{

	@SerializedName("patient_id")
	private String patientId;

	@SerializedName("msg_body")
	private String msgBody;

	@SerializedName("provider_id")
	private String providerId;

	@SerializedName("msg_subject")
	private String msgSubject;


	public void setMsgSubject(String msgSubject) {
		this.msgSubject = msgSubject;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
}