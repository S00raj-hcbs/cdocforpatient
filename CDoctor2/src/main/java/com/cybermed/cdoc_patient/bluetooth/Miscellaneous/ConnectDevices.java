package com.cybermed.cdoc_patient.bluetooth.Miscellaneous;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothVitalService;
import com.cybermed.cdoc_patient.common.BaseActivity;

public class ConnectDevices extends BaseActivity implements View.OnClickListener {
    private Button btnStart, btnStop;
    private TextView dataValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_devices);

        btnStart = findViewById(R.id.start_service_btn);
        btnStop = findViewById(R.id.stop_service_btn);
        dataValue = findViewById(R.id.data_values);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        if (isMyServiceRunning(BluetoothVitalService.class)) {
            dataValue.setText("Bluetooth hub is running");
        } else {
            dataValue.setText("Bluetooth hub has stopped");
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service_btn:
                dataValue.setText("Bluetooth hub is running");
                startService(new Intent(this, BluetoothVitalService.class));
                break;

            case R.id.stop_service_btn:
                dataValue.setText("Bluetooth hub has stopped");
                stopService(new Intent(this, BluetoothVitalService.class));
                break;
        }
    }
}
