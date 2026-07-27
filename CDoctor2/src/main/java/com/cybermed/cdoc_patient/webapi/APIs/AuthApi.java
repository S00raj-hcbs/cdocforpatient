package com.cybermed.cdoc_patient.webapi.APIs;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.ReqSaveSWData;
import com.cybermed.cdoc_patient.doctor.RequestToken;
import com.cybermed.cdoc_patient.doctor.ResponseToken;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ProviderLocationListModel;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ReqApptList;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResApptList;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResDocNextAvail;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResMakeAppt;
import com.cybermed.cdoc_patient.doctor.searchDoctor.RequestDoctorInfo;
import com.cybermed.cdoc_patient.doctor.searchDoctor.ResponseDocInfo;
import com.cybermed.cdoc_patient.login.ResponseAppId;
import com.cybermed.cdoc_patient.main.chat.model.MessageModel;
import com.cybermed.cdoc_patient.me.allergies.model.ResponseAllergies;
import com.cybermed.cdoc_patient.me.calllog.model.ReqCallLog;
import com.cybermed.cdoc_patient.me.calllog.model.ResCallLog;
import com.cybermed.cdoc_patient.me.document.model.ResponseDocument;
import com.cybermed.cdoc_patient.me.immunizations.Model.ResponseImmunization;
import com.cybermed.cdoc_patient.me.labereport.model.ResponseLabReport;
import com.cybermed.cdoc_patient.me.medication.model.MedicationData;
import com.cybermed.cdoc_patient.me.referral.model.ResponsePdf;
import com.cybermed.cdoc_patient.me.referral.model.ResponseReferral;
import com.cybermed.cdoc_patient.me.securemessages.model.RequestSendMessage;
import com.cybermed.cdoc_patient.me.securemessages.model.ResponseProvidersList;
import com.cybermed.cdoc_patient.me.securemessages.model.ResponseReceivedMessage;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ReqSaveVitalData;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ResponseVital;
import com.cybermed.cdoc_patient.webapi.manager.ResponseGetEnableLog;
import com.cybermed.cdoc_patient.webapi.model.response.ApiToken;
import com.cybermed.cdoc_patient.webapi.model.response.ResOrgLogo;
import com.cybermed.cdoc_patient.webapi.model.response.TriageConfigResponse;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

    @FormUrlEncoded
    @POST("Auth/token")
    Call<ApiToken> getToken(@Field("grant_type") String grantType, @Field("app_id") String appId, @Field("api_key") String apiKey);

    /**
     * @param serviceCode service code
     * @param token       auth token
     * @return request
     */
    @GET("hub/patient/immunizations")
    Call<ResponseImmunization> getImmunizationList(@Query("org_code") String serviceCode, @Query("patient_id") String token);

    @GET("portal/document/GetCCDADocumentByUserID")
    Call<ResponseImmunization> getImmunizationList2(@Query("user_id") String token);

    /**
     * @param serviceCode service code
     * @param token       auth token
     * @return request
     */
    @GET("hub/patient/allergis")
    Call<ResponseAllergies> getAllergiesList(@Query("org_code") String serviceCode, @Query("patient_id") String token);
    /**
     * @param serviceCode service code
     * @param token       auth token
     * @return request
     */
    @GET("hub/patient/patient_medication")
    Call<BaseResponseModel<List<MedicationData>>> getMedicationList(@Query("org_code") String serviceCode, @Query("patient_id") String token);

    /**
     * @param serviceCode service code
     * @param token       auth token
     * @return request
     */
    @GET("hub/patient/lab_reports")
    Call<ResponseLabReport> getLabReportList(@Query("org_code") String serviceCode, @Query("patient_id") String token);
    /**
     * @param token       auth token
     * @return request
     */
    @GET("hub/patient/ehr_vital_data")
    Call<ResponseVital> getClinicVitalList(@Query("portal_user_id") String token);
    /**
     * @param token       auth token
     * @return request
     */
    @GET("hub/patient/device_vital_data")
    Call<JsonObject> getDeviceVitalList(@Query("portal_user_id") String token);
    /**
     * @param token auth token
     * @return request
     */
    @GET("hub/patient/referrals")
    Call<ResponseReferral> getRefferalList(@Query("patient_id") String token);

    /**
     * @param token auth token
     * @return request
     */
    @GET("portal/document/GetEHRDocumentByUserID")
    Call<ResponseDocument> getDocumentList(@Query("user_id") String token);

    /**
     * @param reportId id
     * @return request
     */
    @GET("hub/patient/lab_report_details")
    Call<ResponsePdf> getPdf(@Query("report_id") String reportId);

    /**
     * @param userId user id
     * @return request
     */
    @GET("hub/patient/provider_list")
    Call<ResponseProvidersList> getProviderList(@Query("patient_id") String userId);

    /**
     * @param requestSendMessage send message request
     * @return request
     */
    @POST("hub/patient/send_message")
    Call<ResponseBody> sendMessage(@Body RequestSendMessage requestSendMessage);

    /**
     * @param userid user id
     * @return received message
     */
    /*@GET("hub/patient/received_message_v2")
    Call<ResponseReceivedMessage> getReceivedMessageList(@Query("patient_id") String userid);*/
    @GET("hub/patient/received_message_v3")
    Call<ResponseReceivedMessage> getReceivedMessageList(@Query("patient_id") String userid);

    /**
     * @param userid user id
     * @return sent message
     */

    @GET("hub/patient/sent_message")
    Call<ResponseReceivedMessage> getSentMessageList(@Query("patient_id") String userid);

    /**
     * @param userid user id
     * @return request
     */
    @GET("hub/Patient/getLoggingMode")
    Call<ResponseGetEnableLog> getLoggingMode(@Query("patient_id") String userid);

    /**
     * @return agora appd id request
     */
    @GET("hub/Agora/getAppID?appId_name=appID_Live")
    Call<ResponseAppId> getAgoraAppId();

    /**
     * @param requestSendMessage send message
     * @return token request
     */
    @POST("hub/Agora/getToken")
    Call<ResponseToken> getAgoraToken(@Body RequestToken requestSendMessage);

    /**
     * @param requestDoctorInfo model requestDoctorInfo
     * @return call request
     */
    @POST("hub/patient/search_provider")
    Call<List<ResponseDocInfo>> getSearchDocList(@Body RequestDoctorInfo requestDoctorInfo);

    /**
     * @param orgCode orgcode
     * @return requst
     */
    @GET("hub/patient/get_org_logo")
    Call<ResOrgLogo> getOrgLogo(@Query("org_code") String orgCode);

    /**
     * @param userId   user id
     * @param deviceId device id
     * @return call request
     */
    @FormUrlEncoded
    @POST("hub/patient/logout_all_deivces")
    Call<Void> logoutAllDevice(@Field("user_id") String userId, @Field("device_id") String deviceId);

    /**
     * @param type   tyoe of device
     * @param mac    mac address
     * @param length size of list
     * @return requst
     */
    @GET("hub/patient/vital_data")
    Call<BaseResponseModel<List<ReqSaveSWData>>> getSmartDeviceData(@Query("type") String type, @Query("mac") String mac,
                                                                    @Query("length") String length);

    /**
     * @param saveSWData smart watch data request
     * @return request
     */
    @POST("hub/patient/vital_data")
    Call<Void> saveSmartDeviceData(@Body ReqSaveSWData saveSWData);


    /**
     * @param saveVitalData vital data request
     * @return request
     */
    @POST("hub/patient/device_vital_data")
    Call<Void> saveVitalData(@Body ReqSaveVitalData saveVitalData);

    /**
     * @param userId       user id
     * @param providerCode provider id
     * @return provider info
     */
    @GET("hub/provider/ProviderInfo")
    Call<BaseResponseModel<ResponseDocInfo>> getProviderInfo(@Query("user_id") String userId, @Query("provider_code") String providerCode);

    /**
     * @param userId     user id
     * @param providerId provider id
     * @return next avialblity doctor
     */
    @GET("portal/appointment/GetProvidersNextAvailabilityByUserID")
    Call<BaseResponseModel<ResDocNextAvail>> getDocNextAvail(@Query("user_id") String userId, @Query("provider_code") String providerId);

    /**
     * @param userId      userid
     * @param resMakeAppt request model for make appt
     * @return book new clinic appt
     */
    @POST("portal/appointment/RequestAppointmentByUserID")
    Call<BaseResponseModel<ResMakeAppt>> makeClinicAppt(@Query("user_id") String userId, @Body ResMakeAppt resMakeAppt);

    /**
     * @param userId      userid
     * @param resMakeAppt request model for make appt
     * @return book new video appt
     */
    @POST("portal/appointment/MakeAppointmentByUserID")
    Call<BaseResponseModel<ResMakeAppt>> makeVideoAppt(@Query("user_id") String userId, @Body ResMakeAppt resMakeAppt);

    /**
     * @param reqApptList request appt list
     * @return request
     */
    @POST("portal/appointment/GetAllAppointmentsByUserID")
    Call<BaseResponseModel<ArrayList<ResApptList>>> getApptList(@Body ReqApptList reqApptList);

    /**
     * @param reqApptList request appt list
     * @return request
     */
    @POST("hub/patient/GetCallLog")
    Call<BaseResponseModel<ArrayList<ResCallLog>>> getCallLogList(@Body ReqCallLog reqApptList);

    /**
     *
     */
    @POST("io/iHealth/VitalInsert")
    Call<Void> sendVitalData(@Query("type") String type, @Query("value") String value,
                             @Query("timestamp") String timestamp, @Query("device_mac_address") String deviceMacAdd,
                             @Query("hub_mac_address") String hubMacAddress, @Query("measureTimeFormatted") String measureTimeFormat,
                             @Query("measureTime") String measureTime, @Query("bpm") String bpm);

    /**
     * @param orgCode orgcode
     * @return book new video appt
     */
    @GET("/portal/appointment/GetTriageConfiguration")
    Call<TriageConfigResponse> getTriageConfig(@Query("org_code") String orgCode);

    /**
     * @param orgCode orgcode
     * @return Provider Location
     */
    @GET("/portal/appointment/GetPhysicalLocations")
    Call<List<ProviderLocationListModel>> getProviderLocations(@Query("org_code") String orgCode);


    /**
     * @param orgCode request appt list
     * @return request
     */
    // org_code=cdoc&user_id=testpooja%40cdoc.com&appt_date_to_search=12%2F2%2F2021&page_num=0&max_appts_to_return=10
    @POST("portal/appointment/GetAllAppointmentsByDate")
    Call<BaseResponseModel<ArrayList<ResApptList>>> getApptListByDate(@Query("org_code") String orgCode,
                                                                      @Query("user_id") String userId,
                                                                      @Query("appt_date_to_search") String dateToSearch,
                                                                      @Query("page_num") String pageNum,
                                                                      @Query("max_appts_to_return") String pageSize);

    @GET("portal/appointment/GetAllChatMessagesByDate")
    Call<List<MessageModel>> getAllMessagesAppointment(@Query("org_code") String orgCode
            , @Query("patient_id") String patient_id, @Query("provider_id") String provider_id
            , @Query("appt_id") String apptId);
}
