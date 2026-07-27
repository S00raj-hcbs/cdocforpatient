package com.cybermed.cdoc_patient.me.securemessages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ReceivedMessagesItem implements Parcelable {

	@SerializedName(value = "msg_from", alternate = {"addr_from"})
	private String msgFrom;
	@SerializedName(value = "msg_to", alternate = {"addr_to"})
	private String msgTo;

	@SerializedName("mail_id")
	private String mailId;

	@SerializedName("msg_body")
	private String msgBody;

	@SerializedName("msg_send_date")
	private String msgSendDate;

	@SerializedName("msg_subject")
	private String msgSubject;


	@SerializedName("attachment")
	private String attachment;

	@SerializedName("is_read")
	private String isRead;

	@SerializedName("is_deleted")
	private String isDeleted;

	@SerializedName("is_urgent")
	private String isUrgent;

	@SerializedName("entry_date")
	private String entryDate;

	@SerializedName("entry_user_id")
	private String entryUserId;

	@SerializedName("external_msgID")
	private String externalMsgID;

	@SerializedName("fileName")
	private String fileName;

	@SerializedName("mimeType")
	private String mimeType;

	public String getMsgFrom(){
		return msgFrom;
	}

	public String getMailId(){
		return mailId;
	}

	public String getMsgBody(){
		return msgBody;
	}

	public String getMsgSendDate(){
		return msgSendDate;
	}

	public String getMsgSubject(){
		return msgSubject;
	}

	public void setMsgFrom(String msgFrom) {
		this.msgFrom = msgFrom;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	public String getMsgTo() {
		return msgTo;
	}

	public void setMsgTo(String msgTo) {
		this.msgTo = msgTo;
	}

	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}

	public void setMsgSendDate(String msgSendDate) {
		this.msgSendDate = msgSendDate;
	}

	public void setMsgSubject(String msgSubject) {
		this.msgSubject = msgSubject;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(String isUrgent) {
		this.isUrgent = isUrgent;
	}

	public String getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}

	public String getEntryUserId() {
		return entryUserId;
	}

	public void setEntryUserId(String entryUserId) {
		this.entryUserId = entryUserId;
	}

	public String getExternalMsgID() {
		return externalMsgID;
	}

	public void setExternalMsgID(String externalMsgID) {
		this.externalMsgID = externalMsgID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.msgFrom);
		dest.writeString(this.msgTo);
		dest.writeString(this.mailId);
		dest.writeString(this.msgBody);
		dest.writeString(this.msgSendDate);
		dest.writeString(this.msgSubject);
		dest.writeString(this.attachment);
		dest.writeString(this.isRead);
		dest.writeString(this.isDeleted);
		dest.writeString(this.isUrgent);
		dest.writeString(this.entryDate);
		dest.writeString(this.entryUserId);
		dest.writeString(this.externalMsgID);
		dest.writeString(this.fileName);
		dest.writeString(this.mimeType);
	}

	public ReceivedMessagesItem() {
	}

	protected ReceivedMessagesItem(Parcel in) {
		this.msgFrom = in.readString();
		this.msgTo = in.readString();
		this.mailId = in.readString();
		this.msgBody = in.readString();
		this.msgSendDate = in.readString();
		this.msgSubject = in.readString();
		this.attachment = in.readString();
		this.isRead = in.readString();
		this.isDeleted = in.readString();
		this.isUrgent = in.readString();
		this.entryDate = in.readString();
		this.entryUserId = in.readString();
		this.externalMsgID = in.readString();
		this.fileName = in.readString();
		this.mimeType = in.readString();
	}

	public static final Parcelable.Creator<ReceivedMessagesItem> CREATOR = new Parcelable.Creator<ReceivedMessagesItem>() {
		@Override
		public ReceivedMessagesItem createFromParcel(Parcel source) {
			return new ReceivedMessagesItem(source);
		}

		@Override
		public ReceivedMessagesItem[] newArray(int size) {
			return new ReceivedMessagesItem[size];
		}
	};
}