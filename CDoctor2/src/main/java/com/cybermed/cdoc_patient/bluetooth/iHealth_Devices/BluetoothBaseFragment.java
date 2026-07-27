package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.annimon.stream.Stream;
import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.IoT_Device;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.SubjectInterface;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BlueToothService;

import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.SendIOTDataManager;
import com.ihealth.communication.control.Bg1Control;
import com.ihealth.communication.control.Bg1Profile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.stemoscope.stemolib.event.BlueToothStatusEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTED;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.MEASURING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.STRIP_IN;
import static com.cybermed.cdoc_patient.common.CDoctor2Application.application;
import static com.cybermed.cdoc_patient.common.CDoctor2Application.getTabletMode;


public class BluetoothBaseFragment extends BaseFragment implements SubjectInterface {

    public static final String SCALE_DEVICE_NAME = "HS4S", GLUCOMETER_DEVICE_NAME = "BG5", BP_DEVICE_NAME = "BP3L", PO_DEVICE_NAME = "PO3", STEMOSCOPE_NAME = "Stemoscope",
            SCALE_DEVICE_TYPE = "IChoice_Scale", GLUCOMETER_DEVICE_TYPE = "IChoice_Glucose", BP_DEVICE_TYPE = "IChoice_BP", PO_DEVICE_TYPE = "IChoice_Oximeter", STEMOSCOPE_TYPE = "Stemoscope";

    public static final String GLUCOSE_BG5 = "Glucose_BG5";
    public static final String SCALE = "Scale";
    public static final String BP = "BP";
    public static final String OXIMETER = "Oximeter";
    public static final String GLUCOSE_BG1 = "BG1";
    public static final String STEMOSCOPE = "Stemoscope";

    public static final String TIME_OUT = "TIME_OUT";

    public static final String IHEALTH_MAC_ADDR = "IHEALTH_MAC_ADDR";
    public static final String IOT_DEVICES = "IOT_DEVICES";

    protected final String TAG = getClass().getSimpleName();
    private CountDownTimer closePageTimer;
    protected boolean deviceScanned = false;
    protected String deviceMacAddr;

    private BlueToothService blueToothService;


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("BluetoothBaseActivity", "BLUETOOTH TURNING ON");
                        setBluetooth(true, context);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("BluetoothBaseActivity", "BLUETOOTH ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

    public Bg1Control mBg1Control;

    private boolean isGetStripInBg1 = false;
    private boolean isGetResultBg1 = false;
    private boolean isGetBloodBg1 = false;

    private static final String STATE = "state";

    protected final BehaviorSubject<STATE> BG1_Subject = BehaviorSubject.create();

    @NotNull
    @Override
    public Subject getSubject() {
        return BG1_Subject;
    }


    private void makeToast(String message) {
        if (message.contains("error"))
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    String mac;

    //this broadcastReceiver is for BG1
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {

                if (intent.hasExtra(STATE)) {
                    if (intent.getIntExtra(STATE, 0) == 0) {
                        mBg1Control.disconnect();
                        //trying to connect
                        BG1_Subject.onNext(CONNECTING);
                        //makeToast("headset out");
                    }
                    if (intent.getIntExtra(STATE, 0) == 1) {

                        //audio permission

                        Dexter.withActivity(getActivity())
                                .withPermission(Manifest.permission.RECORD_AUDIO)
                                .withListener(new BasePermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        mBg1Control.connect();
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        ErrorMessage.alertDialog(context, "Audio Permission Needed",
                                                "This permission is for glucometer", null);
                                    }
                                })
                                .check();
                    }
                }
            } else if (action.equals(Bg1Profile.ACTION_BG1_IDPS)) {

                // get Mac address in this method
                String idps = intent.getStringExtra(Bg1Profile.BG1_IDPS);

                //get the mac address from idps
                mac = fixMac(idps.substring(22, 22 + 10));

                //makeToast("idps =" + idps);

            } else if (action.equals(Bg1Profile.ACTION_BG1_CONNECT_RESULT)) {

                //flag determines connect or not
                int flag = intent.getIntExtra(Bg1Profile.BG1_CONNECT_RESULT, -1);
                if (flag == 0) {
                    //makeToast("connect success,please send code");

                    String account = CDoctor2Application.getLoginInfo().getAccount();

                    OnPostExecute ope = result -> {
                        if (result != null && result.toString().equals("1")) {
                            Toast.makeText(context, "Register device successfully!", Toast.LENGTH_SHORT).show();
                            mBg1Control.sendCode("", Bg1Profile.CODE_GDH, Bg1Profile.MEASURE_BLOOD);
                        } else {
                            makeToast("Register failed");
                            mBg1Control.disconnect();
                            BG1_Subject.onNext(CONNECTING);
                        }
                    };

                    Vector<IoT_Device> devices = CDoctor2Application.getLoginInfo().getUserInfo().getIoT_devices_obs().getValue();
                    boolean exist = false;

                    if (devices != null)
                        exist = Stream.of(devices)
                                .filter(iot -> iot.device_macAddress.equalsIgnoreCase(mac))
                                .count() > 0;

                    if (exist)
                        mBg1Control.sendCode("", Bg1Profile.CODE_GDH, Bg1Profile.MEASURE_BLOOD);
                    else
                        WebService.webServiceAsyncTask(WebServiceID.register_patient_IoT_device_V2, ope, account, GLUCOMETER_DEVICE_TYPE, mac, String.valueOf(true), GLUCOSE_BG1);

                } else {
                    makeToast("connect failed");
                    BG1_Subject.onNext(CONNECTING);
                    mBg1Control.disconnect();
                }
            } else if (action.equals(Bg1Profile.ACTION_BG1_SENDCODE_RESULT)) {
                //flag determines ready to measure or not
                int flag = intent.getIntExtra(Bg1Profile.BG1_SENDCODE_RESULT, -1);
                if (flag == 0) {
                    //makeToast("sendCode success,ready to  measure");
                    BG1_Subject.onNext(CONNECTED);
                } else {
                    //makeToast("sendCode failed");
                    BG1_Subject.onNext(CONNECTING);
                    mBg1Control.disconnect();
                }
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_ERROR)) {
                int errorNum = intent.getIntExtra(Bg1Profile.BG1_MEASURE_ERROR, -1);
                String error = intent.getStringExtra(Bg1Profile.BG1_MEASURE_ERROR_DESCRIPTION);
                makeToast("error information = " + error);

                //resend code to fix error 4
                if (errorNum == 4) {
                    mBg1Control.sendCode("", Bg1Profile.CODE_GDH, Bg1Profile.MEASURE_BLOOD);
                }

                //Pull-out bar is inserted again to be remeasured.
                //mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_5));
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_STRIP_IN)) {

                //keep track whether the strip is in
                if (!isGetStripInBg1) {
                    isGetStripInBg1 = true;
                    makeToast("Strip In");
                    BG1_Subject.onNext(STRIP_IN);

                    //for test case
//                    new Thread(()->{
//                        try {
//                            Thread.sleep(3000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                        runOnUiThread(()->{
//                            int measureResult = 90;
//
//                            SendVitalData("Glucose", String.valueOf(measureResult), mac);
//                            RESULT.setValue(String.valueOf(measureResult));
//                            BG1_Subject.onNext(RESULT);
//                        });
//                    }).start();
                    //Please drop blood
                    //mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_3));
                }
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetStripInBg1 = false;
                    }
                }.start();
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_GET_BLOOD)) {

                if (!isGetBloodBg1) {
                    isGetBloodBg1 = true;

                    makeToast("Get Blood");
                    BG1_Subject.onNext(MEASURING);
                    //Checking, please wait.
                    //mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_4));
                }
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetBloodBg1 = false;
                    }
                }.start();
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_RESULT)) {
                if (!isGetResultBg1) {
                    isGetResultBg1 = true;

                    //get the data
                    int measureResult = intent.getIntExtra(Bg1Profile.BG1_MEASURE_RESULT, -1);
                    String dataId = intent.getStringExtra(Bg1Profile.DATA_ID);
                    //makeToast("dataId = " + dataId);
                    //makeToast("msgResult = " + measureResult);

                    SendVitalData("Glucose", String.valueOf(measureResult), mac);


                    RESULT.setValue(String.valueOf(measureResult));
                    BG1_Subject.onNext(RESULT);
                }

                //Pull-out bar is inserted again to be remeasured.
                //mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_5));
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetResultBg1 = false;
                    }
                }.start();

            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_STRIP_OUT)) {
                makeToast("Strip Out");
                BG1_Subject.onNext(CONNECTED);

                //please plug strip
                //mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_2));
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_STANDBY)) {
                ErrorMessage.alertDialog(context, "An error has occurred", "Please unplug and plug the glucometer again", null);
                mBg1Control.disconnect();
                BG1_Subject.onNext(CONNECTING);

                if (!isGetResultBg1) {
                    isGetResultBg1 = true;
                }
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetResultBg1 = false;
                    }
                }.start();
            }
        }
    };


    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        filter.addAction(Bg1Profile.ACTION_BG1_DEVICE_READY);
        filter.addAction(Bg1Profile.ACTION_BG1_IDPS);
        filter.addAction(Bg1Profile.ACTION_BG1_CONNECT_RESULT);
        filter.addAction(Bg1Profile.ACTION_BG1_SENDCODE_RESULT);

        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_ERROR);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_STRIP_IN);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_STRIP_OUT);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_GET_BLOOD);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_RESULT);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_STANDBY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getActivity().registerReceiver(mBroadcastReceiver, filter,Context.RECEIVER_EXPORTED);
        }else {
            getActivity().registerReceiver(mBroadcastReceiver, filter);
        }
    }


    public static boolean setBluetooth(boolean enable, Context context) {
        //Bug where after a period of time, bluetooth scan doesnt work
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }


    private void closePageTimer() {
        closePageTimer = new CountDownTimer(600000, 1000) {
            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {

            }

        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregister for bluetooth service
        blueToothService.unregisterCallBack();
        EventBus.getDefault().unregister(blueToothService);

        getActivity().unregisterReceiver(mReceiver);
        if (closePageTimer != null)
            closePageTimer.cancel();
    }

    /*Utility Code*/

    public static void SendVitalData(String device_type, String value, String device_mac_address) {

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timestamp = df.format(Calendar.getInstance().getTime());

        SendVitalData(device_type, value, timestamp, device_mac_address);
    }

    public static void SendVitalData(String device_type, String value, String timestamp, String device_mac_address) {
//        WebServiceID.send_patient_vital_data.setDisableNullRestriction(true);
//        OnPostExecute ope = result -> {
//            RESULT.setIndex(result.toString());
//        };
       // WebService.webServiceAsyncTask(WebServiceID.send_patient_vital_data, ope, device_type, value, timestamp, device_mac_address, getMacAddr(), "", "", "");
        SendIOTDataManager sendIOTDataManager=  new SendIOTDataManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                RESULT.setIndex("1");
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {

            }
        });
        sendIOTDataManager.callGetEnableLog(device_type, value, timestamp,
                device_mac_address, getMacAddr(), null, null, null);
    }

    public static String fixMac(String inMac) {
        final StringBuilder b = new StringBuilder(18);
        for (int i = 0; i < inMac.length(); i++) {
            b.append(inMac.charAt(i));
            if (i % 2 == 1 && i != inMac.length() - 1) b.append(':');
        }
        return b.toString();
    }

    public static String currentDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(Calendar.getInstance().getTime());
    }

    public static String convertToGMT(String dateTime) {
        try {
            DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            TimeZone gmtTime = TimeZone.getTimeZone("GMT");
            gmtFormat.setTimeZone(gmtTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            sdf.setTimeZone(TimeZone.getDefault());
            return gmtFormat.format(sdf.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return currentDateTime();
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

    public static int mainID(String device_type) {
        switch (device_type) {
            case OXIMETER:
                return getTabletMode() ? R.id.action_iHealthPulseOxiResultFragment_to_tabletMainFragment
                        : R.id.action_iHealthPulseOxiResultFragment_to_IOT_MainPage_Fragment;
            case BP:
                return getTabletMode() ? R.id.action_iHealthBPResultFragment_to_tabletMainFragment
                        : R.id.action_iHealthBPResultFragment_to_IOT_MainPage_Fragment;
            case SCALE:
                return getTabletMode() ? R.id.action_iHealthScaleResultFragment_to_tabletMainFragment
                        : R.id.action_iHealthScaleResultFragment_to_IOT_MainPage_Fragment;
            case GLUCOSE_BG5:
            case GLUCOSE_BG1:
                return getTabletMode() ? R.id.action_iHealthBG5ResultFragment_to_tabletMainFragment
                        : R.id.action_iHealthBG5ResultFragment_to_IOT_MainPage_Fragment;
            case TIME_OUT:
                return getTabletMode() ? R.id.action_bluetoothTimeoutFragment_to_tabletMainFragment
                        : R.id.action_bluetoothTimeoutFragment_to_IOT_MainPage_Fragment;
        }

        return 0;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveConnectedStatus(BlueToothStatusEvent event) {
        String cStatus = event.getStatus() == 1 ? "Connected" : "Disconnected";
        if (event.getStatus() == 1) {
            Constant.IS_STEMO_CONNECTED = true;
        } else
            Constant.IS_STEMO_CONNECTED = false;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        return null;
    }

    public void init() {
        // register for bluetooth service
        checkAndRequestPermissions();
        blueToothService = new BlueToothService(requireActivity(),application);
        blueToothService.registerCallBack();
        EventBus.getDefault().register(blueToothService);
        setBluetooth(true,requireActivity());
//        Casv wv `WŠV SVWVV          2354SAW agv swgs`wvvt5```2bsw2v    w` v ` svcw    `wsv    `wswssw``G```VC``   V       ASWV`W  gsw`Ç                 SVW1```               5W25GV B51VYE4e1e4
        //WebServiceID.send_patient_vital_data.setDisableNullRestriction(true);

        //register receiver for unable to turn off bluetooth
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getActivity().registerReceiver(mReceiver, filter,Context.RECEIVER_EXPORTED);
        }else {
            getActivity().registerReceiver(mReceiver, filter);
        }


        mBg1Control = Bg1Control.getInstance();
        mBg1Control.init(getActivity(), "", 0x00FF1304, true);

        registerBroadcast();

        closePageTimer();
    }
    private void checkAndRequestPermissions() {
        String[] permissions ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissions = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }else {
            permissions = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }


        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), listPermissionsNeeded.toArray(new String[0]), 100);
        }
    }
    @Override
    protected void initLayout(View view) {

    }
}
