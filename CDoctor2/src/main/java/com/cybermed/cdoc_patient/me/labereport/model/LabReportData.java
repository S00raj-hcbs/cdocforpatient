package com.cybermed.cdoc_patient.me.labereport.model;

import com.google.gson.annotations.SerializedName;

public class LabReportData {

	@SerializedName("tests")
	private String tests;

	@SerializedName("provider")
	private String provider;

	@SerializedName("request_date")
	private String requestDate;

	@SerializedName("details")
	private String details;

	@SerializedName("lab_type")
	private String labType;

	@SerializedName("lab")
	private String lab;

	@SerializedName("order_id")
	private String orderId;

	@SerializedName("status")
	private String status;

	@SerializedName("report_date")
	private String reportDate;

	public String getTests(){
		return tests;
	}

	public String getProvider(){
		return provider;
	}

	public String getRequestDate(){
		return requestDate;
	}

	public String getDetails(){
		return details;
	}

	public String getLabType(){
		return labType;
	}

	public String getLab(){
		return lab;
	}

	public String getOrderId(){
		return orderId;
	}

	public String getStatus(){
		return status;
	}

	public String getReportDate(){
		return reportDate;
	}
}