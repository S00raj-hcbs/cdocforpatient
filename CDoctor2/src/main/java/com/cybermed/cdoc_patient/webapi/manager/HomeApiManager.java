package com.cybermed.cdoc_patient.webapi.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.ReqSaveSWData;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ProviderLocationListModel;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ReqApptDateList;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResApptList;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResDocNextAvail;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResMakeAppt;
import com.cybermed.cdoc_patient.doctor.searchDoctor.RequestDoctorInfo;
import com.cybermed.cdoc_patient.doctor.searchDoctor.ResponseDocInfo;
import com.cybermed.cdoc_patient.main.chat.model.MessageModel;
import com.cybermed.cdoc_patient.me.vitalcheck.model.ReqSaveVitalData;
import com.cybermed.cdoc_patient.webapi.APIs.AuthApi;
import com.cybermed.cdoc_patient.webapi.APIs.PaymentApi;
import com.cybermed.cdoc_patient.webapi.AuthManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.ResponseWrapper;
import com.cybermed.cdoc_patient.webapi.RestApiCall;
import com.cybermed.cdoc_patient.webapi.model.request.DeleteSquareCard;
import com.cybermed.cdoc_patient.webapi.model.response.ResOrgLogo;
import com.cybermed.cdoc_patient.webapi.model.response.SquareCard;
import com.cybermed.cdoc_patient.webapi.model.response.TriageConfigResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class HomeApiManager {
    Context mContext;

    /**
     * Doctor search request
     */
    private Call<List<ResponseDocInfo>> mRequestDocInfo;
    /**
     * logout all request
     */
    private Call<Void> mRequestLogout;
    /**
     * get org logo request
     */
    private Call<ResOrgLogo> mRequestResOrg;
    /**
     * generic response handler
     */
    IResponseReceiver mIResponseReceiver;
    /**
     * save smart watch data request
     */
    private Call<Void> mRequestSWSet;
    /**
     * save vital data request
     */
    private Call<Void> mRequestVitalSet;
    /**
     * get smart watch data request
     */
    private Call<BaseResponseModel<List<ReqSaveSWData>>> mRequestSWGet;
    /**
     * doc detail request
     */
    private Call<BaseResponseModel<ResponseDocInfo>> mRequestDocDetail;
    /**
     * ProviderLocationList  request
     */
    private Call<List<ProviderLocationListModel>> mRequestProviderLocationList;
    /**
     * credit  card request
     */
    private Call<List<SquareCard>> mRequestCreditCard;
    /**
     * delete card  card request
     */
    private Call<Void> mRequestDeleteCreditCard;
    /**
     * doctor next availability request
     */
    private Call<BaseResponseModel<ResDocNextAvail>> mRequestDocNextAvail;
    /**
     * make appointment request
     */
    private Call<BaseResponseModel<ResMakeAppt>> mRequestMakeAppointment;
    /**
     * get appointment list
     */
    private Call<BaseResponseModel<ArrayList<ResApptList>>> mRequestGetAppointmentList;
    /**
     * get all Message list
     */
    private Call<List<MessageModel>> mRequestAllMessageAppointment;
    /**
     * triage configuration
     */
    private Call<TriageConfigResponse> mRequestTriage;


    public HomeApiManager(IResponseReceiver iResponseReceiver, Context mContext) {
        mIResponseReceiver = iResponseReceiver;
        this.mContext = mContext;
    }


    /**
     * search doctor
     *
     * @param doctorInfo doctor info request model
     */
    public void getSearchDoc(RequestDoctorInfo doctorInfo) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> getSearchDocApi(doctorInfo));
        } else {
            getSearchDocApi(doctorInfo);
        }
    }

    public void getSearchDocApi(RequestDoctorInfo doctorInfo) {
        mRequestDocInfo = RestApiCall.getApiService(AuthApi.class).getSearchDocList(doctorInfo);
        mRequestDocInfo.enqueue(new ResponseWrapper(new IResponseReceiver<List<ResponseDocInfo>>() {
            @Override
            public void onSuccess(List<ResponseDocInfo> data) {
                mIResponseReceiver.onSuccess(data);
                cancelSearchRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelSearchRequest();
            }
        }));
    }

    public void cancelSearchRequest() {
        if (mRequestDocInfo != null && mRequestDocInfo.isExecuted()) {
            mRequestDocInfo.cancel();
        }
    }

    /**
     * get organization logo
     *
     * @param orgCode org code
     */
    public void getOrgLogo(String orgCode) {
        mRequestResOrg = RestApiCall.createNonAuthService(AuthApi.class).getOrgLogo(orgCode);
        mRequestResOrg.enqueue(new ResponseWrapper(new IResponseReceiver<ResOrgLogo>() {
            @Override
            public void onSuccess(ResOrgLogo data) {
                mIResponseReceiver.onSuccess(data);
                cancelOrgRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelOrgRequest();
            }
        }));
    }

    public void cancelOrgRequest() {
        if (mRequestResOrg != null && mRequestResOrg.isExecuted()) {
            mRequestResOrg.cancel();
        }
    }


    /**
     * send vital data
     *
     * @param reqSaveVitalData smart watch data request
     */
    public void saveVitalData(ReqSaveVitalData reqSaveVitalData) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> saveVitalDataApi(reqSaveVitalData));
        } else {
            saveVitalDataApi(reqSaveVitalData);
        }
    }

    public void saveVitalDataApi(ReqSaveVitalData reqSaveVitalData) {
        mRequestVitalSet = RestApiCall.getApiService(AuthApi.class).saveVitalData(reqSaveVitalData);
        mRequestVitalSet.enqueue(new ResponseWrapper(new IResponseReceiver<Void>() {
            @Override
            public void onSuccess(Void data) {
                mIResponseReceiver.onSuccess(data);
                cancelVitalRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelVitalRequest();
            }
        }));
    }

    public void cancelVitalRequest() {
        if (mRequestVitalSet != null && mRequestVitalSet.isExecuted()) {
            mRequestVitalSet.cancel();
        }
    }

    /**
     * logout all request
     *
     * @param userid   user id
     * @param deviceId device id
     */
    public void logoutAll(String userid, String deviceId) {
        mRequestLogout = RestApiCall.getApiService(AuthApi.class).logoutAllDevice(userid, deviceId);
        mRequestLogout.enqueue(new ResponseWrapper(new IResponseReceiver<Void>() {
            @Override
            public void onSuccess(Void data) {
                mIResponseReceiver.onSuccess(data);
                cancelLogoutRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelLogoutRequest();
            }
        }));
    }

    public void cancelLogoutRequest() {
        if (mRequestLogout != null && mRequestLogout.isExecuted()) {
            mRequestLogout.cancel();
        }
    }

    /**
     * send smart watch data
     *
     * @param reqSaveSWData smart watch data request
     */
    public void saveSWData(ReqSaveSWData reqSaveSWData) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> saveSWDataApi(reqSaveSWData));
        } else {
            saveSWDataApi(reqSaveSWData);
        }
    }

    public void saveSWDataApi(ReqSaveSWData reqSaveSWData) {
        mRequestSWSet = RestApiCall.getApiService(AuthApi.class).saveSmartDeviceData(reqSaveSWData);
        mRequestSWSet.enqueue(new ResponseWrapper(new IResponseReceiver<Void>() {
            @Override
            public void onSuccess(Void data) {
                mIResponseReceiver.onSuccess(data);
                cancelSWRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelSWRequest();
            }
        }));
    }

    public void cancelSWRequest() {
        if (mRequestSWSet != null && mRequestSWSet.isExecuted()) {
            mRequestSWSet.cancel();
        }
    }

    /**
     * get smart watch data from server
     *
     * @param type   iot device type
     * @param mac    iot mac address
     * @param length data length
     */
    public void getSWData(String type, String mac, String length) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> getSWDataApi(type, mac, length));
        } else {
            getSWDataApi(type, mac, length);
        }
    }

    public void getSWDataApi(String type, String mac, String length) {
        mRequestSWGet = RestApiCall.getApiService(AuthApi.class).getSmartDeviceData(type, mac, length);
        mRequestSWGet.enqueue(new ResponseWrapper(new IResponseReceiver<BaseResponseModel<List<ReqSaveSWData>>>() {
            @Override
            public void onSuccess(BaseResponseModel<List<ReqSaveSWData>> data) {
                mIResponseReceiver.onSuccess(data);
                cancelGetSWRequest();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelGetSWRequest();
            }
        }));
    }

    public void cancelGetSWRequest() {
        if (mRequestSWGet != null && mRequestSWGet.isExecuted()) {
            mRequestSWGet.cancel();
        }
    }

    /**
     * get provider detail
     *
     * @param userId       user id
     * @param providerCode doctors code
     */
    public void getDoctorDetail(String userId, String providerCode) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> getProviderDetail(userId, providerCode));
        } else {
            getProviderDetail(userId, providerCode);
        }
    }

    public void getProviderDetail(String userId, String providerCode) {
        mRequestDocDetail = RestApiCall.getApiService(AuthApi.class).getProviderInfo(userId, providerCode);
        mRequestDocDetail.enqueue(new ResponseWrapper(new IResponseReceiver<BaseResponseModel<ResponseDocInfo>>() {
            @Override
            public void onSuccess(BaseResponseModel<ResponseDocInfo> data) {
                mIResponseReceiver.onSuccess(data);
                cancelGetDocDetail();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelGetDocDetail();
            }
        }));
    }
    public void cancelGetDocDetail() {
        if (mRequestDocDetail != null && mRequestDocDetail.isExecuted()) {
            mRequestDocDetail.cancel();
        }
    }



    /**
     * get provider Clinic list
     *
     * @param providerCode  providerCode
     * @param providerCode doctors code
     */
    public void getProviderClinic( String providerCode) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> getProviderClinicList( providerCode));
        } else {
            getProviderClinicList(providerCode);
        }
    }

    public void getProviderClinicList( String providerCode) {
        mRequestProviderLocationList = RestApiCall.getApiService(AuthApi.class).getProviderLocations(providerCode);
        mRequestProviderLocationList.enqueue(new ResponseWrapper(new IResponseReceiver<List<ProviderLocationListModel>>() {
            @Override
            public void onSuccess(List<ProviderLocationListModel> data) {
                mIResponseReceiver.onSuccess(data);
                cancelGetProviderClinicList();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelGetProviderClinicList();
            }
        }));
    }

    public void cancelGetProviderClinicList() {
        if (mRequestProviderLocationList != null && mRequestProviderLocationList.isExecuted()) {
            mRequestProviderLocationList.cancel();
        }
    }

    /**
     * credit card list
     *
     * @param userId user id
     */
    public void getCreditCardList(String userId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> getCreditCardListApi(userId));
        } else {
            getCreditCardListApi(userId);
        }
    }

    public void getCreditCardListApi(String userId) {
        mRequestCreditCard = RestApiCall.getApiService(PaymentApi.class).getSquareCards(userId);
        mRequestCreditCard.enqueue(new ResponseWrapper(new IResponseReceiver<List<SquareCard>>() {
            @Override
            public void onSuccess(List<SquareCard> data) {
                mIResponseReceiver.onSuccess(data);
                cancelCreditReq();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelCreditReq();
            }
        }));
    }

    public void cancelCreditReq() {
        if (mRequestCreditCard != null && mRequestCreditCard.isExecuted()) {
            mRequestCreditCard.cancel();
        }
    }

    /**
     * delete credit card
     *
     * @param deleteSquareCard user id and credit card id
     */
    public void deleteCard(DeleteSquareCard deleteSquareCard) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> deleteCreditCardApi(deleteSquareCard));
        } else {
            deleteCreditCardApi(deleteSquareCard);
        }
    }

    public void deleteCreditCardApi(DeleteSquareCard deleteSquareCard) {
        mRequestDeleteCreditCard = RestApiCall.getApiService(PaymentApi.class).deleteSquareCards(deleteSquareCard);
        mRequestDeleteCreditCard.enqueue(new ResponseWrapper(new IResponseReceiver<List<SquareCard>>() {
            @Override
            public void onSuccess(List<SquareCard> data) {
                mIResponseReceiver.onSuccess(data);
                cancelDeleteCardReq();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelDeleteCardReq();
            }
        }));
    }

    public void cancelDeleteCardReq() {
        if (mRequestDeleteCreditCard != null && mRequestDeleteCreditCard.isExecuted()) {
            mRequestDeleteCreditCard.cancel();
        }
    }


    /**
     * credit card list
     *
     * @param userId user id
     */
    public void getDocNextAvailableList(String userId, String providerId) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> getDocNextAvailableListCall(userId, providerId));
        } else {
            getDocNextAvailableListCall(userId, providerId);
        }
    }

    public void getDocNextAvailableListCall(String userId, String providerId) {
        mRequestDocNextAvail = RestApiCall.getApiService(AuthApi.class).getDocNextAvail(userId, providerId);
        mRequestDocNextAvail.enqueue(new ResponseWrapper<>(new IResponseReceiver<BaseResponseModel<ResDocNextAvail>>() {
            @Override
            public void onSuccess(BaseResponseModel<ResDocNextAvail> data) {
                mIResponseReceiver.onSuccess(data);
                cancelGetDocNextAvailableListCall();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelGetDocNextAvailableListCall();
            }
        }));
    }

    public void cancelGetDocNextAvailableListCall() {
        if (mRequestDocNextAvail != null && mRequestDocNextAvail.isExecuted()) {
            mRequestDocNextAvail.cancel();
        }
    }

    /**
     * make video call appointment
     *
     * @param userId user id
     */
    public void makeVideoCallAppointment(String userId, ResMakeAppt resMakeAppt) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> makeVideoCallAppointmentCall(userId, resMakeAppt));
        } else {
            makeVideoCallAppointmentCall(userId, resMakeAppt);
        }
    }

    public void makeVideoCallAppointmentCall(String userId, ResMakeAppt resMakeAppt) {
        mRequestMakeAppointment = RestApiCall.getApiService(AuthApi.class).makeVideoAppt(userId, resMakeAppt);
        mRequestMakeAppointment.enqueue(new ResponseWrapper(new IResponseReceiver<BaseResponseModel<ResMakeAppt>>() {
            @Override
            public void onSuccess(BaseResponseModel<ResMakeAppt> data) {
                mIResponseReceiver.onSuccess(data);
                cancelMakeClinicAppointmentCall();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelMakeClinicAppointmentCall();
            }
        }));
    }

    /**
     * make clinic appointment
     *
     * @param userId user id
     */
    public void makeClinicAppointment(String userId, ResMakeAppt resMakeAppt) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> makeClinicAppointmentCall(userId, resMakeAppt));
        } else {
            makeClinicAppointmentCall(userId, resMakeAppt);
        }
    }

    public void makeClinicAppointmentCall(String userId, ResMakeAppt resMakeAppt) {
        mRequestMakeAppointment = RestApiCall.getApiService(AuthApi.class).makeClinicAppt(userId, resMakeAppt);
        mRequestMakeAppointment.enqueue(new ResponseWrapper(new IResponseReceiver<BaseResponseModel<ResMakeAppt>>() {
            @Override
            public void onSuccess(BaseResponseModel<ResMakeAppt> data) {
                mIResponseReceiver.onSuccess(data);
                cancelMakeClinicAppointmentCall();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelMakeClinicAppointmentCall();
            }
        }));
    }

    public void cancelMakeClinicAppointmentCall() {
        if (mRequestMakeAppointment != null && mRequestMakeAppointment.isExecuted()) {
            mRequestMakeAppointment.cancel();
        }
    }


    /**
     * get Appointment list
     *
     * @param reqAppointmentList Appointment list model
     */
    public void getAppointmentList(ReqApptDateList reqAppointmentList) {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(mContext, () -> getAppointmentListCall(reqAppointmentList));
        } else {
            getAppointmentListCall(reqAppointmentList);
        }
    }

    public void getAppointmentListCall(ReqApptDateList reqAppointmentList) {
        mRequestGetAppointmentList = RestApiCall.getApiService(AuthApi.class).getApptListByDate(reqAppointmentList.getOrgCode(),
                reqAppointmentList.getUserId(), reqAppointmentList.getDateToSearch(), String.valueOf(reqAppointmentList.getPageNumber()),
                String.valueOf(reqAppointmentList.getCountPerPage()));
        mRequestGetAppointmentList.enqueue(new ResponseWrapper(new IResponseReceiver<BaseResponseModel<ArrayList<ResApptList>>>() {
            @Override
            public void onSuccess(BaseResponseModel<ArrayList<ResApptList>> data) {
                mIResponseReceiver.onSuccess(data);
                cancelGetAppointmentListCall();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelGetAppointmentListCall();
            }
        }));
    }

    public void cancelGetAppointmentListCall() {
        if (mRequestGetAppointmentList != null && mRequestGetAppointmentList.isExecuted()) {
            mRequestGetAppointmentList.cancel();
        }
    }

    public void getTriageConfig(String orgCode) {
        mRequestTriage = RestApiCall.getApiService(AuthApi.class).getTriageConfig(orgCode);
        mRequestTriage.enqueue(new ResponseWrapper(new IResponseReceiver<TriageConfigResponse>() {
            @Override
            public void onSuccess(TriageConfigResponse data) {
                mIResponseReceiver.onSuccess(data);
                cancelTriageCall();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelTriageCall();
            }
        }));
    }

    public void cancelTriageCall() {
        if (mRequestTriage != null && mRequestTriage.isExecuted()) {
            mRequestTriage.cancel();
        }
    }

    public void getAllMessagesAppointment(String orgCode,String patientId,String providerId,String appId) {
        mRequestAllMessageAppointment = RestApiCall.getApiService(AuthApi.class).getAllMessagesAppointment(orgCode,patientId,providerId,appId);
        mRequestAllMessageAppointment.enqueue(new ResponseWrapper(new IResponseReceiver<List<MessageModel>>() {
            @Override
            public void onSuccess(List<MessageModel> data) {
                mIResponseReceiver.onSuccess(data);
                cancelAllMessageAppointment();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                mIResponseReceiver.onFailure(errorResponse);
                cancelAllMessageAppointment();
            }
        }));
    }

    public void cancelAllMessageAppointment() {
        if (mRequestAllMessageAppointment != null && mRequestAllMessageAppointment.isExecuted()) {
            mRequestAllMessageAppointment.cancel();
        }
    }
}
