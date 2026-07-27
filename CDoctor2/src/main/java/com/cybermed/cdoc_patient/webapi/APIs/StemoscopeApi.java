package com.cybermed.cdoc_patient.webapi.APIs;

import com.cybermed.cdoc_patient.webapi.model.response.StemoscopeResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface StemoscopeApi {

    @Multipart
    @POST ("/io/stemoscope/upload_reading_data")
    Call<StemoscopeResponse> uploadReading(@Part("QR_code")RequestBody qrCode,
                                           @Part("time")RequestBody timestamp,
                                           @Part MultipartBody.Part audio_file,
                                           @Part("stemo_mode") RequestBody stemoMode,
                                           @Part MultipartBody.Part stemoLocation);
}
