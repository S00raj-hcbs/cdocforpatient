package com.cybermed.cdoc_patient.doctor.docDetail.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.SerializedName;

/**
 * response model for appointment list
 */
public class ResApptList extends BaseObservable implements Parcelable {

    @SerializedName("appt_status")
    private String apptStatus;

    @SerializedName("amount_paid")
    private String amountPaid;

    @SerializedName("provider_languages")
    private String providerLanguages;

    @SerializedName("reachback_phone_number")
    private String reachbackPhoneNumber;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("room_number")
    private String roomNumber;

    @SerializedName("appt_location")
    private String apptLocation;

    @SerializedName("provider_last_name")
    private String providerLastName;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("provider_first_name")
    private String providerFirstName;
    @SerializedName("provider_id")
    private String provider_id;

    @SerializedName("chief_complaint")
    private String chiefComplaint;
    @SerializedName("chief_complaint_notes")
    private String chiefComplaintNotes;

    @SerializedName("provider_specialties")
    private String providerSpecialties;

    @SerializedName("provider_code")
    private String providerCode;

    @SerializedName("appt_id")
    private String apptId;

    @SerializedName("talk_time")
    private String talkTime;

    @SerializedName("appt_date")
    private String apptDate;

    @SerializedName("org_code")
    private String orgCode;

    @SerializedName("account")
    private String account;

    @SerializedName("provider_img")
    private String providerImage;

    @SerializedName("support_provider")
    private String isSupport;

    @SerializedName("provider_online_status")
    private String provider_online_status;
    transient private String apptType;

    private transient int pastViewListSize;
    private transient int futureViewListSize;
    transient private int viewType=2;
    public String getProvider_online_status() {
        return provider_online_status;
    }

    public void setProvider_online_status(String provider_online_status) {
        this.provider_online_status = provider_online_status;
    }

    public String getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(String provider_id) {
        this.provider_id = provider_id;
    }

    public String getProviderImage() {
        return providerImage == null ? "" : providerImage;
    }

    public String getApptStatus() {
        return apptStatus;
    }

    public void setApptStatus(String apptStatus) {
        this.apptStatus = apptStatus;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public String getProviderLanguages() {
        return providerLanguages;
    }

    public String getReachbackPhoneNumber() {
        return reachbackPhoneNumber;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getApptLocation() {
        return apptLocation;
    }

    public String getProviderLastName() {
        return providerLastName;
    }

    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getProviderFirstName() {
        return providerFirstName;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }
    public String getChiefComplaintNotes() {
        return chiefComplaintNotes;
    }

    public String getProviderSpecialties() {
        return providerSpecialties;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public String getApptId() {
        return apptId;
    }

    public String getTalkTime() {
        return talkTime;
    }

    public String getApptDate() {
        return apptDate;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public String getAccount() {
        return account;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apptStatus);
        dest.writeString(this.amountPaid);
        dest.writeString(this.providerLanguages);
        dest.writeString(this.reachbackPhoneNumber);
        dest.writeString(this.endTime);
        dest.writeString(this.roomNumber);
        dest.writeString(this.apptLocation);
        dest.writeString(this.providerLastName);
        dest.writeString(this.startTime);
        dest.writeString(this.providerFirstName);
        dest.writeString(this.chiefComplaint);
        dest.writeString(this.chiefComplaintNotes);
        dest.writeString(this.providerSpecialties);
        dest.writeString(this.providerCode);
        dest.writeString(this.provider_id);
        dest.writeString(this.apptId);
        dest.writeString(this.talkTime);
        dest.writeString(this.apptDate);
        dest.writeString(this.orgCode);
        dest.writeString(this.account);
        dest.writeString(this.isSupport);
    }

    public void readFromParcel(Parcel source) {
        this.apptStatus = source.readString();
        this.amountPaid = source.readString();
        this.providerLanguages = source.readString();
        this.reachbackPhoneNumber = source.readString();
        this.endTime = source.readString();
        this.roomNumber = source.readString();
        this.apptLocation = source.readString();
        this.providerLastName = source.readString();
        this.startTime = source.readString();
        this.providerFirstName = source.readString();
        this.chiefComplaint = source.readString();
        this.chiefComplaintNotes = source.readString();
        this.providerSpecialties = source.readString();
        this.providerCode = source.readString();
        this.provider_id = source.readString();
        this.apptId = source.readString();
        this.talkTime = source.readString();
        this.apptDate = source.readString();
        this.orgCode = source.readString();
        this.account = source.readString();
        this.isSupport=source.readString();
    }

    public ResApptList() {
    }

    protected ResApptList(Parcel in) {
        this.apptStatus = in.readString();
        this.amountPaid = in.readString();
        this.providerLanguages = in.readString();
        this.reachbackPhoneNumber = in.readString();
        this.endTime = in.readString();
        this.roomNumber = in.readString();
        this.apptLocation = in.readString();
        this.providerLastName = in.readString();
        this.startTime = in.readString();
        this.providerFirstName = in.readString();
        this.chiefComplaint = in.readString();
        this.chiefComplaintNotes = in.readString();
        this.providerSpecialties = in.readString();
        this.providerCode = in.readString();
        this.provider_id = in.readString();
        this.apptId = in.readString();
        this.talkTime = in.readString();
        this.apptDate = in.readString();
        this.orgCode = in.readString();
        this.account = in.readString();
        this.isSupport=in.readString();
    }

    public static final Parcelable.Creator<ResApptList> CREATOR = new Parcelable.Creator<ResApptList>() {
        @Override
        public ResApptList createFromParcel(Parcel source) {
            return new ResApptList(source);
        }

        @Override
        public ResApptList[] newArray(int size) {
            return new ResApptList[size];
        }
    };

    @Bindable
    public int getPastViewListSize() {
        return pastViewListSize;
    }

    public void setPastViewListSize(int pastViewListSize) {
        this.pastViewListSize = pastViewListSize;
        notifyPropertyChanged(BR.pastViewListSize);
    }

    @Bindable
    public int getFutureViewListSize() {
        return futureViewListSize;
    }

    public void setFutureViewListSize(int futureViewListSize) {
        this.futureViewListSize = futureViewListSize;
        notifyPropertyChanged(BR.futureViewListSize);
    }

    public String getIsSupport() {
        return isSupport!=null?isSupport:"";
    }

    public String getApptType() {
        return apptType;
    }

    public void setApptType(String apptType) {
        this.apptType = apptType;
    }
}