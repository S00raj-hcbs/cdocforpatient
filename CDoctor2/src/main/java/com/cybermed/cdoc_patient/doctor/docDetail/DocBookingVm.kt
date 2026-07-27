package com.cybermed.cdoc_patient.doctor.docDetail

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cdfortis.datainterface.soap.OnPostExecute
import com.cdfortis.datainterface.soap.WebService
import com.cdfortis.datainterface.soap.WebServiceID
import com.cdfortis.datainterface.soap.model.ProviderAvaliability
import com.cdfortis.datainterface.soap.model.ProviderHours
import com.cdfortis.datainterface.soap.model.SoapObjectVector
import com.cybermed.cdoc_patient.R
import com.cybermed.cdoc_patient.common.base.BaseViewModel
import com.cybermed.cdoc_patient.common.base.SingleLiveEvent
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel
import com.cybermed.cdoc_patient.doctor.docDetail.model.DayDateModel
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResDocNextAvail
import com.cybermed.cdoc_patient.doctor.searchDoctor.ResponseDocInfo
import com.cybermed.cdoc_patient.login.viewmodel.BaseResponse
import com.cybermed.cdoc_patient.main.MainActAction
import com.cybermed.cdoc_patient.main.MainActEvent
import com.cybermed.cdoc_patient.util.AppConstant.*
import com.cybermed.cdoc_patient.util.DateUtil
import com.cybermed.cdoc_patient.webapi.IResponseReceiver
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ksoap2.serialization.SoapObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DocBookingVm(application: Application) : BaseViewModel(application) {
    val context = application.applicationContext
    private val liveDataApiResponse = SingleLiveEvent<MainActAction<Any>>()

    // doctor's info data
    private val docInfoLiveData = MutableLiveData<ResponseDocInfo>()

    // calender of showing day and date
    val calenderDayDate: PropertyAwareLiveData<DayDateModel>? = PropertyAwareLiveData()

    // providers time slot list
    var apptList = MutableLiveData<Any>()

    // date calender
    var apptDateIncrement = MutableLiveData<Int>()

    //set user payment method
    var paymentMethod = MutableLiveData<Int>()

    // decide user is coming from which page
    var pageFrom = MutableLiveData<Int>()

    // Review list of provider
    var providerReviewList = MutableLiveData<Any>()

    //doctor next availability time
    var dateNextAvail = MutableLiveData<String>()

    //mark doctor fav
    var docFav = MutableLiveData<Boolean>()

    var showProgress=MutableLiveData<Boolean>()

    fun getDocFavourite(): MutableLiveData<Boolean> {
        return docFav
    }

    fun getProvideReviewList(): MutableLiveData<Any> {
        return providerReviewList
    }

    fun getApiResponse(): SingleLiveEvent<MainActAction<Any>> {
        return liveDataApiResponse
    }

    fun getPagerFrom(): MutableLiveData<Int> {
        return pageFrom
    }

    fun getApptBookingList(): MutableLiveData<Any> {
        return apptList
    }

    fun getCalDayDate(): PropertyAwareLiveData<DayDateModel>? {
        return calenderDayDate
    }

    fun getDocInfo(): MutableLiveData<ResponseDocInfo> {
        return docInfoLiveData
    }

    init {
        calenderDayDate?.postValue(DayDateModel("", "", false, "", "", "", ""))
        apptDateIncrement.value = 0
    }

    /**
     * mark provider as favourite api
     */
    fun markProviderAsFavorite(user_id: String, org_code: String,
                               provider_id: String, isFavorite: Int) {
        val ope = OnPostExecute { result: Any ->
            val integer = Integer.valueOf(result.toString())
            if (integer == 1) {
                docFav.value = isFavorite==1
                //docInfoLiveData.value?.favDoc =  true
            }
        }
        WebService.webServiceAsyncTask(WebServiceID.Mark_Provider_as_favorite, ope, user_id, org_code, provider_id, isFavorite.toString())
    }

    /**
     * on click of calender dates
     */
    fun updateVal(position: Int) {
        showProgress.value=true
        for (dayDate: DayDateModel in calenderDayDate?.value!!.calenderDayDateList) {
            dayDate.isSelected = calenderDayDate.value!!.calenderDayDateList?.get(position) == dayDate
        }

        val dtStart: String = calenderDayDate.value!!.calenderDayDateList.get(position).date
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        try {
            val date = format.parse(dtStart)
            val cal = Calendar.getInstance(Locale.US)
            cal.time = date
            val msDiff = cal.timeInMillis - Calendar.getInstance(Locale.US).timeInMillis
            if (msDiff < 0) {
                apptDateIncrement.value = 0
            } else if (msDiff >= 0) {
                val daysDiff = TimeUnit.DAYS.convert(msDiff, TimeUnit.MILLISECONDS)
                apptDateIncrement.value = daysDiff.toInt() + 1
                Log.d("daysdiff", daysDiff.toString())
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        calenderDayDate.value = calenderDayDate.value
    }

    /**
     * set calender dates to ui
     */
    fun initAppointmentTime(calInstance: Calendar) {
        showProgress.value=true
        var cal = calInstance
        val apptDate = ArrayList<DayDateModel>()
        for (i in 0..6) {
            cal.add(Calendar.DATE, if (i > 0) 1 else 0)
            val date = SimpleDateFormat("d", Locale.US).format(cal.time)
            val day = SimpleDateFormat("E", Locale.US).format(cal.time)
            val monthNumber = SimpleDateFormat("MM", Locale.US).format(cal.time)
            val year = SimpleDateFormat("yyyy", Locale.US).format(cal.time)
            val monthString = SimpleDateFormat("MMMM", Locale.US).format(cal.time)
            val fullDate = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(cal.time)
            apptDate.add(DayDateModel(day, date, false, fullDate, monthNumber, year, monthString))
            cal = cal.clone() as Calendar
        }
        apptDate.get(0).isSelected = true
        calenderDayDate?.value?.setCalenderDayDateList(apptDate)

    }


    private val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US)

    /**
     * get appointment time of any specific date
     */
    private var display_provider_schedule_disposable: Disposable? = null
    fun displayProviderSchedule(dateToSearch: String?, mDayDateModel: DayDateModel) {
        if (display_provider_schedule_disposable != null) {
            display_provider_schedule_disposable!!.dispose()
            display_provider_schedule_disposable = null
        }
        val provider_scheduler_hour = Observable.fromCallable {
            val o1 = WebService.WSInstance().RxCallingWebservice(WebServiceID.get_provider_schedule_hour_From_EMR_v2, docInfoLiveData.getValue()?.getOrgCode(), docInfoLiveData.getValue()?.getProviderCode(), dateToSearch)
            SoapObjectVector(ProviderHours::class.java, o1 as SoapObject)
        }.subscribeOn(Schedulers.io())
        val provider_appt = Observable.fromCallable {
            val o2 = WebService.WSInstance().RxCallingWebservice(WebServiceID.get_provider_apptlist, docInfoLiveData.getValue()?.getOrgCode(), docInfoLiveData.getValue()?.getProviderCode(), dateToSearch, "CDOC_ONLINE")
            SoapObjectVector(ProviderAvaliability::class.java, o2 as SoapObject)
        }.subscribeOn(Schedulers.io())
        display_provider_schedule_disposable = provider_scheduler_hour.zipWith(provider_appt, BiFunction<SoapObjectVector<ProviderHours>, SoapObjectVector<ProviderAvaliability>,
                android.util.Pair<SoapObjectVector<ProviderHours>,
                        SoapObjectVector<ProviderAvaliability>>> { first: SoapObjectVector<ProviderHours>,
                                                                   second: SoapObjectVector<ProviderAvaliability> ->
            android.util.Pair(first, second)
        })
                .subscribeOn(Schedulers.io())
                .map { pair: android.util.Pair<SoapObjectVector<ProviderHours>, SoapObjectVector<ProviderAvaliability>> ->
                    val providerHoursList: Vector<ProviderHours> = pair.first
                    val providerAvailabilityList: Vector<ProviderAvaliability> = pair.second
                    val paMap: MutableMap<String, Int?> = HashMap()
                    for (pa in providerAvailabilityList) {
                        val time = pa.appt_date.substring(pa.appt_date.indexOf(" ") + 1)
                        if (paMap.containsKey(time)) {
                            val occurrence = paMap[time]!!
                            paMap[time] = occurrence + 1
                        } else {
                            paMap[time] = 1
                        }
                    }

                    /*Top to refactor*/
                    val apptList: MutableList<String> = ArrayList()
                    val maxApptList: MutableList<Int> = ArrayList()
                    val apptAvailabilityList: MutableList<Int> = ArrayList()
                    for (ph in providerHoursList) {
                        val timeSlot = ph.timeslot
                        if (timeSlot != null) {
                            if (ph.availability == "0" && /*0 means available*/
                                    Integer.valueOf(ph.day_num) == getDayNum(mDayDateModel.getDayOfWeek()) && ph.office_location != null && ph.office_location == "CDOC_ONLINE" ||
                                    ph.availability == "0" && /*0 means available*/
                                    Integer.valueOf(ph.day_num) == getDayNum(mDayDateModel.getDayOfWeek()) && ph.office_location == null && ph.max_appts != "0") { //If location is "ANY")
                                val d1: Date = sdf.parse(timeSlot)
                                val d2 = Date()

                                //Get list of doctor appt time
                                if (apptDateIncrement.value == 0 && compareTwoDatesWithTime(d1, d2) < 0) {
                                } else {
                                    var numAppt = 0
                                    val time = timeSlot.substring(timeSlot.indexOf(" ") + 1)
                                    if (paMap.containsKey(time)) {
                                        numAppt = paMap[time]!!
                                    }
                                    //Set a value for apptList. Makes sure the correct count
                                    apptAvailabilityList.add(numAppt)
                                    maxApptList.add(Integer.valueOf(ph.max_appts))
                                    getTime(timeSlot)?.let { apptList.add(it) }
                                }
                            }
                        }
                    }
                    arrayOf<List<*>>(apptList, maxApptList, apptAvailabilityList)
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe { lists: Array<List<*>>? ->
                    apptList.value = lists
                    showProgress.value=false
                }
    }

    fun getDayNum(weekDay: String): Int {
        var day = 1
        when (weekDay) {
            "Sun" -> day = 1
            "Mon" -> day = 2
            "Tue" -> day = 3
            "Wed" -> day = 4
            "Thu" -> day = 5
            "Fri" -> day = 6
            "Sat" -> day = 7
        }
        return day
    }

    private val sb = StringBuilder()

    private val c1 = Calendar.getInstance()
    private val c2 = Calendar.getInstance()

    fun compareTwoDatesWithTime(d1: Date, d2: Date): Int {
        c1.time = d1
        c2.time = d2
        c1[1900, 1] = 15
        c2[1900, 1] = 15
        return c1.compareTo(c2)
    }


    fun getTime(timeSlot: String): String? {
        sb.setLength(0)
        return try {
            val strArray = timeSlot.split(" ".toRegex()).toTypedArray()
            val time = strArray[1]
            sb.append(time.substring(0, time.lastIndexOf(":")))
            sb.append(" ")
            sb.append(strArray[2])
            sb.toString()
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * get user payment method
     */
    fun initPaidMethod() {
        val ope = OnPostExecute { result: Any? ->
            if (result != null) {
                val option = result.toString()
                if (option.contains("Credit") || option.contains("Insurance")) {
                    //mode 1 = duration, 2 = visit
                    val opeMode = OnPostExecute { resultMode: Any? ->
                        if (resultMode != null) {
                            val mode = resultMode.toString()
                            //visit
                            if (mode == "2") {
                                docInfoLiveData.value?.payingMode = PAID_BY_VISIT
                            } else { // duration or empty
                                docInfoLiveData.value?.payingMode = FEE_SCHEDULE
                            }
                        }
                        paymentMethod.value = docInfoLiveData.value?.payingMode
                        initDoctorStatus()
                    }
                    WebService.webServiceAsyncTask(WebServiceID.Get_Provider_Charge_Mode, opeMode, docInfoLiveData.value?.orgCode, docInfoLiveData.value?.providerCode)
                } else { //free provider
                    docInfoLiveData.value?.payingMode = FREE_PROVIDER
                    paymentMethod.value = docInfoLiveData.value?.payingMode
                    initDoctorStatus()
                }
            }
        }
        WebService.webServiceAsyncTask(WebServiceID.Get_Provider_Payment_Options, ope, docInfoLiveData.value?.orgCode, docInfoLiveData.value?.providerCode)
    }

    fun initDoctorStatus() {
        // waiting room
        if (getDocInfo().getValue()?.getOnlineStatus() == "2" || getDocInfo().getValue()?.getOnlineStatus() == "0" || getDocInfo().getValue()?.getOnlineStatus() == "") {
            getDocInfo().getValue()?.setWaitingRoom(1)
        } else {
            getDocInfo().getValue()?.setWaitingRoom(0)
        }
    }

    /**
     * get provider online status
     */
    fun getProviderOnlineStatus() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                liveDataApiResponse.postValue(MainActAction(
                        BaseResponse(WebService.getInstance().getProviderOnlineStatus(docInfoLiveData.value?.providerCode,
                                docInfoLiveData.value?.orgCode)), MainActEvent.PROVIDER_ONLINE_STATUS))

            }
        } catch (e: Exception) {

        }
    }

    /**
     * get provider  review list
     */
    fun getProviderReview() {
        val ope = OnPostExecute { result: Any? ->
            providerReviewList.value = result
        }
        WebService.webServiceAsyncTask(WebServiceID.get_provider_review_v2, ope, docInfoLiveData.getValue()?.getOrgCode(),
                docInfoLiveData.getValue()?.getProviderCode(), "1", "100")
    }

    /**
     * doctor next available date
     */
    fun getDocNextAvailList(userId: String, provider_id: String) {
        //var count = 0;
        var apiManager = HomeApiManager(object : IResponseReceiver<BaseResponseModel<ResDocNextAvail>> {
            override fun onSuccess(data: BaseResponseModel<ResDocNextAvail>) {
                if (data != null && data.`object` != null) {
                    if (!TextUtils.isEmpty(data.`object`.nextAvailable)) {
                        val date = DateUtil.formatedDate(data.`object`.nextAvailable,
                                "yyyy-MM-dd'T'HH:mm:ss", "hh:mm a, MM/dd/yyyy")
                        if (data.`object`.isHasAvailToday) {
                            dateNextAvail.value = SimpleDateFormat("hh:mm a", Locale.US).format(
                                    SimpleDateFormat("hh:mm a, MM/dd/yyyy", Locale.US).parse(date)) + context.getString(R.string.today_time)
                        } else {
                            dateNextAvail.value = date
                        }

                    } else {
                        dateNextAvail.value = context.getString(R.string.unavil_for_3_days)
                    }
                }

            }

            override fun onFailure(errorResponse: String) {

            }
        }, context)
        apiManager.getDocNextAvailableList(userId, provider_id)
    }

    /**
     * reset the value on back press
     */
    fun resetValues() {
        apptDateIncrement.value = 0
        docInfoLiveData.value?.apptTime = ""
        docInfoLiveData.value?.cardId = ""

    }


}

