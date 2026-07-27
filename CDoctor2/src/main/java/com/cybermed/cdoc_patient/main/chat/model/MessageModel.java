package com.cybermed.cdoc_patient.main.chat.model;

public class MessageModel implements ChatItem {
    private String msg_id;
    private String sender_id;
    private String sender_type;
    private String receiver_id;
    private String receiver_type;
    private String msg_type;
    private String msg_detail;
    private String msg_date;
    private String errorMsg;

    public MessageModel(String msg_id, String sender_id, String sender_type, String receiver_id, String receiver_type, String msg_type, String msg_detail, String msg_date, String errorMsg) {
        this.msg_id = msg_id;
        this.sender_id = sender_id;
        this.sender_type = sender_type;
        this.receiver_id = receiver_id;
        this.receiver_type = receiver_type;
        this.msg_type = msg_type;
        this.msg_detail = msg_detail;
        this.msg_date = msg_date;
        this.errorMsg = errorMsg;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_type() {
        return sender_type;
    }

    public void setSender_type(String sender_type) {
        this.sender_type = sender_type;
    }

    public String getReceiver_type() {
        return receiver_type;
    }

    public void setReceiver_type(String receiver_type) {
        this.receiver_type = receiver_type;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getMsg_date() {
        return msg_date;
    }

    public void setMsg_date(String msg_date) {
        this.msg_date = msg_date;
    }

    public String getMsg_detail() {
        return msg_detail;
    }

    public void setMsg_detail(String msg_detail) {
        this.msg_detail = msg_detail;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}

