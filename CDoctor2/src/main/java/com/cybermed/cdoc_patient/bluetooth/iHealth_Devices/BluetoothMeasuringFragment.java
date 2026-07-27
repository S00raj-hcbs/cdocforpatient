package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentIChoiceMeasuringBinding;

import androidx.databinding.DataBindingUtil;

public class BluetoothMeasuringFragment extends BaseFragment implements View.OnClickListener{
    FragmentIChoiceMeasuringBinding binding;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_i_choice_measuring, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initView();
    }
    public void initView(){
        binding.btnQuit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_quit:
                getActivity().finish();
                break;
        }
    }
}
