package com.cybermed.cdoc_patient.common.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class BaseViewModel extends AndroidViewModel {
    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    private SingleLiveEvent<LiveActions> uiLiveEvent = new SingleLiveEvent<LiveActions>();// Handles all ui related events


    public SingleLiveEvent<LiveActions> getUILiveEvent() {
        return uiLiveEvent;
    }
}
