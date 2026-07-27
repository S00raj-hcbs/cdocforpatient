package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.IHEALTH_MAC_ADDR;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTED;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.CONNECTING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.SCANNING;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BtUtils.compareMac;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BtUtils.defaultEventBus;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ConnectDevice;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DEVICE;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DeviceNotify;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.DisconnectDevice;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ScanDevice;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StartDiscovery;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.StopDiscovery;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ToTimeoutPage;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BlueToothScanFragment extends EventBusFragment {


    protected STATE state = SCANNING;
    protected DEVICE deviceName = DEVICE.UNKNOWN;
    String mac,mDeviceName;
    protected String macAddress = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            macAddress = getArguments().getString(IHEALTH_MAC_ADDR);

    }

    @Override
    public void onStop() {
        super.onStop();
        defaultEventBus().post(new StopDiscovery());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (state != CONNECTING && state != SCANNING) {
            state = CONNECTED;
            connectedViewSet();
        } else if (state == CONNECTING) {
            connectingViewSet();
        } else {
            scanningViewSet();
        }
        defaultEventBus().post(new StartDiscovery(deviceName));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanDevice(ScanDevice scanDevice) {
         mac = scanDevice.getMac();
        mDeviceName = scanDevice.getDeviceName();

        if (compareMac(mac, macAddress)) {
            state = CONNECTING;
            connectingViewSet();
            defaultEventBus().post(new ConnectDevice("test@com.com", mac, mDeviceName));
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void toTimeoutPage(ToTimeoutPage toTimeoutPage) {
        timeOutViewSet();

    }

    @Override
    public void onDestroy() {
        if (state == CONNECTING|| state==CONNECTED) {
            defaultEventBus().post(new DisconnectDevice( mac, mDeviceName));
        }
        super.onDestroy();

    }

    //  abstract protected void onDeviceConnectionStateChange(DeviceConnectionStateChange deviceConnectionStateChange);

    abstract protected void onDeviceNotify(DeviceNotify deviceNotify);

    abstract protected void scanningViewSet();

    abstract protected void connectingViewSet();

    abstract protected void connectedViewSet();

    abstract protected void timeOutViewSet();


    public boolean isConnecting() {
        return state == CONNECTING;
    }

}
