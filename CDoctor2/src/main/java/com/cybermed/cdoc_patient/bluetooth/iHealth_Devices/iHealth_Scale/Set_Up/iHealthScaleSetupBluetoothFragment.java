package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Scale.Set_Up;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTDeviceSetUpFragment;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentBg5BluetoothSetupBinding;
import com.cybermed.cdoc_patient.databinding.FragmentScaleBluetoothSetupBinding;

public class iHealthScaleSetupBluetoothFragment extends BaseFragment {


    int GO_TO_BLUETOOTH_SETTING = 14;

    FragmentBg5BluetoothSetupBinding binding;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bg5_bluetooth_setup, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initView();
    }

    public void initView() {
        binding.txtDesc.setText(getString(R.string.iot_scale_bluetooth_pair));
        binding.toolbar.txtTittle.setText(getString(R.string.iot_scale));
        binding.toolbar.backBtn.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthScaleSetupBluetoothFragment_to_iHealthScaleSetupFragment));
        clickListener();
    }

    private void clickListener() {
        binding.btnPhoneSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS), GO_TO_BLUETOOTH_SETTING);

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GO_TO_BLUETOOTH_SETTING && getView() != null) {
            Bundle b = new Bundle();
            b.putString(IOTDeviceSetUpFragment.SCAN_DEVICE_KEY, getResources().getString(R.string.iot_scale));
            Navigation.findNavController(getView()).navigate(R.id.action_iHealthScaleSetupBluetoothFragment_to_iHealthScannedDeviceFragment, b);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }
}