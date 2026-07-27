package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.BG5.Measurement;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.EventBusFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ToResultFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

public class iHealthBG5InstructionFragment extends EventBusFragment {
    IBackPressFrag iBackPressListner;
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bg5_instructions, container, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ToResultFragment(ToResultFragment toResultFragment) {
        iBackPressListner.moveToNext();
        // Navigation.findNavController(getView()).navigate(R.id.action_iHealthBG5InstructionFragment_to_iHealthBG5ResultFragment);
    }
    public void setBackPressListner(IBackPressFrag iBackPressListner) {
        this.iBackPressListner = iBackPressListner;
    }
}