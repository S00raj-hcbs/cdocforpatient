package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.BG5.Set_Up;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.ActivityIHealthBg5SetupBinding;

public class iHealthBG5SetupFragment extends BaseFragment {


    ActivityIHealthBg5SetupBinding binding;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_i_health_bg5_setup, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initView();

    }

    public void initView() {
        binding.toolbar.txtTittle.setText(getString(R.string.iot_glucometer));
        binding.toolbar.backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_iHealthBG5SetupFragment_to_IOTDeviceSetUpFragment));
        clickListener();
    }

    private void clickListener() {
        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_iHealthBG5SetupFragment_to_iHealthBG5SetupBluetoothFragment);

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }
}