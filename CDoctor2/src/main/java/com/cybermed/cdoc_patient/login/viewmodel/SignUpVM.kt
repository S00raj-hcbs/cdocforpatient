package com.cybermed.cdoc_patient.login.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cdfortis.datainterface.soap.OnPostExecute
import com.cdfortis.datainterface.soap.WebService
import com.cdfortis.datainterface.soap.WebServiceID
import com.cybermed.cdoc_patient.R
import com.cybermed.cdoc_patient.common.base.BaseViewModel
import com.cybermed.cdoc_patient.common.base.SingleLiveEvent
import com.cybermed.cdoc_patient.login.signup.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SignUpVM(application: Application) : BaseViewModel(application) {
    /**
     * Select Mode
     */
    val modeSelected = MutableLiveData<String>()
    val clinicCode = MutableLiveData<String>()

    /**
     * Basic info edit text fields
     */
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    val mobile = MutableLiveData<String>()
    val passwordView = MutableLiveData<Boolean>()

    /**
     * Basic info validation error fields
     */
    val emailError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()
    val confirmPasswordError = MutableLiveData<String>()
    val mobileError = MutableLiveData<String>()

    /**
     * Account info edit text fields
     */
    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val dob = MutableLiveData<String>()

    /**
     *Account info error fields
     */
    val firstNameError = MutableLiveData<String>()
    val lastNameError = MutableLiveData<String>()
    val dobError = MutableLiveData<String>()

    /**
     * Contact Info fields
     */
    var addressLine1 = MutableLiveData<String>()
    var addressLine2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zipCode = MutableLiveData<String>()
    val zipCodeError = MutableLiveData<String>()

    val moveNext = MutableLiveData<Boolean>()
    val complete = MutableLiveData<Boolean>()
    val moveBack = MutableLiveData<Boolean>()
    val cliniCodePopUp = MutableLiveData<Boolean>()

    private val liveDataApiResponse = SingleLiveEvent<SignInUpLiveAction<Any>>()
    private val errorLiveData = MutableLiveData<Boolean>()

    fun getApiResponse(): SingleLiveEvent<SignInUpLiveAction<Any>> {
        return liveDataApiResponse
    }

    /**
     * Basic info validation check
     */
    fun validationBasicInfoCheck(): Boolean {
        var isValidate = false
        if (!ValidationUtils.isEmailAddress(email.value) || !ValidationUtils.isEmailValid(email.value)) {
            emailError.value = getApplication<Application>().applicationContext.getString(R.string.regist_error_email)
        } else emailError.value = ""

        if ((password.value?.length ?: 0) < 5) {
            passwordError.value = getApplication<Application>().applicationContext.getString(R.string.regist_error_password_short)
        } else passwordError.value = ""

        if (!confirmPassword.value.equals(password.value)) {
            confirmPasswordError.value = getApplication<Application>().applicationContext.getString(R.string.regist_error_confirm_password_match)
        } else confirmPasswordError.value = ""

        if (!ValidationUtils.isPhoneNum(mobile.value)) {
            mobileError.value = getApplication<Application>().applicationContext.getString(R.string.regist_error_phone)
        } else mobileError.value = ""

        if (mobile.value?.length != 12) {
            mobileError.value = getApplication<Application>().applicationContext.getString(R.string.regist_error_phone)
        } else mobileError.value = ""

        if (emailError.value.equals("") && passwordError.value.equals("") &&
                confirmPasswordError.value.equals("") && mobileError.value.equals("")) {
            isValidate = true
        }
        return isValidate
    }

    /**
     * Contact info validation check
     */
    fun validationContactInfoCheck(): Boolean {
        if ((zipCode.value?.length ?: 0) != 5) {
            zipCodeError.value = getApplication<Application>().applicationContext.getResources().getString(R.string.regist_error_zip_verify)
            return false
        } else return true
    }

    /**
     * Account info validation check
     */
    fun validationAccountInfoCheck(): Boolean {
        var isValidate = false
        if ((firstName.value?.length ?: 0) < 2 &&
            getApplication<Application>().applicationContext.getResources().getConfiguration().locale != Locale.CHINA) {
            firstNameError.value = getApplication<Application>().applicationContext.getString(R.string.regist_error_invalid_name)
        } else firstNameError.value = ""

        if ((lastName.value?.length ?: 0) < 2 &&
            getApplication<Application>().applicationContext.getResources().getConfiguration().locale != Locale.CHINA) {
            lastNameError.value = getApplication<Application>().applicationContext.getString(R.string.regist_error_invalid_name)
        } else lastNameError.value = ""
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = df.format(c.time)
        if (!ValidationUtils.compareDates(currentDate, dob.value)) {
            dobError.value = getApplication<Application>().applicationContext.getString(R.string.regist_error_invalid_dob)
        } else dobError.value = ""
        if (dobError.value.equals("") && lastNameError.value.equals("") && firstNameError.value.equals("")) {
            isValidate = true
        }
        return isValidate
    }

    fun checkEmailDuplication(check_Duplicate_Email_Address: WebServiceID, ope: OnPostExecute?, email: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
//                liveDataApiResponse.postValue(SignInUpLiveAction(
//                        WebService.webServiceAsyncTask(check_Duplicate_Email_Address, ope, email), SignInUpEvent.EVENT_CHECK_EMAIL_DUPLICATE))
                WebService.webServiceAsyncTask(check_Duplicate_Email_Address, ope, email)
            }
        } catch (e: Exception) {
            errorLiveData.postValue(true)
        }
    }

}