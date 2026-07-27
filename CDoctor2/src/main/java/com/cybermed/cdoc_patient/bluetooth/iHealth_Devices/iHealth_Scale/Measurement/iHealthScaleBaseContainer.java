package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Scale.Measurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.Ihealthbg5BaseBinding;

public class iHealthScaleBaseContainer extends BaseFragment {
    Ihealthbg5BaseBinding binding;
    iHealthScaleFragment iHealthScaleFragment1;
    iHealthScaleMeasurementFragment iHealthScaleMeasure2;
    iHealthScaleResultFragment iHealthScaleResultFragment3;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.ihealthbg5_base, container, false);

        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        iHealthScaleFragment1 = new iHealthScaleFragment();
        iHealthScaleFragment1.setArguments(getArguments());
        iHealthScaleMeasure2 = new iHealthScaleMeasurementFragment();
        iHealthScaleResultFragment3 = new iHealthScaleResultFragment();
        replaceFragmentWithAnimation(iHealthScaleFragment1, "0");
        binding.toolbar.txtTittle.setText(getString(R.string.iot_weight_scale));
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iHealthScaleFragment1.disconnect();
                backToHome();
            }
        });
    }

    public void backToHome() {
        getParentFragmentManager().popBackStack();
        if (CDoctor2Application.getTabletMode())
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthScaleFragment_to_TabletMainFragment);
        else
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthScaleFragment_to_iHealthToIOTFragment);

    }

    public void openBG5Step1Fragment() {
        replaceFragmentWithAnimation(iHealthScaleFragment1, "0");
    }

    public void openBG5Step2Fragment() {
        replaceFragmentWithAnimation(iHealthScaleMeasure2, "1");
    }

    public void openBG5Step3Fragment(boolean addProgress) {
        if (addProgress) {
            int progress = binding.progressIndicator.getProgress();
            binding.progressIndicator.setProgress(progress + 2);
        }
        replaceFragmentWithAnimation(iHealthScaleResultFragment3, "2");
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
                openBG5Step3Fragment(false);
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
}
