package com.cybermed.cdoc_patient.bluetooth;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.cybermed.cdoc_patient.bluetooth.contour_services.BGMeterGattService;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.SendIOTDataManager;

import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ContourService extends Service {
    private final static String TAG = ContourService.class.getSimpleName();

    String mDeviceAddress = "00:00:00:00:00:00";
    BGMeterGattService mBGMeterGattService;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothLeScanner mBluetoothLeScanner = null;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBGMeterGattService = ((BGMeterGattService.LocalBinder) service).getService();
            if (!mBGMeterGattService.initialize()) {
                Log.d(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBGMeterGattService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBGMeterGattService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BGMeterGattService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "contour connected");
            } else if (BGMeterGattService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "contour disconnected");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBluetoothLeScanner.startScan(mLeScanCallback);
                }
            } else if (BGMeterGattService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG,"Data: " + intent.getStringExtra(BGMeterGattService.EXTRA_DATA));
                sendPatVital("Glucose",intent.getStringExtra(BGMeterGattService.EXTRA_DATA), currentDateTime(), mDeviceAddress);
            }
        }
    };

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback mLeScanCallback =
            new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    Log.d(TAG, "Device: " + result.getDevice().getName());
                    if (result.getDevice().getName() != null && result.getDevice().getName().toLowerCase().contains("contour")) {

                        mBluetoothLeScanner.stopScan(mLeScanCallback);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter(),Context.RECEIVER_EXPORTED);
                        }else {
                            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                        }
                        mDeviceAddress = result.getDevice().getAddress();
                        Log.d(TAG, "Device: " + mDeviceAddress);

                        Intent gattServiceIntent = new Intent(ContourService.this, BGMeterGattService.class);
                        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.d(TAG, "Scan Error");
                }
            };


    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            this.mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mBluetoothLeScanner.startScan(mLeScanCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy() , service stopped...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner.stopScan(mLeScanCallback);
        }

        try {
            unregisterReceiver(mGattUpdateReceiver);
            unbindService(mServiceConnection);
        } catch (IllegalArgumentException e){
            Log.e(TAG, "onDestroy() error ", e);
        }
        mBGMeterGattService = null;
    }

    //    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BGMeterGattService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BGMeterGattService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BGMeterGattService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    String timestamp;
    private void sendPatVital(final String type, final String value, final String date, final String device_mac_address) {
        //WebService.getInstance().SendPatVitalData(type, value, timestamp, device_mac_address, getMacAddr());

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

    private static String currentDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(Calendar.getInstance().getTime());
    }
}
