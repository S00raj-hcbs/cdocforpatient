package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Blood_pressure;


import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BtUtils.defaultEventBus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.EventBusFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.MeasuringData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StartMeasuring;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ToResultFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;
import com.cybermed.cdoc_patient.databinding.FragmentBp3lMeasureBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

public class iHealthBP3LMeasurementFragment extends EventBusFragment {

    FragmentBp3lMeasureBinding binding;
    IBackPressFrag iBackPressListner;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bp3l_measure, container, false);
        binding.toolbar.relativeParent.setVisibility(View.GONE);
        defaultEventBus().post(new StartMeasuring());
        binding.btnStartMeasure.setEnabled(false);
        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPress();
            }
        });
        binding.btnStartMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnStartMeasure.setEnabled(false);
                binding.btnStop.setEnabled(true);
                binding.btnStop.setTextColor(ContextCompat.getColor(getActivity(), R.color.azure));
                defaultEventBus().post(new StartMeasuring());
            }
        });

        return binding.getRoot();
    }

    private void backPress() {
        iBackPressListner.backPress();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayData(MeasuringData measuringData) {
        String measure = measuringData.getData1();
        if (binding.bpMeasurement != null)
            binding.bpMeasurement.setText(measure);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ToResultFragment(ToResultFragment toResultFragment) {
        this.iBackPressListner.moveToNext();
    }
    public void setBackPressListner(IBackPressFrag iBackPressListner){
        this.iBackPressListner=iBackPressListner;
    }
}
