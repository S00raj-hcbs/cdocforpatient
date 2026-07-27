package com.cybermed.cdoc_patient.login.signup;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.databinding.FragmentSignup0Binding;
import com.cybermed.cdoc_patient.login.viewmodel.SignUpVM;
import com.cybermed.cdoc_patient.util.LocalizationUtil;

import java.util.Locale;


/**
 * Created by joshu on 9/11/2017.
 */

public class SignUp0Fragment extends BaseMVVMFragment<SignUpVM> {

    FragmentSignup0Binding binding;

    @Override
    protected SignUpVM createViewModel() {
        return new ViewModelProvider(getActivity()).get(SignUpVM.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_signup0;
    }

    @Override
    public void onViewModelCreated(View view, SignUpVM viewModel) {
        binding = (FragmentSignup0Binding) getDataBinding();
        initViews();
    }

    /**
     * init views
     */
    private void initViews() {
        final String teleHealth = LocalizationUtil.getLocalizedResources(getActivity(), Locale.US).getString(R.string.TeleHealth_Mode);
        final String remoteMonitor = LocalizationUtil.getLocalizedResources(getActivity(), Locale.US).getString(R.string.Remote_Monitoring_Mode);
        setSelected(binding.remoteMonitoringSwitch);

        binding.next.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(viewModel.getClinicCode().getValue())) {
                viewModel.getMoveNext().setValue(true);
            } else
                viewModel.getCliniCodePopUp().setValue(true);
        });

        binding.tvInfo.setOnClickListener(v -> {
            viewModel.getCliniCodePopUp().setValue(true);
        });

        binding.remoteMonitoringSwitch.setOnClickListener(v -> {
            viewModel.getModeSelected().setValue(remoteMonitor);
            setSelected(binding.remoteMonitoringSwitch);
            setUnSelected(binding.telehealthSwitch);
        });

        binding.telehealthSwitch.setOnClickListener(v -> {
            viewModel.getModeSelected().setValue(teleHealth);
            setSelected(binding.telehealthSwitch);
            setUnSelected(binding.remoteMonitoringSwitch);
        });

        if (viewModel.getModeSelected().getValue() == null || viewModel.getModeSelected().getValue().equals(teleHealth)) {
            binding.telehealthSwitch.performClick();
        } else if (viewModel.getModeSelected().getValue().equals(remoteMonitor)) {
            //If previously selected remote monitoring mode
            setSelected(binding.remoteMonitoringSwitch);
            setUnSelected(binding.telehealthSwitch);
        }
    }

    public void setSelected(TextView tv) {
        tv.setSelected(true);
        tv.setTextColor(getActivity().getResources().getColor(R.color.color_007ec0));
    }

    public void setUnSelected(TextView tv) {
        tv.setSelected(false);
        tv.setTextColor(getActivity().getResources().getColor(R.color.color_98b3c2));
    }

}
