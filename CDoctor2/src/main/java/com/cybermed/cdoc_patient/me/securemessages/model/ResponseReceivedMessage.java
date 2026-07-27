package com.cybermed.cdoc_patient.me.securemessages.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseReceivedMessage {

    @SerializedName("received_messages")
    private List<ReceivedMessagesItem> receivedMessages;

    public List<ReceivedMessagesItem> getReceivedMessages(){
        return receivedMessages;
    }
}
