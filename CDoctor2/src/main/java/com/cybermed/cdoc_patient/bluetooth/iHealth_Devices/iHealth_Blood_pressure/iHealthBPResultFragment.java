package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Blood_pressure;


import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.EventBusFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.OnBackPressedListener;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ResultData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StopMeasuring;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;
import com.cybermed.cdoc_patient.databinding.FragmentBpResultBinding;
import com.cybermed.cdoc_patient.util.AppUtiltiy;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.BP_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BtUtils.defaultEventBus;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT1;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT2;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.TIMESTAMP;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.VALUE;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MAX_BP;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MIN_BP;

public class iHealthBPResultFragment extends EventBusFragment implements OnBackPressedListener {

    FragmentBpResultBinding binding;
    Context context;
    String sys;
    String dia;
    IBackPressFrag iBackPressListner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bp_result, container, false);
        context = getActivity();
        binding.toolbar.relativeParent.setVisibility(View.GONE);
        ResultData resultData = defaultEventBus().removeStickyEvent(ResultData.class);
        displayData(resultData);
        binding.btnViewHistory.setOnClickListener(v -> {
            defaultEventBus().post(new StopMeasuring());
            Bundle args = new Bundle();
            args.putString(VALUE, BP_DEVICE_TYPE);
            args.putString(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
            args.putString(MEASUREMENT1, sys);
            args.putString(MEASUREMENT2, dia);
            getParentFragmentManager().popBackStack();
            Navigation.findNavController(v).navigate(R.id.action_iHealthBPFragment_to_IOTGraph, args);
        });
        binding.imginfo.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString(KEY_MIN_BP, dia);
            args.putString(KEY_MAX_BP, sys);
            BPInfoFragment bpInfoFragment = new BPInfoFragment();
            bpInfoFragment.setArguments(args);
            bpInfoFragment.show(getParentFragmentManager(), "BpInfo Fragment");
        });
        return binding.getRoot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayData(ResultData resultData) {
        sys = resultData.getData1();
        dia = resultData.getData2();
        String pulse = resultData.getData3();

        binding.layResultbp.txtSys.setText(sys);
        binding.layResultbp.txtDia.setText(dia);
        binding.txtPulse.setText(pulse);
        setBPStatus(dia, sys);

    }

    public void setBPStatus(String minBpLevel, String maxBpLevel) {
        if (minBpLevel != null) {
            binding.txtDiaStatus.setText(AppUtiltiy.getBpDiaStatus(minBpLevel, context));
            binding.txtDiaStatus.setTextColor(AppUtiltiy.getDiaColor());
        }
        if (maxBpLevel != null) {
            binding.txtSysStatus.setText(AppUtiltiy.getBpSysStatus(maxBpLevel, context));
            binding.txtSysStatus.setTextColor(AppUtiltiy.getSysColor());
        }
    }

    @Override
    public void onBackPressed() {
        iBackPressListner.backPress();

    }

    public void setBackPressListner(IBackPressFrag iBackPressListner){
        this.iBackPressListner=iBackPressListner;
    }
}