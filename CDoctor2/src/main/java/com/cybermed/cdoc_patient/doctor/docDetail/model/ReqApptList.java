package com.cybermed.cdoc_patient.doctor.docDetail.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for appointment list
 */
public class ReqApptList{

	@SerializedName("page_number")
	private int pageNumber;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("count_per_page")
	private int countPerPage;

	@SerializedName("futureOrpast")
	private int futureOrpast;

	@SerializedName("date_to_search")
	private String dateToSearch;

	@SerializedName("current_date")
	private String currentdate;

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

	public void setFutureOrpast(int futureOrpast){
		this.futureOrpast = futureOrpast;
	}

	public int getFutureOrpast(){
		return futureOrpast;
	}

	public void setDateToSearch(String dateToSearch){
		this.dateToSearch = dateToSearch;
	}

	public String getDateToSearch(){
		return dateToSearch;
	}

	public void setCurrentdate(String currentdate) {
		this.currentdate = currentdate;
	}
}