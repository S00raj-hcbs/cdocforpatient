package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Scale.Measurement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.BlueToothScanFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DEVICE;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DeviceConnectionStateChange;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DeviceNotify;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.MeasuringData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ResultData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StartDiscovery;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StopDiscovery;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ToResultFragment;
import com.cybermed.cdoc_patient.databinding.FragmentPulseOxiInstructionBinding;
import com.cybermed.cdoc_patient.databinding.FragmentScaleInstructionBinding;
import com.ihealth.communication.control.Hs4sControl;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SendVitalData;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.convertToGMT;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.fixMac;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTED;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.MEASURING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BtUtils.defaultEventBus;

public class iHealthScaleFragment extends BlueToothScanFragment {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


    private Hs4sControl hs4sControl;
    FragmentPulseOxiInstructionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pulse_oxi_instruction, container, false);
        binding.imgDevice.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.scale_instructions));
        binding.txtmessage.setText(getString(R.string.stand_weigh_scale));
        binding.btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToIot();
                // getActivity().onBackPressed();
            }
        });
        deviceName = DEVICE.SCALE;

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
        binding.searchingText.setText(getResources().getString(R.string.iot_reading_data));

    }

    @Override
    protected void timeOutViewSet() {
        Toast.makeText(getActivity(), getString(R.string.error_while_measuring), Toast.LENGTH_LONG).show();
        backToIot();
    }

    private void backToIot() {
        if (getParentFragment() != null)
            ((iHealthScaleBaseContainer) getParentFragment()).backToHome();
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeviceConnectionStateChange(DeviceConnectionStateChange deviceConnectionStateChange) {
        int status = deviceConnectionStateChange.getStatus();
        String mac = deviceConnectionStateChange.getMac();
        Toast.makeText(getActivity(), "status.." + status, Toast.LENGTH_LONG).show();
        if (status == 1) {
            connectedViewSet();
            state = CONNECTED;
            defaultEventBus().post(new StopDiscovery());
            Toast.makeText(getActivity(), "connected..", Toast.LENGTH_LONG).show();
            // get history data
            hs4sControl = iHealthDevicesManager.getInstance().getHs4sControl(mac);
            getOfflineData();
        } else if (status == 2) {
            connectingViewSet();
            Toast.makeText(getActivity(), "connecting..", Toast.LENGTH_LONG).show();
            if (state == MEASURING || state == RESULT)
                backToIot();

            state = CONNECTING;
            defaultEventBus().post(new StartDiscovery(deviceName));

        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeviceNotify(DeviceNotify deviceNotify) {
        String action = deviceNotify.getAction();
        String message = deviceNotify.getMessage();
        String mac = deviceNotify.getMac();

        JSONTokener jsonTokener = new JSONTokener(message);
        Toast.makeText(getActivity(), "status.." + action, Toast.LENGTH_LONG).show();
        switch (action) {
            case HsProfile.ACTION_HISTORICAL_DATA_HS:
                try {
                    JSONObject object = (JSONObject) jsonTokener.nextValue();
                    JSONArray jsonArray = object.getJSONArray(HsProfile.HISTORDATA_HS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //get the json data of each data point
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String dateString = jsonObject.getString(HsProfile.MEASUREMENT_DATE_HS);
                        double weight = jsonObject.getDouble(HsProfile.WEIGHT_HS);

                        //calling webservice to send vital data
                        SendVitalData("weight", String.valueOf(weight), convertToGMT(dateString), fixMac(mac));

                        //display only the last data of the array
                        if (i == jsonArray.length() - 1) {

                            Date dataDate = sdf.parse(dateString);
                            Date currDate = new Date();
                            long diffInMillies = Math.abs(currDate.getTime() - dataDate.getTime());

                            if (diffInMillies < 10000) {
                                weight = Math.floor(weight * 2.2046154 * 10) / 10.0;
                                state = RESULT;
                                RESULT.setValue(String.valueOf(weight));
                                if (getParentFragment() != null)
                                    ((iHealthScaleBaseContainer) getParentFragment()).openBG5Step3Fragment(true);
                                //action_iHealthScaleFragment_to_iHealthScaleResultFragment
                                //Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthScaleFragment_to_iHealthScaleResultFragment);
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                getOfflineData();
                break;

            case HsProfile.ACTION_NO_HISTORICALDATA:
                getOfflineData();
                break;
            case HsProfile.ACTION_ERROR_HS:
                binding.searchingText.setText("Error! \nPlease step on the scale again");

                getOfflineData();
                break;
            /*the following ACTION_LIVEDATA_HS && ACTION_ONLINE_RESULT_HS are not being called since
             * we keep calling historical data to get data generated before connecting to scale */
            case HsProfile.ACTION_LIVEDATA_HS:
                if (state != MEASURING) {
                    state = MEASURING;
                    //open Fragment
                    ((iHealthScaleBaseContainer) getParentFragment()).initNextBtn();
                    ///Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthScaleFragment_to_iHealthScaleMeasurementFragment);
                }

                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    double weight = Math.floor(jsonObject.getDouble(HsProfile.LIVEDATA_HS) * 2.2046154 * 10) / 10.0;

                    defaultEventBus().post(new MeasuringData(String.valueOf(weight), ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HsProfile.ACTION_ONLINE_RESULT_HS:
                state = RESULT;
                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    double weight = Math.floor(jsonObject.getDouble(HsProfile.WEIGHT_HS) * 2.2046154 * 10) / 10.0;

                    defaultEventBus().postSticky(new ResultData(String.valueOf(weight), "", ""));
                    defaultEventBus().post(new ToResultFragment());

                    SendVitalData("weight", String.valueOf(jsonObject.getDouble(HsProfile.WEIGHT_HS)), fixMac(mac));

                    Log.d("ScaleLiveDataResult", String.valueOf(weight));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                Log.d("ScaleAction", "Action: " + action);
                break;
        }
    }


    // this method is not being used since we no longer call online data
    private void startMeasuring() {
        if (hs4sControl != null)
            hs4sControl.measureOnline(2, 123);
    }

    private void getOfflineData() {
        if (hs4sControl != null)
            hs4sControl.getOfflineData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    public void disconnect() {
        if (hs4sControl != null) {
            hs4sControl.disconnect();
        }
    }

}
