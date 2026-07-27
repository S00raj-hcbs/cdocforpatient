package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothRestartReceiver extends BroadcastReceiver {
    private final static String TAG = BluetoothRestartReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Do something every 5 seconds
        Log.d(TAG,"BluetoothRestarting");


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();

        if (!isEnabled) {
            bluetoothAdapter.enable();
            Log.d(TAG,"bluetooth enabled");

        } else {
            bluetoothAdapter.disable();
            Log.d(TAG,"bluetooth disabled");

        }
    }
}