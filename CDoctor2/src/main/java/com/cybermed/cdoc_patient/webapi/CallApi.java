package com.cybermed.cdoc_patient.webapi;


import android.content.Context;
import android.util.Log;

import com.cybermed.cdoc_patient.webapi.APIs.StemoscopeApi;
import com.cybermed.cdoc_patient.webapi.model.response.StemoscopeResponse;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallApi {
    static IApiCallBack mIApiCallBack;

    private final static String TAG = "CallStemoscopeApi";

    public static void sendStemoDataAsync(Context context, String QRCode, String timestamp, File wavFile, String stemoMode, File pngFile) {
        StemoscopeApi stemoscopeApi = RestApiCall.createNonAuthService(StemoscopeApi.class);
        RequestBody qrCodeBody = RequestBody.create(MediaType.parse("multipart/form-data"), QRCode);
        RequestBody tsBody = RequestBody.create(MediaType.parse("multipart/form-data"), timestamp);
        RequestBody wavFileBody = RequestBody.create(MediaType.parse("multipart/form-data"), wavFile);
        MultipartBody.Part wavFilePart = MultipartBody.Part.createFormData("audio_file", wavFile.getName(), wavFileBody);
        RequestBody stemoModeBody = RequestBody.create(MediaType.parse("multipart/form-data"), stemoMode);
        RequestBody pngFileBody = RequestBody.create(MediaType.parse("multipart/form-data"), pngFile);
        MultipartBody.Part pngFilePart = MultipartBody.Part.createFormData("stemo_location", pngFile.getName(), pngFileBody);
        Call<StemoscopeResponse> sendCall = stemoscopeApi.uploadReading(qrCodeBody, tsBody, wavFilePart, stemoModeBody, pngFilePart);
        sendCall.enqueue(new Callback<StemoscopeResponse>() {
            @Override
            public void onResponse(Call<StemoscopeResponse> call, Response<StemoscopeResponse> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 500) {
                        Log.d(TAG, response.toString());
                        mIApiCallBack.failure();
                    } else {
                        try {
                            StemoscopeResponse error = null;
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                error = new Gson().fromJson(errorBody, StemoscopeResponse.class);
                            }
                            Log.d(TAG, error.getUploaded());
                            mIApiCallBack.failure();
                        } catch (IOException e) {
                            mIApiCallBack.failure();
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (context != null) {
                        mIApiCallBack.success();
                    }
                }
            }

            @Override
            public void onFailure(Call<StemoscopeResponse> call, Throwable t) {
                mIApiCallBack.failure();
            }
        });
    }



    public interface IApiCallBack{
        void success();
        void failure();
    }

    public static void setListner(IApiCallBack iApiCallBack){
        mIApiCallBack=iApiCallBack;
    }
}
