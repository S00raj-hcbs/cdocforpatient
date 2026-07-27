package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.FragmentIotSetUpBinding;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.BP_DEVICE_NAME;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.BP_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.GLUCOMETER_DEVICE_NAME;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.GLUCOMETER_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.PO_DEVICE_NAME;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.PO_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SCALE_DEVICE_NAME;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SCALE_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.STEMOSCOPE_NAME;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.STEMOSCOPE_TYPE;


public class IOTDeviceSetUpFragment extends BaseFragment implements View.OnClickListener {


    public static String set_up_device_name = "";
    public static String set_up_device_type = "";
    public final static String SCAN_DEVICE_KEY = "scanDeviceKey";
    FragmentIotSetUpBinding binding;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_iot_set_up, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initContent();
    }

    public void initContent() {
        binding.iotSettingToolbar.txtTittle.setText(getString(R.string.rpm_add_new_device));
        binding.iotSettingToolbar.backBtn.setOnClickListener(view -> {
            if (Constant.isvitalnot.equals("1")){
                Constant.isvitalnot="2";
                Navigation.findNavController(view).navigate(R.id.action_IOTDeviceSetup_to_IOT_MainPage_Fragment);
            }else {
                Navigation.findNavController(view).navigate(R.id.action_IOTDeviceSetup_to_IOT_MainPage_Fragment);
            }

        });
        initEvent();
    }

    private void initEvent() {
        binding.btnNewGluco.setOnClickListener(this);
        binding.btnNewScale.setOnClickListener(this);
        binding.btnNewBp.setOnClickListener(this);
        binding.btnNewOxi.setOnClickListener(this);
        binding.tempStemoscope.setOnClickListener(this);
        binding.cardSmartwatch.setOnClickListener(this);

    }


    public void onClick(View view) {
        Intent iot_intent = null;
        String macAddress = "";

        switch (view.getId()) {
            case R.id.btn_new_gluco:
                set_up_device_name = GLUCOMETER_DEVICE_NAME;
                set_up_device_type = GLUCOMETER_DEVICE_TYPE;
                Navigation.findNavController(view).navigate(R.id.action_IOTDeviceSetUpFragment_to_iHealthBG5SetupFragment);
                break;
            case R.id.card_smartwatch:
                //startActivityForResult(new Intent(getActivity(), DeviceScanFragment.class), REQUEST_CODE_ADD_DEVICE);
                Navigation.findNavController(view).navigate(R.id.action_IOTDEVICE_Setup_Devicescan);
                break;
            case R.id.btn_new_scale:
                set_up_device_name = SCALE_DEVICE_NAME;
                set_up_device_type = SCALE_DEVICE_TYPE;
                Navigation.findNavController(view).navigate(R.id.action_IOTDeviceSetUpFragment_to_iHealthScaleSetupFragment);
                break;
            case R.id.btn_new_bp:
                set_up_device_name = BP_DEVICE_NAME;
                set_up_device_type = BP_DEVICE_TYPE;
                Bundle bp = new Bundle();
                bp.putString(SCAN_DEVICE_KEY, getResources().getString(R.string.iot_blood_pressure));
                Navigation.findNavController(view).navigate(R.id.action_IOTDeviceSetUpFragment_to_iHealthScannedDeviceFragment, bp);
                break;
            case R.id.btn_new_oxi:
                set_up_device_name = PO_DEVICE_NAME;
                set_up_device_type = PO_DEVICE_TYPE;
                Bundle oxi = new Bundle();
                oxi.putString(SCAN_DEVICE_KEY, getResources().getString(R.string.iot_pulse_oximeter));
                Navigation.findNavController(view).navigate(R.id.action_IOTDeviceSetUpFragment_to_iHealthScannedDeviceFragment, oxi);
                break;
            case R.id.tempStemoscope:
                set_up_device_name = STEMOSCOPE_NAME;
                set_up_device_type = STEMOSCOPE_TYPE;

                Navigation.findNavController(view).navigate(R.id.action_IOTDeviceSetUpFragment_to_QRCodeScannerFragment);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Navigation.findNavController(binding.getRoot()).navigate(R.id.action_IOTDeviceSetUpFragment_to_IOT_MainPage_Fragment);
    }
}
