package com.choicemmed.c208blelibrary.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.choicemmed.c208blelibrary.R;
import com.choicemmed.c208blelibrary.R.string;


@TargetApi(18)
public abstract class BaseBle
        implements GattListener, BluetoothAdapter.LeScanCallback {
    protected static final String LogTag_BLE = "BLELog";
    private Context mContext;
    public BleListener mBleListener;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    public BluetoothGatt mBluetoothGatt;
    private static final long BLE_CONNECT_TIMEOUT = 10000L;
    private static final long SCAN_PERIOD = 20000L;
    public boolean foundDevice = false;

    protected static final int MSG_STOPSCAN = 0;

    protected static final int MSG_CONNECTTIMEOUT = 1;

    protected Handler bleHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    BaseBle.this.stopLeScan();
                    break;
                case 1:
                    BaseBle.this.mBleListener.onError(BaseBle.this.getDeviceType(), BaseBle.this.mContext.getString(R.string.error_connect_timeout));
                    BaseBle.this.resetGatt();
                    break;
            }

        }
    };

    public BaseBle(Context context, BleListener bleListener) {
        this.mContext = context;
        this.mBleListener = bleListener;
        initBluetoothAdapter();
    }

    private void initBluetoothAdapter() {
        @SuppressLint("WrongConstant") BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
        this.mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private class TestBleResult {
        public boolean isAvailable = true;
        public String errorMsg;

        private TestBleResult() {
        }
    }

    private TestBleResult testBle() {
        TestBleResult result = new TestBleResult();

        if (this.mBluetoothAdapter == null) {
            result.isAvailable = false;
            result.errorMsg = this.mContext.getString(R.string.error_bluetooth_not_supported);
            return result;
        }

        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            result.isAvailable = false;
            result.errorMsg = this.mContext.getString(R.string.error_ble_not_supported);
            return result;
        }

        if (!this.mBluetoothAdapter.isEnabled()) {
            result.isAvailable = false;
            result.errorMsg = this.mContext.getString(R.string.error_bluetooth_not_open);
            return result;
        }

        return result;
    }

    protected abstract DeviceType getDeviceType();

    public void startLeScan() {
        TestBleResult result = testBle();
        if (!result.isAvailable) {
            this.mBleListener.onError(getDeviceType(), result.errorMsg);
            return;
        }

//    this.bleHandler.removeCallbacksAndMessages(null);
        Message msg = this.bleHandler.obtainMessage();

        msg.what = 0;

        this.bleHandler.sendMessageDelayed(msg, 20000L);

        this.foundDevice = false;
        this.mBluetoothAdapter.startLeScan(this);
    }

    public void stopLeScan() {
        this.mBluetoothAdapter.stopLeScan(this);
        if (!this.foundDevice) {
            this.mBleListener.onScanTimeout(getDeviceType());
        }
    }

    public void connectDevice(String address) {
        Log.d("BLELog", "开始连接……");
        if (address == null) {
            Log.d("BLELog", "参数错误：address为空");
            return;
        }
        if (this.mBluetoothAdapter == null) {
            Log.d("BLELog", "BluetoothAdapter未初始化");
            return;
        }
        try {
            resetGatt();
            this.mBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(address);
            Log.e("BLELog", "开始连接……" + this.mBluetoothDevice);
            this.mBluetoothGatt = this.mBluetoothDevice.connectGatt(this.mContext, false, GetGattCallback());
            Message msgConnectTimeout = this.bleHandler.obtainMessage(1);
            this.bleHandler.sendMessageDelayed(msgConnectTimeout, 20000L);
        } catch (Exception e) {
            Log.d("BLELog", "连接出错");
            e.printStackTrace();
        }
    }

    protected abstract BluetoothGattCallback GetGattCallback();

    public abstract void sendCmd(String paramString);

    public void resetGatt() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
            this.mBluetoothGatt.close();
        }
        this.mBluetoothDevice = null;
    }

    public void onError(DeviceType deviceType, String errorMsg) {
        this.mBleListener.onError(deviceType, errorMsg);
        resetGatt();
    }

    public void onDisconnected(DeviceType deviceType) {
        this.mBleListener.onDisconnected(deviceType);
        resetGatt();
    }

    public void onInitialized(DeviceType deviceType) {
        this.mBleListener.onInitialized(deviceType);
        this.bleHandler.removeMessages(1);
        Log.d("1-6", "onInitialized");
    }

    public void onCmdResponse(DeviceType deviceType, byte[] data) {
        this.mBleListener.onCmdResponse(deviceType, data);
    }

    public void onDataResponse(DeviceType deviceType, byte[] data) {
        this.mBleListener.onDataResponse(deviceType, data);
    }
}


