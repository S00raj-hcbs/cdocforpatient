package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

public class EventBusFragment extends ButterKnifeFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}


