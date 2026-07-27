package com.cybermed.cdoc_patient.ViewModel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


data class QRCodeData(val qrCode: String, val view: View)

class QRCodeViewModel : ViewModel() {
    private val qrCodeDataObs: MutableLiveData<QRCodeData> = MutableLiveData()

    fun getQRCodeDataObs(): LiveData<QRCodeData> {
        return qrCodeDataObs
    }

    fun setQRCodeData(qrCodeData: QRCodeData) {
        qrCodeDataObs.value = qrCodeData
    }
}