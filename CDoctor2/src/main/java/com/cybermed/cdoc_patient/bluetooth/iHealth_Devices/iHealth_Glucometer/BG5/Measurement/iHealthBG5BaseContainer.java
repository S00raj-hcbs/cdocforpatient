package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.BG5.Measurement;

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

public class iHealthBG5BaseContainer extends BaseFragment implements IBackPressFrag {
    Ihealthbg5BaseBinding binding;
    iHealthBG5Fragment fragBG5Step1;
    iHealthBG5InstructionFragment fragBG5Step2;
    iHealthBG5ResultFragment fragBG5Step3;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.ihealthbg5_base, container, false);

        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        fragBG5Step1 = new iHealthBG5Fragment();
        fragBG5Step1.setArguments(getArguments());
        fragBG5Step1.setBackPressListner(this);
        fragBG5Step2 = new iHealthBG5InstructionFragment();
        fragBG5Step2.setBackPressListner(this);
        fragBG5Step3 = new iHealthBG5ResultFragment();
        fragBG5Step3.setBackPressListner(this);
        replaceFragmentWithAnimation(fragBG5Step1, "0");
        binding.toolbar.txtTittle.setText(getString(R.string.iot_glucometer));
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragBG5Step1.disconnect();
                backPress();
            }
        });
    }



    public void openBG5Step1Fragment() {
        replaceFragmentWithAnimation(fragBG5Step1, "0");
    }

    public void openBG5Step2Fragment() {
        replaceFragmentWithAnimation(fragBG5Step2, "1");
    }

    public void openBG5Step3Fragment() {
        replaceFragmentWithAnimation(fragBG5Step3, "2");
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
        if (fragBG5Step1 != null)
            fragBG5Step1.disconnect();
        getParentFragmentManager().popBackStack();
        if (CDoctor2Application.getTabletMode()) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthBG5Fragment_to_iHealthTabletMain);
        } else
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthBG5Fragment_to_iHealthIotMainFragment);

    }

    @Override
    public void moveToNext() {
        initNextBtn();
    }
}
