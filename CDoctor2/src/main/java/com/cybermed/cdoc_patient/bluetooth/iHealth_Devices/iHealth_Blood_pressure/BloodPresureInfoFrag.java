package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Blood_pressure;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragBpStatusBinding;
import com.cybermed.cdoc_patient.util.AppUtiltiy;

import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MAX_BP;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MIN_BP;

public class BloodPresureInfoFrag extends BaseFragment {
    String status;
    int color;
    Context context;
    FragBpStatusBinding binding;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_bp_status, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        Bundle data = getArguments();
        context = getActivity();
        if (data != null) {
            String minVal = data.getString(KEY_MIN_BP);
            String maxVal = data.getString(KEY_MAX_BP);
            binding.txtSys.setText(String.format("%s: %s", getString(R.string.sys), maxVal));
            binding.txtDia.setText(String.format("%s: %s", getString(R.string.dia), minVal));
            if (minVal != null) {
                binding.txtRangeDia.setText(AppUtiltiy.getBpDiaStatus(minVal, context));
                binding.txtRangeDia.setTextColor(AppUtiltiy.getDiaColor());
            }
            if (maxVal != null) {
                binding.txtRangeSys.setText(AppUtiltiy.getBpSysStatus(maxVal, context));
                binding.txtRangeSys.setTextColor(AppUtiltiy.getSysColor());
            }


        }

    }


}
