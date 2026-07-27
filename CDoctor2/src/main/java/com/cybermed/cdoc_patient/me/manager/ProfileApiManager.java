package com.cybermed.cdoc_patient.me.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.me.allergies.model.ResponseAllergies;
import com.cybermed.cdoc_patient.me.calllog.model.ReqCallLog;
import com.cybermed.cdoc_patient.me.calllog.model.ResCallLog;
import com.cybermed.cdoc_patient.me.document.model.ResponseDocument;
import com.cybermed.cdoc_patient.me.immunizations.Model.ResponseImmunization;
import com.cybermed.cdoc_patient.me.labereport.model.ResponseLabReport;
import com.cybermed.cdoc_patient.me.medication.model.MedicationData;
import com.cybermed.cdoc_patient.me.referral.model.ResponseReferral;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ResponseVital;
import com.cybermed.cdoc_patient.webapi.APIs.AuthApi;
import com.cybermed.cdoc_patient.webapi.AuthManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.ResponseWrapper;
import com.cybermed.cdoc_patient.webapi.RestApiCall;
import com.cybermed.cdoc_patient.webapi.manager.ResponseGetEnableLog;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class ProfileApiManager {

    Context mContext;
    /**
     * medication list reuqest
     */
    private Call<BaseResponseModel<List<MedicationData>>> mRequestMedication;
    /**
     * lab report request
     */
    private Call<ResponseLabReport> mRequestLab;
    /**
     * Clinic request
     */
    private Call<ResponseVital> mClinicvital;

    /**
     * Clinic request
     */
    private Call<JsonObject> mDevicevital;
    /**
     * Immunization request
     */
    private Call<ResponseImmunization> mRequestImmunization;
    /**
     * Allergies request
     */
    private Call<ResponseAllergies> mRequestAllergies;
    /**
     * referal request
     */
    private Call<ResponseReferral> mRequestReferral;

    /**
     * document request
     */
    private Call<ResponseDocument> mRequestDocument;

    IResponseReceiver mIResponseReceiver;

    /**
     * request to show/hide enable log button for agora call
     */
    private Call<ResponseGetEnableLog> mRequestEnableLog;

    /**
     * request to get call logs
     */
    private Call<BaseResponseModel<ArrayList<ResCallLog>>> mRequestCallLog;

    public ProfileApiManager(IResponseReceiver iResponseReceiver, Context context) {
        mIResponseReceiver = iResponseReceiver;
        mContext = context;
    }

    /**
     * Get ImmunizationList
     *
     * @param serviceCode service code
     * @param userId      userid
     */
    public void getImmunizationList(String serviceCode, String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callImmunizationApi(serviceCode, userId));
        } else {
            callImmunizationApi(serviceCode, userId);
        }
    }


    void callImmunizationApi(String serviceCode, String userId) {
        mRequestImmunization = RestApiCall.getApiService(AuthApi.class).getImmunizationList(serviceCode, userId);
        mRequestImmunization.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseImmunization>() {
            @Override
            public void onSuccess(ResponseImmunization data) {
                cancelMedicationRequest();
                mIResponseReceiver.onSuccess(data);
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                cancelMedicationRequest();
                mIResponseReceiver.onFailure(errorResponse);
            }
        }));
    }

    /**
     * Get AllergiesList
     *
     * @param serviceCode service code
     * @param userId      userid
     */
    public void getAllergiesList(String serviceCode, String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callAllergiesApi(serviceCode, userId));
        } else {
            callAllergiesApi(serviceCode, userId);
        }
    }


    void callAllergiesApi(String serviceCode, String userId) {
        mRequestAllergies = RestApiCall.getApiService(AuthApi.class).getAllergiesList(serviceCode, userId);
        mRequestAllergies.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseAllergies>() {
            @Override
            public void onSuccess(ResponseAllergies data) {
                cancelMedicationRequest();
                mIResponseReceiver.onSuccess(data);
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                cancelMedicationRequest();
                mIResponseReceiver.onFailure(errorResponse);
            }
        }));
    }
    /**
     * Get MedicationList
     *
     * @param serviceCode service code
     * @param userId      userid
     */
    public void getMedicationList(String serviceCode, String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callMedicationApi(serviceCode, userId));
        } else {
            callMedicationApi(serviceCode, userId);
        }
    }

    void callMedicationApi(String serviceCode, String userId) {
        mRequestMedication = RestApiCall.getApiService(AuthApi.class).getMedicationList(serviceCode, userId);
        mRequestMedication.enqueue(new ResponseWrapper(new IResponseReceiver<BaseResponseModel<List<MedicationData>>>() {
            @Override
            public void onSuccess(BaseResponseModel<List<MedicationData>> data) {
                cancelMedicationRequest();
                mIResponseReceiver.onSuccess(data);

            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                cancelMedicationRequest();
                mIResponseReceiver.onFailure(errorResponse);

            }
        }));
    }

    /**
     * Get Lab Report List
     *
     * @param userId      userid
     */
    public void getClinicVitalList(String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callClinicVitalApi( userId));
        } else {
            callClinicVitalApi(userId);
        }
    }

    void callClinicVitalApi( String userId) {
        mClinicvital = RestApiCall.getApiService(AuthApi.class).getClinicVitalList(userId);
        mClinicvital.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseVital>() {
            @Override
            public void onSuccess(ResponseVital data) {
                cancelLabRequest();
                mIResponseReceiver.onSuccess(data);

            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                cancelLabRequest();
                mIResponseReceiver.onFailure(errorResponse);

            }
        }));
    }

    /**
     * Get Lab Report List
     *
     * @param userId      userid
     */
    public void getDeviceVitalList(String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callDeviceVitalApi( userId));
        } else {
            callDeviceVitalApi(userId);
        }
    }

    void callDeviceVitalApi( String userId) {
        mDevicevital = RestApiCall.getApiService(AuthApi.class).getDeviceVitalList(userId);
        mDevicevital.enqueue(new ResponseWrapper(new IResponseReceiver<JsonObject>() {
            @Override
            public void onSuccess(JsonObject data) {
                cancelLabRequest();
                mIResponseReceiver.onSuccess(data);

            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                cancelLabRequest();
                mIResponseReceiver.onFailure(errorResponse);

            }
        }));
    }

    /**
     * Get Lab Report List
     *
     * @param serviceCode service code
     * @param userId      userid
     */
    public void getLabReportList(String serviceCode, String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callLabReportApi(serviceCode, userId));
        } else {
            callLabReportApi(serviceCode, userId);
        }
    }

    void callLabReportApi(String serviceCode, String userId) {
        mRequestLab = RestApiCall.getApiService(AuthApi.class).getLabReportList(serviceCode, userId);
        mRequestLab.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseLabReport>() {
            @Override
            public void onSuccess(ResponseLabReport data) {
                cancelLabRequest();
                mIResponseReceiver.onSuccess(data);

            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                cancelLabRequest();
                mIResponseReceiver.onFailure(errorResponse);

            }
        }));
    }

    /**
     * Referral List
     *
     * @param userId userid
     */
    public void getReferralList(String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callReferralApi(userId));
        } else {
            callReferralApi(userId);
        }
    }

    void callReferralApi(String userId) {
        mRequestReferral = RestApiCall.getApiService(AuthApi.class).getRefferalList(userId);
        mRequestReferral.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseReferral>() {
            @Override
            public void onSuccess(ResponseReferral data) {
                mIResponseReceiver.onSuccess(data);
                cancelReferalRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelReferalRequest();
            }
        }));
    }

    /**
     * Document List
     *
     * @param userId userid
     */
    public void getDocumentList(String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callDocumentApi(userId));
        } else {
            callDocumentApi(userId);
        }
    }

    void callDocumentApi(String userId) {
        mRequestDocument = RestApiCall.getApiService(AuthApi.class).getDocumentList(userId);
        mRequestDocument.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseDocument>() {
            @Override
            public void onSuccess(ResponseDocument data) {
                mIResponseReceiver.onSuccess(data);
                cancelReferalRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelReferalRequest();
            }
        }));
    }

    /**
     * Cancel the request which is in progress
     */
    public void cancelReferalRequest() {
        if (mRequestReferral != null && mRequestReferral.isExecuted()) {
            mRequestReferral.cancel();
        }
    }

    /**
     * Cancel the request which is in progress
     */
    public void cancelLabRequest() {
        if (mRequestLab != null && mRequestLab.isExecuted()) {
            mRequestLab.cancel();
        }
    }

    /**
     * Cancel the request which is in progress
     */
    public void cancelMedicationRequest() {
        if (mRequestMedication != null && mRequestMedication.isExecuted()) {
            mRequestMedication.cancel();
        }
    }


    /**
     * show/hide enable log button for agora call
     *
     * @param email user email
     */
    public void getEnableLog(String email) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> callGetEnableLog(email));
        } else {
            callGetEnableLog(email);
        }
    }

    void callGetEnableLog(String email) {
        mRequestEnableLog = RestApiCall.getApiService(AuthApi.class).getLoggingMode(email);
        mRequestEnableLog.enqueue(new ResponseWrapper(new IResponseReceiver<ResponseGetEnableLog>() {
            @Override
            public void onSuccess(ResponseGetEnableLog data) {
                mIResponseReceiver.onSuccess(data);
                cancelEnableRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelEnableRequest();
            }
        }));
    }

    public void cancelEnableRequest() {
        if (mRequestEnableLog != null && mRequestEnableLog.isExecuted()) {
            mRequestEnableLog.cancel();
        }
    }

    /**
     * get call log
     *
     * @param reqCallLog call log model
     */
    public void getCallLog(ReqCallLog reqCallLog) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> getCallLogApi(reqCallLog));
        } else {
            getCallLogApi(reqCallLog);
        }
    }

    void getCallLogApi(ReqCallLog reqCallLog) {
        mRequestCallLog = RestApiCall.getApiService(AuthApi.class).getCallLogList(reqCallLog);
        mRequestCallLog.enqueue(new ResponseWrapper(new IResponseReceiver<BaseResponseModel<ArrayList<ResCallLog>>>() {
            @Override
            public void onSuccess(BaseResponseModel<ArrayList<ResCallLog>> data) {
                mIResponseReceiver.onSuccess(data);
                cancelCallLogRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelCallLogRequest();
            }
        }));
    }

    public void cancelCallLogRequest() {
        if (mRequestEnableLog != null && mRequestEnableLog.isExecuted()) {
            mRequestEnableLog.cancel();
        }
    }


}
