
package com.cybermed.cdoc_patient.doctor.searchDoctor;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@SuppressWarnings("unused")
public class ResponseDocInfo implements Serializable {

    @SerializedName("addr1")
    private String addr1;
    @SerializedName("addr2")
    private String addr2;
    @SerializedName("city")
    private String city;
    @SerializedName("favorite")
    private String favorite;
    @SerializedName("is_favorite")
    private boolean favorite_doc;
    @SerializedName("first_name")
    private String first_name;
    @SerializedName("incremental_charge")
    private String incremental_charge;
    @SerializedName("incremental_min")
    private String incremental_min;
    @SerializedName("initial_charge")
    private String initial_charge;
    @SerializedName("initial_min")
    private String initial_min;
    @SerializedName("languages")
    private String languages;
    @SerializedName("last_name")
    private String last_name;
    @SerializedName("online_status")
    private String online_status;
    @SerializedName("org_code")
    private String org_code;
    @SerializedName("provider_code")
    private String provider_code;
    @SerializedName("provider_desc")
    private String mProviderDesc;
    @SerializedName("review_score")
    private String review_score;
    @SerializedName("specialties")
    private String specialties;
    @SerializedName("state")
    private String state;
    @SerializedName("zip")
    private String zip;
    @SerializedName("profile_img")
    private String profileImage;
    @SerializedName("sex")
    private String gender;
    @SerializedName("is_reschedule")
    private boolean is_reschedule;
    @SerializedName("appt_id")
    private String ApptId;
    @SerializedName("chief_complaint")
    private String chiefComplaint;
    @SerializedName("chief_complaint_note")
    private String chiefComplaintNote;
    private transient int payingMode = 0;
    private transient int waitingRoom = 0;
    private transient String apptTime;
    private transient String cardId;
    private transient int paymentType = 0;
    private transient boolean isVideoAppoitnmentType;

    public boolean isIs_reschedule() {
        return is_reschedule;
    }

    public void setIs_reschedule(boolean is_reschedule) {
        this.is_reschedule = is_reschedule;
    }

    public String getApptId() {
        return ApptId;
    }

    public void setApptId(String apptId) {
        ApptId = apptId;
    }

    public boolean isVideoAppoitnmentType() {
        return isVideoAppoitnmentType;
    }

    public void setVideoAppoitnmentType(boolean videoAppoitnmentType) {
        this.isVideoAppoitnmentType = videoAppoitnmentType;
    }

    public boolean getFavDoc() {
        return favorite_doc;
    }

    public String getProfileImage() {
        return profileImage==null?"":profileImage;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public String getCardId() {
        return cardId != null ? cardId : "";
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getGender() {
        return gender == null ? "" : gender;
    }

    public String getApptTime() {
        return apptTime != null ? apptTime : "";
    }

    public void setApptTime(String apptTime) {
        this.apptTime = apptTime;
    }

    public int getWaitingRoom() {
        return waitingRoom;
    }

    public void setWaitingRoom(int waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    public int getPayingMode() {
        return payingMode;
    }

    public void setPayingMode(int payingMode) {
        this.payingMode = payingMode;
    }

    public String getAddr1() {
        return addr1 == null ? "" : addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2 == null ? "" : addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getCity() {
        return city == null ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFavorite() {
        return favorite == null ? "" : favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getFirstName() {
        return first_name == null ? "" : first_name.charAt(0) + first_name.substring(1).toLowerCase();
    }

    public void setFirstName(String firstName) {
        first_name = firstName;
    }

    public String getIncrementalCharge() {
        return incremental_charge;
    }

    public void setIncrementalCharge(String incrementalCharge) {
        incremental_charge = incrementalCharge;
    }

    public String getIncrementalMin() {
        return incremental_min;
    }

    public void setIncrementalMin(String incrementalMin) {
        incremental_min = incrementalMin;
    }

    public String getInitialCharge() {
        return initial_charge;
    }

    public void setInitialCharge(String initialCharge) {
        initial_charge = initialCharge;
    }

    public String getInitialMin() {
        return initial_min;
    }

    public void setInitialMin(String initialMin) {
        initial_min = initialMin;
    }

    public String getLanguages() {
        return languages == null ? "" : languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getLastName() {
        return last_name == null ? "" : last_name.charAt(0) + last_name.substring(1).toLowerCase();
    }

    public void setLastName(String lastName) {
        last_name = lastName;
    }

    public String getOnlineStatus() {
        return online_status == null ? "" : online_status;
    }

    public void setOnlineStatus(String onlineStatus) {
        online_status = onlineStatus;
    }

    public String getOrgCode() {
        return org_code == null ? "" : org_code;
    }

    public void setOrgCode(String orgCode) {
        org_code = orgCode;
    }

    public String getProviderCode() {
        return provider_code == null ? "" : provider_code;
    }

    public void setProviderCode(String providerCode) {
        provider_code = providerCode;
    }

    public String getProviderDesc() {
        return mProviderDesc;
    }

    public void setProviderDesc(String providerDesc) {
        mProviderDesc = providerDesc;
    }

    public String getReviewScore() {
        return review_score == null ? "" : review_score.equals("0") ? "" : review_score;
    }

    public void setReviewScore(String reviewScore) {
        review_score = reviewScore;
    }

    public String getSpecialties() {
        return specialties == null ? "" : specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public String getState() {
        return state == null ? "" : state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip == null ? "" : zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public String getChiefComplaintNote() {
        return chiefComplaintNote;
    }

    public void setChiefComplaintNote(String chiefComplaintNote) {
        this.chiefComplaintNote = chiefComplaintNote;
    }
}
