package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Scale.Set_Up;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.ActivityIHealthBg5SetupBinding;
import com.cybermed.cdoc_patient.databinding.FragmentScaleSetupBinding;

public class iHealthScaleSetupFragment extends BaseFragment {


    private static final String TEXT = "text";
    private com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Scale.Set_Up.iHealthScaleSetupBluetoothFragment iHealthScaleSetupBluetoothFragment;
    ActivityIHealthBg5SetupBinding binding;


    public iHealthScaleSetupFragment() {
        // Required empty public constructor
    }

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
        binding.imgDevice.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.scale_tap));
        binding.txtTittle.setText(getString(R.string.iot_scale_setup_title));
        binding.txtDesc.setText(getString(R.string.iot_scale_instructions));
        binding.toolbar.txtTittle.setText(getString(R.string.iot_scale));
        binding.toolbar.backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_iHealthScaleSetupFragment_to_iHealthIOTDeviceSetUpFragment));
        clickListener();
    }

    private void clickListener() {
        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_iHealthScaleSetupFragment_to_iHealthScaleSetupBluetoothFragment);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }
}