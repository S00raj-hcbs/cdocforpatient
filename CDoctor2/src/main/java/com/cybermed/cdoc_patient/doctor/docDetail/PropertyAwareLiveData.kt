package com.cybermed.cdoc_patient.doctor.docDetail

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData

/**
 * property aware live data for booking calender model
 */
class PropertyAwareLiveData<T : BaseObservable> : MutableLiveData<T>() {

    val callback = object : Observable.OnPropertyChangedCallback(){
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            value = value
        }
    }
    override fun setValue(value: T?) {
        super.setValue(value)
        value?.addOnPropertyChangedCallback(callback)
    }
}