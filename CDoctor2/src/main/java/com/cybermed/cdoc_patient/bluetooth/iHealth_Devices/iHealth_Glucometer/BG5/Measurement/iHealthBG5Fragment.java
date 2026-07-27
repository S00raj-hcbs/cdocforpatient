package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.BG5.Measurement;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SendVitalData;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.convertToGMT;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.fixMac;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTED;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.MEASURING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;
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
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DEVICE;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DeviceConnectionStateChange;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DeviceNotify;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ResultData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StopDiscovery;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ToResultFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;
import com.cybermed.cdoc_patient.databinding.FragmentPulseOxiInstructionBinding;
import com.ihealth.communication.control.Bg5Control;
import com.ihealth.communication.control.Bg5Profile;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class iHealthBG5Fragment extends BlueToothScanFragment {
    private Bg5Control mBG5Control;
    FragmentPulseOxiInstructionBinding binding;
    IBackPressFrag iBackPressListner;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pulse_oxi_instruction, container, false);
        context = getContext();
        binding.imgDevice.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rpm_bg5_strip_in));
        binding.txtmessage.setText(getString(R.string.iot_glucose_instruction));
        deviceName = DEVICE.GLUCOMETER;
        binding.btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iBackPressListner.backPress();
            }
        });
        return binding.getRoot();
    }


    @Override
    protected void scanningViewSet() {
        binding.searchingText.setText(getResources().getString(R.string.iot_searching));

    }

    @Override
    protected void connectingViewSet() {
        binding.searchingText.setText(getResources().getString(R.string.iot_connecting));
    }

    @Override
    protected void connectedViewSet() {
        binding.searchingText.setText(getResources().getString(R.string.iot_reading_data));

    }

    @Override
    protected void timeOutViewSet() {
        Toast.makeText(getActivity(), getString(R.string.error_while_measuring), Toast.LENGTH_LONG).show();
        iBackPressListner.backPress();
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeviceConnectionStateChange(DeviceConnectionStateChange deviceConnectionStateChange) {
        int status = deviceConnectionStateChange.getStatus();
        String mac = deviceConnectionStateChange.getMac();

        if (status == 1) {
            connectedViewSet();
            state = CONNECTED;
            defaultEventBus().post(new StopDiscovery());

            mBG5Control = iHealthDevicesManager.getInstance().getBg5Control(mac);
            if (mBG5Control != null)
                mBG5Control.setTime();
        } else if (status == 2) {
            connectingViewSet();
            state = CONNECTING;
            // defaultEventBus().post(new StartDiscovery(deviceName));
        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeviceNotify(DeviceNotify deviceNotify) {
        String action = deviceNotify.getAction();
        String message = deviceNotify.getMessage();
        String mac = deviceNotify.getMac();

        switch (action) {
            case Bg5Profile.ACTION_START_MEASURE:
                break;
            case Bg5Profile.ACTION_STRIP_IN:
                state = MEASURING;
                iBackPressListner.moveToNext();
                break;
            case Bg5Profile.ACTION_GET_BLOOD:
                break;
            case Bg5Profile.ACTION_SET_UNIT:
                mBG5Control.setBottleMessageWithInfo(Bg5Profile.STRIP_GDH, Bg5Profile.MEASURE_BLOOD, "", 255, "2050-1-1");
                break;
            case Bg5Profile.ACTION_SET_TIME:
                mBG5Control.setUnit(2);
                break;
            case Bg5Profile.ACTION_SET_BOTTLE_MESSAGE_SUCCESS:
                mBG5Control.startMeasure(Bg5Profile.MEASURE_BLOOD);
                break;
            case Bg5Profile.ACTION_ONLINE_RESULT_BG:
                state = RESULT;
                try {
                    JSONObject info = new JSONObject(message);
                    String glucose = info.getString(Bg5Profile.ONLINE_RESULT_BG);
                    String dateString = info.getString(Bg5Profile.MEASUREMENT_DATE_BG);

                    defaultEventBus().postSticky(new ResultData(glucose, "", ""));
                    defaultEventBus().post(new ToResultFragment());

                    SendVitalData("Glucose", glucose, convertToGMT(dateString), fixMac(mac));
                    disconnect();
                } catch (JSONException e) {
                    e.printStackTrace();
                    iBackPressListner.backPress();
                    Toast.makeText(getActivity(), getString(R.string.error_while_measuring), Toast.LENGTH_LONG).show();
                }
                break;
            case Bg5Profile.ACTION_ERROR_BG:
                Toast.makeText(getActivity(), getString(R.string.error_while_measuring), Toast.LENGTH_LONG).show();
                iBackPressListner.backPress();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    public void disconnect() {
        if (mBG5Control != null)
            mBG5Control.disconnect();
    }

    public void setBackPressListner(IBackPressFrag iBackPressListner) {
        this.iBackPressListner = iBackPressListner;
    }

}
