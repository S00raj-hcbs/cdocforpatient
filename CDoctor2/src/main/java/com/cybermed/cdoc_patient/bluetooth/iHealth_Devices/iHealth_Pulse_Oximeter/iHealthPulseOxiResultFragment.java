package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Pulse_Oximeter;


import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.PO_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT1;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT2;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.TIMESTAMP;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.VALUE;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.EventBusFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.OnBackPressedListener;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ResultData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;
import com.cybermed.cdoc_patient.databinding.FragmentPulseOxiResultBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class iHealthPulseOxiResultFragment extends EventBusFragment implements OnBackPressedListener {

    //action_iHealthPulseOxiResultFragment_to_IOT_MainPage_Fragment


    private String hr = null;
    private String bo = null;
    FragmentPulseOxiResultBinding binding;
    IBackPressFrag iBackPressListner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pulse_oxi_result, container, false);
        binding.layResultbp.labeldia.setText(getString(R.string.heart_rate));
        binding.layResultbp.labelSys.setText(getString(R.string.bo));
        binding.layResultbp.txtVal.setText(getString(R.string.vitals_bpm));
        binding.layResultbp.txtDia.setText("--");
        binding.layResultbp.txtSys.setText("--");
        binding.btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToMain();
            }
        });
        return binding.getRoot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayData(ResultData resultData) {
        hr = resultData.getData1();
        bo = resultData.getData2();

        binding.layResultbp.txtDia.setText(hr);
        binding.layResultbp.txtSys.setText(bo + "%");

    }


    @Override
    public void onBackPressed() {
        goBackToMain();
    }

    private void goBackToMain() {

        if (hr == null || bo == null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.error_dialog_title))
                    .setMessage(getString(R.string.iot_remove_oximeter))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }
        if ((iHealthPulseOxiBaseContainer) getParentFragment() != null)
            ((iHealthPulseOxiBaseContainer) getParentFragment()).disconnectDevice();
        // int ID = mainID(BluetoothBaseFragment.OXIMETER);

        Bundle args = new Bundle();
        args.putString(VALUE, PO_DEVICE_TYPE);
        args.putString(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        args.putString(MEASUREMENT1, hr);
        args.putString(MEASUREMENT2, bo);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthPulseOxiFragment_to_IOTGraph, args);


    }
    public void setBackPressListner(IBackPressFrag iBackPressListner) {
        this.iBackPressListner = iBackPressListner;
    }
}