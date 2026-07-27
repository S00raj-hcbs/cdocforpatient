package com.cybermed.cdoc_patient.login.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cdfortis.datainterface.soap.WebService
import com.cybermed.cdoc_patient.common.base.BaseViewModel
import com.cybermed.cdoc_patient.common.base.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginVM(application: Application) : BaseViewModel(application) {

    val context = application.applicationContext

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val liveDataApiResponse = SingleLiveEvent<SignInUpLiveAction<Any>>()
    private val errorLiveData = MutableLiveData<Boolean>()
    val passwordView = MutableLiveData<Boolean>()

    fun getApiResponse(): SingleLiveEvent<SignInUpLiveAction<Any>> {
        return liveDataApiResponse
    }

    fun recoverUserPassword(userId: String?) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                liveDataApiResponse.postValue(SignInUpLiveAction(
                        BaseResponse(WebService.getInstance().RecoverUserPassword(userId)), SignInUpEvent.RECOVER_USER_PASSWORD))

            }
        } catch (e: Exception) {
            errorLiveData.postValue(true)
        }
    }

}