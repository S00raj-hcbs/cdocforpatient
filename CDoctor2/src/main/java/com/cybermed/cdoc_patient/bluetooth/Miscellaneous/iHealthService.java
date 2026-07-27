package com.cybermed.cdoc_patient.bluetooth.Miscellaneous;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.SendIOTDataManager;
import com.ihealth.communication.control.Bp550BTControl;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.control.Hs4sControl;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.control.Po3Control;
import com.ihealth.communication.control.PoProfile;
import com.ihealth.communication.manager.DiscoveryTypeEnum;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class iHealthService extends Service {
    private static final String TAG = iHealthService.class.getSimpleName();

    private int mClientCallbackId;

//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//
//            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
//                        BluetoothAdapter.ERROR);
//                switch (state) {
//                    case BluetoothAdapter.STATE_OFF:
//                        // Bluetooth has been turned off;
//
//                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                        bluetoothAdapter.enable();
//                        Log.d(BluetoothRestartReceiver.class.getSimpleName(),"bluetooth enabled during receiver");
//
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_OFF:
//                        // Bluetooth is turning off;
//                        break;
//                    case BluetoothAdapter.STATE_ON:
//                        // Bluetooth has been on
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_ON:
//                        // Bluetooth is turning on
//                        break;
//                }
//            }
//        }
//    };

    @Override
    public void onCreate() {
        super.onCreate();

        /*
         * Initializes the iHealth devices manager. Can discovery available iHealth devices nearby
         * and connect these devices through iHealthDevicesManager.
         */
        iHealthDevicesManager.getInstance().init(this.getApplication(), Log.VERBOSE, Log.ASSERT);

        /*
         * Register callback to the manager. This method will return a callback Id.
         */
        mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);

        SharedPreferences mySharedPreferences = getSharedPreferences("preference", MODE_PRIVATE);
        long discoveryType = mySharedPreferences.getLong("discoveryType", 0);
        for (DeviceStruct struct : deviceStructList) {
            struct.isSelected = ((discoveryType & struct.type) != 0);
        }

        try {
            InputStream is = getAssets().open("com_cybermed_cdoc_patient_android.pem");
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


//        // Register for broadcasts on BluetoothAdapter state change
//        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        registerReceiver(mReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startDiscovery() {
        //old code ihealthlib2.4.3
        /*long type = iHealthDevicesManager.DISCOVERY_BP550BT | //Blood Pressure Monitor
                iHealthDevicesManager.DISCOVERY_PO3 | //Blood Oxygen
                iHealthDevicesManager.DISCOVERY_HS4S;// Scale
        iHealthDevicesManager.getInstance().startDiscovery(type);*/

        iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.BP550BT);//Blood Pressure Monitor
        iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.PO3);//Blood Oxygen
        iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.HS4S);// Scale
    }

    private void stopDiscovery() {
        iHealthDevicesManager.getInstance().stopDiscovery();

    }


    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onScanDevice(String mac, String deviceType, int rssi, Map manufactorData) {
            Log.i(TAG, "onScanDevice - mac:" + mac + " - deviceName:" + deviceType + " - rssi:" + rssi + " -manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
//            Message msg = new Message();
//            msg.what = HANDLER_SCAN;
//            msg.setData(bundle);
//            myHandler.sendMessage(msg);
//            if (manufactorData != null) {
//                Log.d(TAG, "onScanDevice mac suffix = " + manufactorData.get(HsProfile.SCALE_WIFI_MAC_SUFFIX));
//            }
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
                case "HS4S":
                    Hs4sControl hs4sControl = iHealthDevicesManager.getInstance().getHs4sControl(mac);
                    if (hs4sControl != null) {
                        hs4sControl.getOfflineData();
                        hs4sControl.measureOnline(1, 123);
                    }
                    break;
                case "PO3":
                    Po3Control mPo3Control = iHealthDevicesManager.getInstance().getPo3Control(mac);
                    if (mPo3Control != null) {
                        mPo3Control.startMeasure();
                    }
                    break;
            }

//            iHealthDevicesManager.getInstance().startDiscovery(128);

//            Message msg = new Message();
//            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
//                msg.what = HANDLER_CONNECTED;
//            } else if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
//                msg.what = HANDLER_DISCONNECT;
//            }
//            msg.setData(bundle);
//            myHandler.sendMessage(msg);

            //Intent intent = new Intent();
            //intent.setClass(BluetoothVitalService.this, BP550BT.class);
            //intent.putExtra("mac", mac);
            //startActivity(intent);
        }


        @Override
        public void onDeviceNotify(String mac, String deviceType,
                                   String action, String message) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceName: " + deviceType);
            Log.i(TAG, "action: " + action);
            Log.i(TAG, "message: " + message);

            switch (deviceType) {
                case "KN-550BT":
                    Bp550BTControl mBp550BTControl = iHealthDevicesManager.getInstance().getBp550BTControl(mac);
                    mBp550BTControl.disconnect();
                    break;
//                case "HS4S":
//                    Hs4sControl mHs4sControl = iHealthDevicesManager.getInstance().getHs4sControl(mac);
//                    mHs4sControl.disconnect();
//                    break;
//                case "PO3":
//                    Po3Control mPo3Control = iHealthDevicesManager.getInstance().getPo3Control(mac);
//                    mPo3Control.disconnect();
//                    break;
            }

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

                                sendPatVital("BP", hightPressure + ":" + lowPressure, date, fixMac(mac));

                            }
                        }

                        Message msg = new Message();
                        msg.what = HANDLER_MESSAGE;
                        msg.obj = str;
                        myHandler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case HsProfile.ACTION_HISTORICAL_DATA_HS:

                    try {
                        JSONObject object = (JSONObject) jsonTokener.nextValue();
                        JSONArray jsonArray = object.getJSONArray(HsProfile.HISTORDATA_HS);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            String dateString = jsonObject.getString(HsProfile.MEASUREMENT_DATE_HS);
                            float weight = (float) jsonObject.getDouble(HsProfile.WEIGHT_HS);
                            String dataId = jsonObject.getString(HsProfile.DATAID);
                            Log.d(TAG, "dataId:" + dataId + "--date:" + dateString + "-weight:" + weight);

                            sendPatVital("weight", String.valueOf(weight), dateString, fixMac(mac));
                        }
                        Message history = new Message();
                        history.what = 1;
                        history.obj = message;
                        myHandler.sendMessage(history);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_LIVEDATA_HS:
                    Log.d(TAG, "ACTION_LIVE_HS");
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        float weight = (float) jsonObject.getDouble(HsProfile.LIVEDATA_HS);
                        Log.d(TAG, "weight:" + weight);
                        Message value = new Message();
                        value.what = 1;
                        value.obj = "weight:" + weight;
                        myHandler.sendMessage(value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_ONLINE_RESULT_HS:
                    Log.d(TAG, "ACTION_ONLINE_RESULT_HS");
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        float weight = (float) jsonObject.getDouble(HsProfile.WEIGHT_HS);
                        String dataId = jsonObject.getString(HsProfile.DATAID);
                        Log.d(TAG, "dataId:" + dataId + "---weight:" + weight);
                        sendPatVital("weight", String.valueOf(weight), currentDateTime(), fixMac(mac));
                        Message result = new Message();
                        result.what = 1;
                        result.obj = "dataId:" + dataId + "---weight:" + weight;
                        myHandler.sendMessage(result);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
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
                            Log.i(TAG, "dataId:" + dataId + "--date:" + dateString + "--oxygen:" + oxygen + "--pulseRate:" + pulseRate
                                    + "-wave1:"
                                    + wave[0]
                                    + "-wave2:" + wave[1] + "--wave3:" + wave[2]);

                            sendPatVital("BO", String.valueOf(oxygen), dateString, fixMac(mac));
                            sendPatVital("HR", String.valueOf(pulseRate), dateString, fixMac(mac));

                        }
                        Message message2 = new Message();
                        message2.what = 1;
                        message2.obj = message;
                        myHandler.sendMessage(message2);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case PoProfile.ACTION_LIVEDA_PO:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        int oxygen = jsonObject.getInt(PoProfile.BLOOD_OXYGEN_PO);
                        int pulseRate = jsonObject.getInt(PoProfile.PULSE_RATE_PO);
                        float PI = (float) jsonObject.getDouble(PoProfile.PI_PO);
                        JSONArray jsonArray = jsonObject.getJSONArray(PoProfile.PULSE_WAVE_PO);
                        int[] wave = new int[3];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            wave[i] = jsonArray.getInt(i);
                        }
                        Log.i(TAG, "oxygen:" + oxygen + "--pulseRate:" + pulseRate + "--Pi:" + PI + "-wave1:" + wave[0]
                                + "-wave2:" + wave[1] + "--wave3:" + wave[2]);
                        Message message3 = new Message();
                        message3.what = 1;
                        message3.obj = message;
                        myHandler.sendMessage(message3);
//                        mBtnMeasurement.setEnabled(false);
//                        mBtnBattery.setEnabled(false);
//                        mBtnGetData.setEnabled(false);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
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
                        Log.i(TAG, "dataId:" + dataId + "--oxygen:" + oxygen + "--pulseRate:" + pulseRate + "--Pi:" + PI + "-wave1:" + wave[0]
                                + "-wave2:" + wave[1] + "--wave3:" + wave[2]);

                        sendPatVital("BO", String.valueOf(oxygen), currentDateTime(), fixMac(mac));
                        sendPatVital("HR", String.valueOf(pulseRate), currentDateTime(), fixMac(mac));

                        /*comment in order to compile*/
//                        Intent RTReturn = new Intent(TabletMainFragment.RECEIVE_DEVICE_DATA);
//                        RTReturn.putExtra("DEVICE_DATA", oxygen);
//                        LocalBroadcastManager.getInstance(iHealthService.this).sendBroadcast(RTReturn);

                        Message message3 = new Message();
                        message3.what = 1;
                        message3.obj = message;
                        myHandler.sendMessage(message3);

//                        mBtnMeasurement.setEnabled(true);
//                        mBtnBattery.setEnabled(true);
//                        mBtnGetData.setEnabled(true);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_ERROR_HS:
                    stopDiscovery();
                    startDiscovery();
//                    try {

//                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
//                        int err = jsonObject.getInt(HsProfile.ERROR_NUM_HS);
//                        Log.d(TAG, "weight:" + err);
//                        Message error = new Message();
//                        error.what = 1;
//                        error.obj = "err:" + err;
//                        myHandler.sendMessage(error);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    break;
            }
        }


        @Override
        public void onScanFinish() {
            super.onScanFinish();
            startDiscovery();
        }
    };

    private static final int HANDLER_MESSAGE = 101;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE:
                    Log.d(TAG, (String) msg.obj);
//                    sendPatVital("BP", bph + ":" + bpl, macAddress);
//
//                    tv_return.setText((String)msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private static class DeviceStruct {
        String name;
        long type;
        boolean isSelected;
    }

    private static ArrayList<DeviceStruct> deviceStructList = new ArrayList<>();

    static {
        Field[] fields = iHealthDevicesManager.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.contains("DISCOVERY_")) {
                DeviceStruct struct = new DeviceStruct();
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
        Log.i(TAG, "onDestroy() , service stopped...");
        stopDiscovery();

//        unregisterReceiver(mReceiver);
        /*
         * When the Activity is destroyed , need to call unRegisterClientCallback method to
         * unregister callback
         */
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
        /*
         * When the Activity is destroyed , need to call destroy method of iHealthDeivcesManager to
         * release resources
         */
        iHealthDevicesManager.getInstance().destroy();
    }

    String timestamp;
    private void sendPatVital(final String type, final String value, final String date, final String device_mac_address) {
        DateFormat of = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        try {
            Date dt = of.parse(date);
            timestamp = df.format(dt);

            Log.d(TAG, "timestamp: " + timestamp);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        SendIOTDataManager sendIOTDataManager=  new SendIOTDataManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                RESULT.setIndex("1");
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {

            }
        });
        sendIOTDataManager.callGetEnableLog(type, value, timestamp, device_mac_address, getMacAddr(), null, null, null);

    }


    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    static String fixMac(String inMac) {
        final StringBuilder b = new StringBuilder(18);
        for (int i = 0; i < inMac.length(); i++) {
            b.append(inMac.charAt(i));
            if (i % 2 == 1 && i != inMac.length() - 1) b.append(':');
        }
        return b.toString();
    }

    private static String currentDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(Calendar.getInstance().getTime());
    }
}
