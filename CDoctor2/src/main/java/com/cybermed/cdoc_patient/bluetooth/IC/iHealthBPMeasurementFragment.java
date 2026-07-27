package com.cybermed.cdoc_patient.bluetooth.IC;


import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentBpMeasureBinding;

public class iHealthBPMeasurementFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TEXT = "text";

    // TODO: Rename and change types of parameters
    private String mText;

//    private OnFragmentInteractionListener mListener;


    private MyCountDownTimer myCountDownTimer;
    FragmentBpMeasureBinding binding;

    public iHealthBPMeasurementFragment() {
        // Required empty public constructor
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bp_measure, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initView();

    }

    public void initView(){
        binding.progressBar.setProgress(100);
        myCountDownTimer = new MyCountDownTimer(5000, 500);
        myCountDownTimer.start();
    }




    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished/100);
            binding.progressBar.setProgress(progress);
        }

        @Override
        public void onFinish() {
            binding.progressBar.setProgress(0);
//            ((iHealthPulseOxiFragment)getActivity()).openFragment(((iHealthPulseOxiFragment)getActivity()).iHealthPulseOxiResultFragmentFragment);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myCountDownTimer.cancel();
    }
}