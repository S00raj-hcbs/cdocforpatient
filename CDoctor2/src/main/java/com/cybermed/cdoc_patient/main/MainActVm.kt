package com.cybermed.cdoc_patient.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cdfortis.datainterface.soap.WebService
import com.cybermed.cdoc_patient.common.BaseFragment
import com.cybermed.cdoc_patient.common.BaseFragment.FUTUREAPPT
import com.cybermed.cdoc_patient.common.CDoctor2Application
import com.cybermed.cdoc_patient.common.base.BaseViewModel
import com.cybermed.cdoc_patient.common.base.SingleLiveEvent
import com.cybermed.cdoc_patient.doctor.docDetail.PropertyAwareLiveData
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel
import com.cybermed.cdoc_patient.doctor.docDetail.model.ReqApptList
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResApptList
import com.cybermed.cdoc_patient.login.viewmodel.BaseResponse
import com.cybermed.cdoc_patient.webapi.AuthManager
import com.cybermed.cdoc_patient.webapi.RestApiCall
import com.cybermed.cdoc_patient.webapi.RxJavaApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActVm(application: Application) : BaseViewModel(application) {
    val context = application.applicationContext
    private val errorLiveData = MutableLiveData<Boolean>()

    /**
     * api response
     */
    private val liveDataApiResponse = SingleLiveEvent<MainActAction<Any>>()

    /**
     * past and future appointment list
     */
    val apptList = SingleLiveEvent<ArrayList<BaseResponseModel<ArrayList<ResApptList>>>>()
    val apptModelLiveData = PropertyAwareLiveData<ResApptList>()


    /**
     * cdoc support
     */
    private val cdocSupport = SingleLiveEvent<Any>()

    init {
        apptModelLiveData.value = ResApptList()
        errorLiveData.value = false
    }

    fun getCdocSupport(): SingleLiveEvent<Any> {
        return cdocSupport
    }

    fun getPatientApptList(): SingleLiveEvent<ArrayList<BaseResponseModel<ArrayList<ResApptList>>>> {
        return apptList
    }


    fun isErrorOccurred(): LiveData<Boolean> {
        return errorLiveData;
    }

    fun getApiResponse(): SingleLiveEvent<MainActAction<Any>> {
        return liveDataApiResponse
    }

    /**
     * active guest count for call
     */
    fun getActiveGuestsCount(roomNumber: String?, guestID: String?) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                liveDataApiResponse.postValue(MainActAction(
                        BaseResponse(WebService.getInstance().GetActiveGuestsCount(roomNumber, guestID)), MainActEvent.ACTIVE_GUEST_COUNT))

            }
        } catch (e: Exception) {

        }
    }

    /**
     * user info
     */
    fun getUserInfoAsyncTask(userId: String?) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                liveDataApiResponse.postValue(MainActAction(
                        WebService.getInstance().get_PatientDemographic_Android(userId), MainActEvent.GET_USER_INFO))

            }
        } catch (e: Exception) {

        }
    }

    /**
     * verify patient login
     */
    fun getUserLoginTask(email: String?, pwd: String?, oneSignalId: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                liveDataApiResponse.postValue(MainActAction(
                        BaseResponse(WebService.getInstance().verifyPatientLogin_Android(email, pwd, oneSignalId)), MainActEvent.LOGIN_INFO))

            }
        } catch (e: Exception) {

        }
    }

    /**
     * reconnect dropped video call
     */
    fun reconnectDroppedVideo() {
        val preferences: SharedPreferences = context.getSharedPreferences("VIDEOSHAREPREF", Context.MODE_PRIVATE)
        val roomNumber = preferences.getString("ROOM_NUMBER", "")
        val guestId = preferences.getString("ROOM_GUEST_ID", "")
        if (roomNumber != "") {
            getActiveGuestsCount(roomNumber, guestId)
        }
    }

    /**
     * get doc support status
     */
    fun getCdocStatusAsync(providerCode: String?, orgCode: String?) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                cdocSupport.postValue(BaseResponse(WebService.getInstance().getProviderOnlineStatus(providerCode, orgCode)))
            }
        } catch (e: Exception) {

        }
    }

    var disposable: Disposable? = null

    /**
     * get patient future and past appointment list
     */
    fun getPatientApptHistory() {
        if (AuthManager.isTokenExpire()) {
            AuthManager.acquireNewTokenAsync(context) { apicall() }
        } else {
            apicall()
        }
    }

    /**
     * appointement api call
     */
    fun apicall() {
        val restApiCall = RestApiCall.getApiService<RxJavaApi>(RxJavaApi::class.java)
        val futureObservable = restApiCall.getApptList(createRequest(FUTUREAPPT))
        val pastObserveable = restApiCall.getApptList(createRequest(BaseFragment.PASTAPPT))
        disposable = futureObservable.zipWith(pastObserveable, { first: BaseResponseModel<ArrayList<ResApptList>>,
                                                                 second: BaseResponseModel<ArrayList<ResApptList>> ->
            Pair(first, second)
        })
                .subscribeOn(Schedulers.io()).doOnError {
                    errorLiveData.postValue(true)
                }
                .map { pair: Pair<BaseResponseModel<ArrayList<ResApptList>>, BaseResponseModel<ArrayList<ResApptList>>> ->
                    val list = ArrayList<BaseResponseModel<ArrayList<ResApptList>>>()
                    list.add(pair.first)
                    list.add(pair.second)
                    list
                }.observeOn(AndroidSchedulers.mainThread()).subscribe { list: ArrayList<BaseResponseModel<ArrayList<ResApptList>>> ->
                    apptList.postValue(list)
                }
    }

    /**
     * create request for patient appt.
     */
    fun createRequest(myApptTab: String): ReqApptList? {
        val reqApptList = ReqApptList()
        reqApptList.countPerPage = 1
        reqApptList.dateToSearch = getDate()
        reqApptList.futureOrpast = myApptTab.toInt()
        reqApptList.userId = CDoctor2Application.getLoginInfo().account
        reqApptList.pageNumber = 0
        return reqApptList
    }

    fun getDate(): String? {
        val cal = Calendar.getInstance()
        val dateFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US)
        return dateFormat.format(cal.time)
    }



}