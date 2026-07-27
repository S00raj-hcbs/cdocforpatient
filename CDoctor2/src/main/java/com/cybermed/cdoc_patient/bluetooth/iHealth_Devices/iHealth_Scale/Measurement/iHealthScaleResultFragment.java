package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Scale.Measurement;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.ButterKnifeFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.OnBackPressedListener;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ResultData;
import com.cybermed.cdoc_patient.databinding.FragmentScaleResultBinding;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SCALE_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BtUtils.defaultEventBus;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT1;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT2;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.TIMESTAMP;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.VALUE;

public class iHealthScaleResultFragment extends ButterKnifeFragment implements OnBackPressedListener {

    FragmentScaleResultBinding binding;
    String displayedData;
    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scale_result, container, false);


        if (RESULT.getValue() != null) {
            displayedData = RESULT.getValue();
            RESULT.setValue(null);
        } else {
            ResultData resultData = defaultEventBus().getStickyEvent(ResultData.class);
            displayedData = resultData.getData1();
        }


        binding.txtGlucose.setText(displayedData);
        binding.btnViewHistory.setOnClickListener(v -> goBackToMain());

        return binding.getRoot();
    }


    private void goBackToMain() {
        Bundle args = new Bundle();
        args.putString(VALUE, SCALE_DEVICE_TYPE);
        args.putString(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        args.putString(MEASUREMENT1, displayedData);
        args.putString(MEASUREMENT2, "");
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthScaleFragment_to_iHealthIOTGraph, args);
    }


    @Override
    public void onBackPressed() {
        goBackToMain();
    }
}