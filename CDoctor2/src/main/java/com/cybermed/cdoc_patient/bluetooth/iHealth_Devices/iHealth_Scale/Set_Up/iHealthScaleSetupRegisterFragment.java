package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Scale.Set_Up;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentScaleBluetoothSetupRegisterBinding;

public class iHealthScaleSetupRegisterFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TEXT = "text";
    FragmentScaleBluetoothSetupRegisterBinding binding;

    public iHealthScaleSetupRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       binding = DataBindingUtil.inflate(inflater,R.layout.fragment_scale_bluetooth_setup_register, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) { }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }
}