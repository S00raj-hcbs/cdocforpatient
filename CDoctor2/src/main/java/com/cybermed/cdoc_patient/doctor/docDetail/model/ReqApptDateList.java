package com.cybermed.cdoc_patient.doctor.docDetail.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for appointment list
 */
public class ReqApptDateList {

	@SerializedName("page_num")
	private int pageNumber;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("max_appts_to_return")
	private int countPerPage;
//
//	@SerializedName("futureOrpast")
//	private int futureOrpast;

	@SerializedName("appt_date_to_search")
	private String dateToSearch;

	@SerializedName("org_code")
	private String orgCode;

	public void setPageNumber(int pageNumber){
		this.pageNumber = pageNumber;
	}

	public int getPageNumber(){
		return pageNumber;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setCountPerPage(int countPerPage){
		this.countPerPage = countPerPage;
	}

	public int getCountPerPage(){
		return countPerPage;
	}



	public void setDateToSearch(String dateToSearch){
		this.dateToSearch = dateToSearch;
	}

	public String getDateToSearch(){
		return dateToSearch;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgCode() {
		return orgCode;
	}
}