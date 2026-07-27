package com.cybermed.cdoc_patient.webapi;

import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ReqApptList;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResApptList;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RxJavaApi {
    /**
     *
     * @param reqApptList appt list request
     * @return observable
     */
    @POST("portal/appointment/GetAllAppointmentsByUserID")
    Observable<BaseResponseModel<ArrayList<ResApptList>>> getApptList(@Body ReqApptList reqApptList);
}
