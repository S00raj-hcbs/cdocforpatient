package com.cybermed.cdoc_patient.me.labereport.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseLabReport{

	@SerializedName("results")
	private List<LabReportData> results;

	public List<LabReportData> getResults(){
		return results;
	}
}