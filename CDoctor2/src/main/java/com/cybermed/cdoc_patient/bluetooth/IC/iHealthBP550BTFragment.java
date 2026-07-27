package com.cybermed.cdoc_patient.bluetooth.IC;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment;
import com.ihealth.communication.control.Bp550BTControl;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.DiscoveryTypeEnum;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

public class iHealthBP550BTFragment extends BluetoothBaseFragment implements View.OnClickListener {
    private static final String TAG = iHealthBP550BTFragment.class.getSimpleName();

    //    implements iHealthPulseOxiMeasurementFragment.OnFragmentInteractionListener {
    private FrameLayout fragmentContainer;
    private int mClientCallbackId;
//    private iHealthPulseOxiMeasurementFragment iHealthPulseOxiMeasurementFragment;
//    public iHealthBPResultFragment iHealthBPResultFragment;
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_health_bp550bt);

        fragmentContainer = findViewById(R.id.fragment_container);
        findViewById(R.id.btn_quit).setOnClickListener(this);
//        iHealthPulseOxiMeasurementFragment = new iHealthPulseOxiMeasurementFragment();
//        iHealthBPResultFragment = new iHealthBPResultFragment();
        iHealthDevicesManager.getInstance().init(this, Log.VERBOSE, Log.ASSERT);
        mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);

        SharedPreferences mySharedPreferences = getSharedPreferences("preference", MODE_PRIVATE);
        long discoveryType = mySharedPreferences.getLong("discoveryType", 0);
        for (iHealthBP550BTActivity.DeviceStruct struct : deviceStructList) {
            struct.isSelected = ((discoveryType & struct.type) != 0);
        }

        try {
            InputStream is = getAssets().open("com_cybermed_cdoc_android.pem");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            boolean isPass = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer);
            Log.i("info", "isPass:    " + isPass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        startDiscovery();
    }*/

//    @Override
    public void startDiscovery() {
        //old ihealthlib_2.4.3
        /*long type = iHealthDevicesManager.DISCOVERY_BP550BT;
        iHealthDevicesManager.getInstance().startDiscovery(type);*/
        iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.BP550BT);
    }




    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onScanDevice(String mac, String deviceType, int rssi, Map manufactorData) {
            Log.i(TAG, "onScanDevice - mac:" + mac + " - deviceName:" + deviceType + " - rssi:" + rssi + " -manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
//            stopDiscovery();
            boolean req = iHealthDevicesManager.getInstance().connectDevice("test@com.com", mac, deviceType);
            if (!req) {
                Log.d("ihealthdebug", "Haven’t permission to connect this device or the mac is not valid");
            }
        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID, Map manufactorData) {
            Log.e(TAG, "mac:" + mac + " deviceName:" + deviceType + " status:" + status + " errorid:" + errorID + " -manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);

            switch (deviceType) {
                case "KN-550BT":
                    Bp550BTControl bp550BTControl = iHealthDevicesManager.getInstance().getBp550BTControl(mac);
                    if (bp550BTControl != null) {
                        bp550BTControl.getOfflineData();
                    }
                    break;
            }
        }


        @Override
        public void onDeviceNotify(String mac, String deviceType,
                                   String action, String message) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceName: " + deviceType);
            Log.i(TAG, "action: " + action);
            Log.i(TAG, "message: " + message);


            JSONTokener jsonTokener = new JSONTokener(message);

            switch (action) {
                case BpProfile.ACTION_HISTORICAL_DATA_BP:

                    String str = "";

                    Log.d(TAG, "on device notify");
                    try {
                        JSONObject info = new JSONObject(message);
                        if (info.has(BpProfile.HISTORICAL_DATA_BP)) {
                            JSONArray array = info.getJSONArray(BpProfile.HISTORICAL_DATA_BP);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String date = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                                String hightPressure = obj.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                                String lowPressure = obj.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                                String pulseWave = obj.getString(BpProfile.PULSE_BP);
                                String ahr = obj.getString(BpProfile.MEASUREMENT_AHR_BP);
                                str = "date:" + date
                                        + "hightPressure:" + hightPressure + "\n"
                                        + "lowPressure:" + lowPressure + "\n"
                                        + "pulseWave" + pulseWave + "\n"
                                        + "ahr:" + ahr + "\n";
                                Log.d(TAG, "DATA:" + str);
                                SendVitalData("BP", hightPressure + ":" + lowPressure, date, fixMac(mac));
//                                iHealthBPResultFragment fragment = iHealthBPResultFragment.newInstance(hightPressure,lowPressure);
//                                Fragment hm = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
//                                if (hm == null) {
////                                    openFragment(fragment);
//                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }


        @Override
        public void onScanFinish() {
            super.onScanFinish();
            startDiscovery();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_quit:
                getActivity().finish();
                break;
        }
    }

    private static class DeviceStruct {
        String name;
        long type;
        boolean isSelected;
    }

    private static ArrayList<iHealthBP550BTFragment.DeviceStruct> deviceStructList = new ArrayList<>();

    static {
        Field[] fields = iHealthDevicesManager.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.contains("DISCOVERY_")) {
                iHealthBP550BTFragment.DeviceStruct struct = new iHealthBP550BTFragment.DeviceStruct();
                struct.name = fieldName.substring(10);
                try {
                    struct.type = field.getLong(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                deviceStructList.add(struct);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy() , service stopped...");
//        stopDiscovery();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
        iHealthDevicesManager.getInstance().destroy();
    }

}
