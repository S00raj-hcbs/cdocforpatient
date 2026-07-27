package com.cybermed.cdoc_patient.webapi.model.response;


import com.google.gson.annotations.SerializedName;

public class TriageConfigResponse{

	@SerializedName("triageConfiguration")
	private String triageConfiguration;

	public String getTriageConfiguration(){
		return triageConfiguration;
	}
}