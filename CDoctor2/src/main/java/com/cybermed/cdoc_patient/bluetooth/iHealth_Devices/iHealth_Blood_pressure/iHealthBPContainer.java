package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Blood_pressure;

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

public class iHealthBPContainer extends BaseFragment implements IBackPressFrag {
    Ihealthbg5BaseBinding binding;
    iHealthBp3LFragment fragBPStep1;
    iHealthBP3LMeasurementFragment fragBPStep2;
    iHealthBPResultFragment fragBPStep3;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.ihealthbg5_base, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        binding.progressIndicator.setVisibility(View.GONE);
        fragBPStep1 = new iHealthBp3LFragment();
        fragBPStep1.setBackPressListner(this);
        fragBPStep1.setArguments(getArguments());
        fragBPStep2 = new iHealthBP3LMeasurementFragment();
        fragBPStep2.setBackPressListner(this);
        fragBPStep3 = new iHealthBPResultFragment();
        fragBPStep3.setBackPressListner(this);
        replaceFragmentWithAnimation(fragBPStep1, "0");
        binding.toolbar.txtTittle.setText(getString(R.string.iot_blood_pressure));
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPress();
            }
        });
    }



    public void disconnectDevice() {
        if (fragBPStep1 != null)
            fragBPStep1.disconnect();
    }

    public void openBG5Step1Fragment() {
        replaceFragmentWithAnimation(fragBPStep1, "0");
    }

    public void openBG5Step2Fragment() {
        replaceFragmentWithAnimation(fragBPStep2, "1");
    }

    public void openBG5Step3Fragment() {
        replaceFragmentWithAnimation(fragBPStep3, "2");
    }

    public void initNextBtn() {
        switch (getChildFragmentManager().getBackStackEntryCount()) {
            case 0:
                openBG5Step1Fragment();
                break;
            case 1:
                openBG5Step2Fragment();
                break;
            case 2:
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


    @Override
    public void backPress() {
        disconnectDevice();
        getParentFragmentManager().popBackStack();
        if (CDoctor2Application.getTabletMode()) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthBp3LFragment_to_tabletMainFragment);
        } else
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthBp3LFragment_to_IOT_MainPage_Fragment);

    }

    @Override
    public void moveToNext() {
        initNextBtn();
    }
}
