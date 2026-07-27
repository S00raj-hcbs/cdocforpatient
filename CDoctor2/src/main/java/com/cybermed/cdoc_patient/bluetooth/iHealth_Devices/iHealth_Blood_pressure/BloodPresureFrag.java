package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Blood_pressure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragBloodPresureBinding;

import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MAX_BP;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MIN_BP;

public class BloodPresureFrag extends BaseFragment {
    FragBloodPresureBinding binding;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.frag_blood_presure, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        Bundle data = getArguments();
        if (data != null) {
            String minVal = data.getString(KEY_MIN_BP);
            String maxVal = data.getString(KEY_MAX_BP);
            if (maxVal != null) {
                if (Integer.parseInt(maxVal) >= 70 && Integer.parseInt(maxVal) <= 90) {
                    binding.linearBelowNormal.setVisibility(View.VISIBLE);
                    binding.txtBelowNormal.setText(getString(R.string.sys)+" "+maxVal);
                } else if (Integer.parseInt(maxVal) >= 91 && Integer.parseInt(maxVal) <= 120) {
                    binding.linearNormal.setVisibility(View.VISIBLE);
                    binding.txtNormal.setText(getString(R.string.sys)+" "+maxVal);
                } else if (Integer.parseInt(maxVal) >= 121) {
                    binding.linearAboveHigh.setVisibility(View.VISIBLE);
                    binding.txtAboveHigh.setText(getString(R.string.sys)+" "+maxVal);
                }
            }
            if (minVal != null) {
                if (Integer.parseInt(minVal) >= 40 && Integer.parseInt(minVal) <= 60) {
                    binding.linearLow.setVisibility(View.VISIBLE);
                    binding.txtLow.setText( getString(R.string.dia)+" "+minVal);
                } else if (Integer.parseInt(minVal) >= 61 && Integer.parseInt(minVal) <= 80) {
                    binding.linearLowNromal.setVisibility(View.VISIBLE);
                    binding.txtLowNormal.setText(getString(R.string.dia)+" "+minVal);
                } else if (Integer.parseInt(minVal) >= 81 ) {
                    binding.linearLowBelow.setVisibility(View.VISIBLE);
                    binding.txtLowBelow.setText(getString(R.string.dia)+" "+minVal);
                }
            }
        }
    }




}
