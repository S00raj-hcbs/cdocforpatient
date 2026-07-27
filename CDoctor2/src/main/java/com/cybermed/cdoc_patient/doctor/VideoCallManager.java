package com.cybermed.cdoc_patient.doctor;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cybermed.cdoc_patient.login.ResponseAppId;
import com.cybermed.cdoc_patient.webapi.APIs.AuthApi;
import com.cybermed.cdoc_patient.webapi.AuthManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.ResponseWrapper;
import com.cybermed.cdoc_patient.webapi.RestApiCall;

import retrofit2.Call;

public class VideoCallManager {

    Context mContext;
    private Call<ResponseAppId> mRequestGetAppId;
    private Call<ResponseToken> mRequestGetToken;


    IResponseReceiver mIResponseReceiver;


    public VideoCallManager(IResponseReceiver iResponseReceiver, Context context) {
        mIResponseReceiver = iResponseReceiver;
        mContext=context;
    }


    public void getAppId() {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> getAppIdApi());
        } else {
            getAppIdApi();
        }
    }

    void getAppIdApi() {
        mRequestGetAppId = RestApiCall.getApiService(AuthApi.class).getAgoraAppId();
        mRequestGetAppId.enqueue(new ResponseWrapper(new IResponseReceiver<Object>() {
            @Override
            public void onSuccess(Object data) {
                mIResponseReceiver.onSuccess(data);
                cancelAppIdRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelAppIdRequest();
            }
        }));
    }


    public void getAppToken(RequestToken requestToken) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callAppToken(requestToken));
        } else {
            callAppToken(requestToken);
        }
    }

    void callAppToken(RequestToken requestToken) {
        mRequestGetToken = RestApiCall.getApiService(AuthApi.class).getAgoraToken(requestToken);
        mRequestGetToken.enqueue(new ResponseWrapper(new IResponseReceiver<Object>() {
            @Override
            public void onSuccess(Object data) {
                mIResponseReceiver.onSuccess(data);
                cancelTokenRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelTokenRequest();
            }
        }));
    }


    /**
     * Cancel the request which is in progress
     */
    public void cancelTokenRequest() {
        if (mRequestGetToken != null && mRequestGetToken.isExecuted()) {
            mRequestGetToken.cancel();
        }
    }
    /**
     * Cancel the request which is in progress
     */
    public void cancelAppIdRequest() {
        if (mRequestGetAppId != null && mRequestGetAppId.isExecuted()) {
            mRequestGetAppId.cancel();
        }
    }

}
