package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Blood_pressure;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SendVitalData;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.fixMac;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTED;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.MEASURING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BtUtils.defaultEventBus;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.BlueToothScanFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.OnBackPressedListener;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DEVICE;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DeviceConnectionStateChange;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DeviceNotify;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.MeasuringData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ResultData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StartMeasuring;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StopDiscovery;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StopMeasuring;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ToResultFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;
import com.cybermed.cdoc_patient.databinding.FragmentScanDeviceBinding;
import com.ihealth.communication.control.Bp3lControl;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class iHealthBp3LFragment extends BlueToothScanFragment implements OnBackPressedListener {

    Bp3lControl mBp3lControl;
    FragmentScanDeviceBinding binding;
    Context context;
    IBackPressFrag iBackPressListner;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_device, container, false);
        binding.layToolbar.relativeParent.setVisibility(View.GONE);
        context = getActivity();
        deviceName = DEVICE.BLOOD_PRESSURE_MONITOR;
        binding.btnQuit.setOnClickListener(v -> {
            backToIotListPage();
        });


        return binding.getRoot();
    }


    @Override
    protected void scanningViewSet() {

    }

    @Override
    protected void connectingViewSet() {
        binding.btnQuit.setEnabled(false);
        binding.btnQuit.setTextColor(ContextCompat.getColor(context, R.color.disableBlueButton));
        binding.searchingText.setText(getResources().getString(R.string.iot_connecting));

    }

    @Override
    protected void connectedViewSet() {
        binding.relativeProgress.setVisibility(View.GONE);
        binding.searchingText.setText(getString(R.string.blood_presure_cuffs));
        binding.searchingText.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                ContextCompat.getDrawable(context, R.drawable.cuff_instructions), null, null);
        binding.txtLabel.setText(getString(R.string.wear_blood_presure));
        binding.btnQuit.setVisibility(View.GONE);
        binding.btnStartMeasure.setVisibility(View.VISIBLE);
        binding.btnStartMeasure.setText(getString(R.string.start_measure));
        binding.btnStartMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iBackPressListner.moveToNext();
            }
        });
    }

    @Override
    protected void timeOutViewSet() {
        Toast.makeText(getActivity(), getString(R.string.error_while_measuring), Toast.LENGTH_LONG).show();
        backToIotListPage();
    }

    void backToIotListPage() {
        iBackPressListner.backPress();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void stopMeasure(StopMeasuring stopMeasuring) {
        if (mBp3lControl != null) {
            mBp3lControl.interruptMeasure();
            mBp3lControl.disconnect();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void startMeasure(StartMeasuring startMeasuring) {
        if (state != MEASURING && mBp3lControl != null) {
            state = MEASURING;
            //open Fragment
            mBp3lControl.startMeasure();

        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeviceConnectionStateChange(DeviceConnectionStateChange deviceConnectionStateChange) {
        String mac = deviceConnectionStateChange.getMac();
        int status = deviceConnectionStateChange.getStatus();

        if (status == 1) {
            connectedViewSet();
            state = CONNECTED;
            defaultEventBus().post(new StopDiscovery());
        } else {
            connectingViewSet();
            if (status == 2) {
                state = CONNECTING;
                //defaultEventBus().post(new StartDiscovery(deviceName));
            }
        }

        mBp3lControl = iHealthDevicesManager.getInstance().getBp3lControl(mac);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeviceNotify(DeviceNotify deviceNotify) {
        String action = deviceNotify.getAction();
        String message = deviceNotify.getMessage();
        String mac = deviceNotify.getMac();


        switch (action) {
            case BpProfile.ACTION_ONLINE_RESULT_BP:
                try {
                    defaultEventBus().post(new ToResultFragment());

                    JSONObject info = new JSONObject(message);
                    String highPressure = info.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                    String lowPressure = info.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                    String pulse = info.getString(BpProfile.PULSE_BP);

                    defaultEventBus().postSticky(new ResultData(highPressure, lowPressure, pulse));
                    SendVitalData("BP", highPressure + ":" + lowPressure + ":" + pulse, fixMac(mac));
                    disconnect();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case BpProfile.ACTION_ONLINE_PULSEWAVE_BP:

            case BpProfile.ACTION_ONLINE_PRESSURE_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    defaultEventBus().post(new MeasuringData(pressure, ""));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case BpProfile.ACTION_ERROR_BP:
                backToIotListPage();
                Toast.makeText(getActivity(), getString(R.string.error_while_measuring), Toast.LENGTH_LONG).show();
                //defaultEventBus().post(new MeasuringData("Error! \nPlease stay still while measuring. Click stop and try again", ""));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    public void disconnect() {
        if (mBp3lControl != null) {
            mBp3lControl.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        backToIotListPage();
    }

    public void setBackPressListner(IBackPressFrag iBackPressListner) {
        this.iBackPressListner = iBackPressListner;
    }
}
