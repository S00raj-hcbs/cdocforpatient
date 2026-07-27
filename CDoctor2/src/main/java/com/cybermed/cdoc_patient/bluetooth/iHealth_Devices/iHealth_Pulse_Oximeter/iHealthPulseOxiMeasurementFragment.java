package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Pulse_Oximeter;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentPulseOxiMeasureBinding;

public class iHealthPulseOxiMeasurementFragment extends BaseFragment {


    private MyCountDownTimer myCountDownTimer;
    FragmentPulseOxiMeasureBinding binding;
    IBackPressFrag iBackPressListner;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pulse_oxi_measure, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initContent();
    }

    public void initContent() {

        myCountDownTimer = new MyCountDownTimer(10000, 1000);
        myCountDownTimer.start();

        binding.btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCountDownTimer.cancel();
                if (iBackPressListner != null)
                    iBackPressListner.backPress();
            }
        });
    }


    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            iBackPressListner.moveToNext();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myCountDownTimer.cancel();
    }

    public void setBackPressListner(IBackPressFrag iBackPressListner) {
        this.iBackPressListner = iBackPressListner;
    }
}