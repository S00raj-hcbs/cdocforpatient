package com.cybermed.cdoc_patient.me.calllog.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for call log list
 */
public class ReqCallLog {

    @SerializedName("page_num")
    private int pageNumber;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("count_per_page")
    private int countPerPage;

    @SerializedName("date_to_search")
    private String dateToSearch;


    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setCountPerPage(int countPerPage) {
        this.countPerPage = countPerPage;
    }

    public int getCountPerPage() {
        return countPerPage;
    }


    public void setDateToSearch(String dateToSearch) {
        this.dateToSearch = dateToSearch;
    }

    public String getDateToSearch() {
        return dateToSearch;
    }


}
