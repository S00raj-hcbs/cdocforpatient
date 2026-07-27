package com.cybermed.cdoc_patient.me.calllog.model;

import com.google.gson.annotations.SerializedName;

public class ResCallLog {
	@SerializedName("start_time")
	private String startTime;

	@SerializedName("end_time")
	private String endTime;

	@SerializedName("provider_code")
	private String providerCode;

	@SerializedName("appt_id")
	private int apptId;

	@SerializedName("provider_name")
	private String providerName;

	@SerializedName("org_code")
	private String orgCode;

	@SerializedName("call_type")
	private String callType;

	@SerializedName("talk_min")
	private String talkMin;

	@SerializedName("charge_amount")
	private String chargeAmount;

	@SerializedName("support_provider")
	private String isSupport;

	transient private int viewType=2;

	public String getStartTime(){
		return startTime;
	}

	public String getEndTime(){
		return endTime;
	}

	public String getProviderCode(){
		return providerCode;
	}

	public int getApptId(){
		return apptId;
	}

	public String getProviderName(){
		return providerName;
	}

	public String getOrgCode(){
		return orgCode;
	}

	public String getCallType(){
		return callType;
	}

	public String getTalkMin(){
		return talkMin;
	}

	public String getChargeAmount(){
		return chargeAmount==null?"0":chargeAmount;
	}

	public int getViewType() {
		return viewType;
	}

	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getIsSupport() {
		return isSupport!=null?isSupport:"";
	}
}