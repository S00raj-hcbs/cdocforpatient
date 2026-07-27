package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Pulse_Oximeter;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SendVitalData;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.convertToGMT;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.currentDateTime;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.fixMac;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTED;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.MEASURING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.SCANNING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BtUtils.defaultEventBus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.BlueToothScanFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DEVICE;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DeviceConnectionStateChange;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DeviceNotify;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ResultData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StopDiscovery;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;
import com.cybermed.cdoc_patient.databinding.FragmentPulseOxiInstructionBinding;
import com.ihealth.communication.control.Po3Control;
import com.ihealth.communication.control.PoProfile;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

//need to be changed to fragment
public class iHealthPulseOxiFragment extends BlueToothScanFragment {

    private Po3Control mPo3Control;
    FragmentPulseOxiInstructionBinding binding;
    IBackPressFrag iBackPressListner;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pulse_oxi_instruction, container, false);


        deviceName = DEVICE.PULSE_OXIMETER;
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

    }

    @Override
    protected void connectingViewSet() {
        binding.searchingText.setText(getResources().getString(R.string.iot_connecting));

    }


    @Override
    protected void connectedViewSet() {
        binding.searchingText.setText(getString(R.string.iot_reading_data));

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
        //Connected
        if (status == 1) {
            connectedViewSet();
            state = CONNECTED;
            defaultEventBus().post(new StopDiscovery());

            // get history data
            mPo3Control = iHealthDevicesManager.getInstance().getPo3Control(mac);

                if (mPo3Control != null) {
                    mPo3Control.getHistoryData();
                    mPo3Control.startMeasure();
                }

        } else if (status == 2) {
            scanningViewSet();
            state = SCANNING;
            // defaultEventBus().post(new StartDiscovery(deviceName));
        }

    }

    //
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeviceNotify(DeviceNotify deviceNotify) {
        String action = deviceNotify.getAction();
        String message = deviceNotify.getMessage();
        String mac = deviceNotify.getMac();

        JSONTokener jsonTokener = new JSONTokener(message);

        switch (action) {
            case PoProfile.ACTION_OFFLINEDATA_PO:
                try {
                    JSONObject object = (JSONObject) jsonTokener.nextValue();
                    JSONArray jsonArray = object.getJSONArray(PoProfile.OFFLINEDATA_PO);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String dataId = jsonObject.getString(PoProfile.DATAID);
                        String dateString = jsonObject.getString(PoProfile.MEASURE_DATE_PO);
                        int oxygen = jsonObject.getInt(PoProfile.BLOOD_OXYGEN_PO);
                        int pulseRate = jsonObject.getInt(PoProfile.PULSE_RATE_PO);
                        JSONArray jsonArray1 = jsonObject.getJSONArray(PoProfile.PULSE_WAVE_PO);
                        int[] wave = new int[jsonArray1.length()];
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            wave[j] = jsonArray1.getInt(j);
                        }

                        SendVitalData("BO", String.valueOf(oxygen), convertToGMT(dateString), fixMac(mac));
                        SendVitalData("HR", String.valueOf(pulseRate), convertToGMT(dateString), fixMac(mac));
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case PoProfile.ACTION_LIVEDA_PO:
                if (state != MEASURING) {
                    state = MEASURING;
                    //open Fragment
                    iBackPressListner.moveToNext();
                }
                break;
            case PoProfile.ACTION_RESULTDATA_PO:
                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    String dataId = jsonObject.getString(PoProfile.DATAID);
                    int oxygen = jsonObject.getInt(PoProfile.BLOOD_OXYGEN_PO);
                    int pulseRate = jsonObject.getInt(PoProfile.PULSE_RATE_PO);
                    float PI = (float) jsonObject.getDouble(PoProfile.PI_PO);
                    JSONArray jsonArray = jsonObject.getJSONArray(PoProfile.PULSE_WAVE_PO);
                    int[] wave = new int[3];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        wave[i] = jsonArray.getInt(i);
                    }

                    String pulseRateResult = String.valueOf(pulseRate);
                    String oxygenResult = String.valueOf(oxygen);

                    if (defaultEventBus().hasSubscriberForEvent(ResultData.class)) {
                        state = RESULT;
                        defaultEventBus().post(new ResultData(pulseRateResult, oxygenResult, ""));
                        SendVitalData("BO", String.valueOf(oxygen), currentDateTime(), fixMac(mac));
                        SendVitalData("HR", String.valueOf(pulseRate), currentDateTime(), fixMac(mac));
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_while_measuring), Toast.LENGTH_LONG).show();
                        iBackPressListner.backPress();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    void disconnect() {
        if (mPo3Control != null) {
            mPo3Control.disconnect();
        }
    }

    public void setBackPressListner(IBackPressFrag iBackPressListner) {
        this.iBackPressListner = iBackPressListner;
    }

}
