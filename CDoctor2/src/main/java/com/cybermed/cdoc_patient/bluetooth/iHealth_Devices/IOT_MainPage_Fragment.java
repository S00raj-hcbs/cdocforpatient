package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import static com.cybermed.cdoc_patient.common.CDoctor2Application.getAndSetDeviceVector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.model.IoT_Device;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.FragmentIotMainPageBinding;

import java.util.Vector;

public class IOT_MainPage_Fragment extends BaseFragment {

    FragmentIotMainPageBinding binding;
    Vector<IoT_Device> ioT_devices;
    Activity mContext;
    ProgressDialog pd;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_iot_main_page, container, false);
        mContext = getActivity();
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initContent();
    }

    public void initContent() {
        UserInfo userInfo = CDoctor2Application.getLoginInfo().getUserInfo();
        if (!userInfo.getService_code().equalsIgnoreCase("IOT")) {
            binding.toolbar.backBtn.setOnClickListener(v -> backPress());
        }

        // mWelcome.setText("Welcome, " + userInfo.getFirstName() + " " + userInfo.getLastname());
        binding.toolbar.txtTittle.setText("Welcome, " + userInfo.getFirstName());
        if (Constant.isvitalnot.equals("1")){
            addDevice();
        }else if (Constant.isvitalnot.equals("2")){
            Constant.isvitalnot="1";
            ((IOTActivity_MainPage) getParentFragment().getParentFragment()).backPress();
        }else if (Constant.isvitalrecord.equals("1")){
            Vitalrecord();
        }else {
            initEvent();
        }


    }


    public void initEvent() {
        showProgressBar();
        getAndSetDeviceVector();
        binding.rpmAddDeviceBtn.setOnClickListener(v -> {
            addDevice();
        });
        CDoctor2Application.getLoginInfo().getUserInfo().getIoT_devices_obs().observe(getViewLifecycleOwner(), ioT_devices -> {
            if (ioT_devices != null && ioT_devices.size() != 0) {
                if (this.ioT_devices == null) {
                    this.ioT_devices = ioT_devices;
                    showHideNoDeviceView(false);
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.pager, new IOTDeviceListFragment()).commit();
                }

            } else showHideNoDeviceView(true);
        });
        CDoctor2Application.getLoginInfo().getUserInfo().getIoT_devices_error().observe(getViewLifecycleOwner(), ioT_devices -> {
            showHideNoDeviceView(true);
        });


    }


    void showProgressBar() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        //disposable.dispose();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    public void backPress() {
        ((IOTActivity_MainPage) getParentFragment().getParentFragment()).backPress();
    }

    /**
     * @param showNoDeviceView true: show no device view
     *                         false : hide no device view
     */
    void showHideNoDeviceView(boolean showNoDeviceView) {
        binding.progress.setVisibility(View.GONE);
        if (showNoDeviceView) {
            binding.pager.setVisibility(View.GONE);
            binding.topView.setVisibility(View.GONE);
            binding.noDeviceView.setVisibility(View.VISIBLE);
        } else {
            binding.pager.setVisibility(View.VISIBLE);
            binding.topView.setVisibility(View.VISIBLE);
            binding.noDeviceView.setVisibility(View.GONE);
        }
        binding.addDeviceBtn.setOnClickListener(v -> addDevice());

    }

    /**
     * add new device
     */
    void addDevice() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_IOTDeviceSetUpFragment);
    }

    /**
     * open vital screen
     */
    void Vitalrecord() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalMonitor);
    }

    public void onRefresh() {
        if (Constant.isvitalrecord.equals("1")){
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalMonitor);
        }else {
            getAndSetDeviceVector();
        }

    }

}