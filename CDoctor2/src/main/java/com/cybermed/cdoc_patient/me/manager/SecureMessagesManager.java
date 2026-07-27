package com.cybermed.cdoc_patient.me.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cybermed.cdoc_patient.me.securemessages.model.RequestSendMessage;
import com.cybermed.cdoc_patient.me.securemessages.model.ResponseProvidersList;
import com.cybermed.cdoc_patient.me.securemessages.model.ResponseReceivedMessage;
import com.cybermed.cdoc_patient.webapi.APIs.AuthApi;
import com.cybermed.cdoc_patient.webapi.AuthManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.ResponseWrapper;
import com.cybermed.cdoc_patient.webapi.RestApiCall;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SecureMessagesManager {

    Context mContext;
    private Call<ResponseBody> mRequestSendMessage;
    private Call<ResponseReceivedMessage> mRequestReceivedMessage;
    private Call<ResponseProvidersList> mRequestProviderList;
    private Call<ResponseReceivedMessage> mRequestSentMessage;

    IResponseReceiver mIResponseReceiver;
    IReponseMessageCall mIResponseCall;

    public SecureMessagesManager(IResponseReceiver iResponseReceiver) {
        mIResponseReceiver = iResponseReceiver;
    }
    public SecureMessagesManager(IReponseMessageCall iResponseReceiver) {
        mIResponseCall = iResponseReceiver;
    }

    /**
     * Send messages
     *
     * @param requestSendMessage send message model
     */
    public void sendMessage(RequestSendMessage requestSendMessage) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callSendMessageApi(requestSendMessage));
        } else {
            callSendMessageApi(requestSendMessage);
        }
    }

    void callSendMessageApi(RequestSendMessage requestSendMessage) {
        mRequestSendMessage = RestApiCall.getApiService(AuthApi.class).sendMessage(requestSendMessage);
        mRequestSendMessage.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                mIResponseCall.onSendMessageSuccess(data);
                cancelSendRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseCall.onFailure(errorResponse);
                cancelSendRequest();
            }
        }));
    }

    /**
     * Get provider list(contact list)
     *
     * @param userId      userid
     */
    public void getProviderList(String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callProviderList(userId));
        } else {
            callProviderList(userId);
        }
    }

    void callProviderList(String userId) {
        mRequestProviderList = RestApiCall.getApiService(AuthApi.class).getProviderList(userId);
        mRequestProviderList.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseProvidersList>() {
            @Override
            public void onSuccess(ResponseProvidersList data) {
                mIResponseCall.onProviderListSuccess(data);
                cancelProvidersRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseCall.onFailure(errorResponse);
                cancelProvidersRequest();
            }
        }));
    }

    /**
     * Message List
     *
     * @param userId userid
     */
    public void getMessageList(String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callMessageListApi(userId));
        } else {
            callMessageListApi(userId);
        }
    }

    void callMessageListApi(String userId) {
        mRequestReceivedMessage = RestApiCall.getApiService(AuthApi.class).getReceivedMessageList(userId);
        mRequestReceivedMessage.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseReceivedMessage>() {
            @Override
            public void onSuccess(ResponseReceivedMessage data) {
                mIResponseReceiver.onSuccess(data);
                cancelReceivedMessage();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelReceivedMessage();
            }
        }));
    }

    /**
     * Message List
     *
     * @param userId userid
     */
    public void getSentMessageList(String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callSentMessageListApi(userId));
        } else {
            callSentMessageListApi(userId);
        }
    }

    void callSentMessageListApi(String userId) {
        mRequestSentMessage = RestApiCall.getApiService(AuthApi.class).getSentMessageList(userId);
        mRequestSentMessage.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseReceivedMessage>() {
            @Override
            public void onSuccess(ResponseReceivedMessage data) {
                mIResponseReceiver.onSuccess(data);
                cancelReceivedMessage();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelReceivedMessage();
            }
        }));
    }


    /**
     * Cancel the request which is in progress
     */
    public void cancelReceivedMessage() {
        if (mRequestReceivedMessage != null && mRequestReceivedMessage.isExecuted()) {
            mRequestReceivedMessage.cancel();
        }
    }
    /**
     * Cancel the request which is in progress
     */
    public void cancelSendRequest() {
        if (mRequestSendMessage != null && mRequestSendMessage.isExecuted()) {
            mRequestSendMessage.cancel();
        }
    }
    /**
     * Cancel the request which is in progress
     */
    public void cancelProvidersRequest() {
        if (mRequestProviderList != null && mRequestProviderList.isExecuted()) {
            mRequestProviderList.cancel();
        }
    }
}
