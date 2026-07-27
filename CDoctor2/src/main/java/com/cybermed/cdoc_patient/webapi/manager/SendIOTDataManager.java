package com.cybermed.cdoc_patient.webapi.manager;

import androidx.annotation.NonNull;

import com.cybermed.cdoc_patient.webapi.APIs.AuthApi;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.ResponseWrapper;
import com.cybermed.cdoc_patient.webapi.RestApiCall;

import retrofit2.Call;

public class SendIOTDataManager {
    private Call<Void> mRequestSendIOTData;
    IResponseReceiver mIResponseReceiver;

    public SendIOTDataManager(IResponseReceiver iResponseReceiver) {
        mIResponseReceiver = iResponseReceiver;
    }

    public void callGetEnableLog(String type, String value,String timestamp,String deviceMacAddres,
                          String hubMac,String measurementTimeFormat,String measureTime, String bpm) {
        mRequestSendIOTData = RestApiCall.getApiService(AuthApi.class).sendVitalData(type,value,timestamp,
                deviceMacAddres,hubMac, measurementTimeFormat,measureTime,bpm);
        mRequestSendIOTData.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseGetEnableLog>() {
            @Override
            public void onSuccess(ResponseGetEnableLog data) {
                mIResponseReceiver.onSuccess(data);
                cancelRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelRequest();
            }
        }));
    }
    /**
     * Cancel the request which is in progress
     */
    public void cancelRequest() {
        if (mRequestSendIOTData != null && mRequestSendIOTData.isExecuted()) {
            mRequestSendIOTData.cancel();
        }
    }
}
