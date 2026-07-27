package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Pulse_Oximeter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.Ihealthbg5BaseBinding;

public class iHealthPulseOxiBaseContainer extends BaseFragment implements IBackPressFrag {
    Ihealthbg5BaseBinding binding;
    iHealthPulseOxiFragment fragPluseOxiStep1;
    iHealthPulseOxiMeasurementFragment fragPluseOxiStep2;
    iHealthPulseOxiResultFragment fragPluseOxiStep3;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.ihealthbg5_base, container, false);

        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        fragPluseOxiStep1 = new iHealthPulseOxiFragment();
        fragPluseOxiStep1.setArguments(getArguments());
        fragPluseOxiStep1.setBackPressListner(this);
        fragPluseOxiStep2 = new iHealthPulseOxiMeasurementFragment();
        fragPluseOxiStep2.setBackPressListner(this);
        fragPluseOxiStep3 = new iHealthPulseOxiResultFragment();
        fragPluseOxiStep3.setBackPressListner(this);
        replaceFragmentWithAnimation(fragPluseOxiStep1, "0");
        binding.toolbar.txtTittle.setText(getString(R.string.iot_pulse_oximeter));
        binding.toolbar.backBtn.setOnClickListener(v -> backPress());
    }


    public void disconnectDevice() {
        if (fragPluseOxiStep1 != null)
            fragPluseOxiStep1.disconnect();
    }

    public void openBG5Step1Fragment() {
        replaceFragmentWithAnimation(fragPluseOxiStep1, "0");
    }

    public void openBG5Step2Fragment() {
        replaceFragmentWithAnimation(fragPluseOxiStep2, "1");
    }

    public void openBG5Step3Fragment() {
        replaceFragmentWithAnimation(fragPluseOxiStep3, "2");
    }

    public void initNextBtn() {
        switch (getChildFragmentManager().getBackStackEntryCount()) {
            case 0:
                openBG5Step1Fragment();
                break;
            case 1:
                addProgress();
                openBG5Step2Fragment();
                break;
            case 2:
                addProgress();
                openBG5Step3Fragment();
                break;
        }
    }


    public void replaceFragmentWithAnimation(Fragment fragment, String tag) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (getChildFragmentManager().getBackStackEntryCount() != 0) {
            transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left, R.animator.slide_from_left, R.animator.slide_to_right);
        }
        transaction.replace(R.id.content, fragment, "1");
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    void addProgress() {
        int progress = binding.progressIndicator.getProgress();
        binding.progressIndicator.setProgress(progress + 1);
    }

    void backProgress() {
        int progress = binding.progressIndicator.getProgress();
        binding.progressIndicator.setProgress(progress - 1);
    }


    @Override
    public void backPress() {
        disconnectDevice();
        getParentFragmentManager().popBackStack();
        if (CDoctor2Application.getTabletMode()) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthPulseOxiFragment_to_TabletMainFragment);
        } else {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthPulseOxiFragment_to_IOTMainPageFragment);
        }
    }

    @Override
    public void moveToNext() {
        initNextBtn();
    }
}
