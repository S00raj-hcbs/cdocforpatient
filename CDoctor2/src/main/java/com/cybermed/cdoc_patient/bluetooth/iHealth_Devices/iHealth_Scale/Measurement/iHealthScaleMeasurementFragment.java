package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Scale.Measurement;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.EventBusFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.MeasuringData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ToResultFragment;
import com.cybermed.cdoc_patient.databinding.FragmentScaleMeasureBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class iHealthScaleMeasurementFragment extends EventBusFragment {


    FragmentScaleMeasureBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scale_measure, container, false);


        return binding.getRoot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayData(MeasuringData measuringData) {
        String weight = measuringData.getData1();
        binding.weightGraph.setText(weight);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ToResultFragment(ToResultFragment toResultFragment) {
        if (getParentFragment() != null)
            ((iHealthScaleBaseContainer) getParentFragment()).initNextBtn();
        //Navigation.findNavController(getView()).navigate(R.id.action_iHealthScaleMeasurementFragment_to_iHealthScaleResultFragment);
    }
}