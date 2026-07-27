package com.choicemmed.s1blelibrary.base;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.choicemmed.s1blelibrary.R;

/**
 * Created by Yu Baoxiang on 2015/3/27.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BaseBle implements GattListener, BluetoothAdapter.LeScanCallback {


    protected static final String LogTag_BLE = "BLELog";

    private Context mContext;
    public BleListener mBleListener;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice mBluetoothDevice;
    public BluetoothGatt mBluetoothGatt;

    private static final long BLE_CONNECT_TIMEOUT = 20000;

    private static final long SCAN_PERIOD = 20000;
    public boolean foundDevice = false;

    protected static final int MSG_STOPSCAN = 0;
    protected static final int MSG_CONNECTTIMEOUT = 1;


    protected Handler bleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STOPSCAN:
                    stopLeScan();
                    break;
                case MSG_CONNECTTIMEOUT:
                    bleHandler.removeMessages(MSG_STOPSCAN);
                    mBleListener.onError(getDeviceType(), mContext.getString(R.string.error_connect_timeout));
                    resetGatt();
                    break;
                default:
                    break;
            }
        }
    };

    public BaseBle(Context context, BleListener bleListener) {
        mContext = context;
        mBleListener = bleListener;
        initBluetoothAdapter();
    }

    private void initBluetoothAdapter() {
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private class TestBleResult {
        public boolean isAvailable = true;
        public String errorMsg;
    }

    private TestBleResult testBle() {
        TestBleResult result = new TestBleResult();

        if (mBluetoothAdapter == null) {
            result.isAvailable = false;
            result.errorMsg = mContext.getString(R.string.error_bluetooth_not_supported);
            return result;
        }

        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            result.isAvailable = false;
            result.errorMsg = mContext.getString(R.string.error_ble_not_supported);
            return result;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            result.isAvailable = false;
            result.errorMsg = mContext.getString(R.string.error_bluetooth_not_open);
            return result;
        }

        return result;
    }

    protected abstract DeviceType getDeviceType();

    public void startLeScan() {
        TestBleResult result = testBle();
        if (!result.isAvailable) {
            mBleListener.onError(getDeviceType(), result.errorMsg);
            return;
        }
        if (!bleHandler.hasMessages(MSG_STOPSCAN)) {
            Message msg = bleHandler.obtainMessage();

            msg.what = MSG_STOPSCAN;
            bleHandler.sendMessageDelayed(msg, SCAN_PERIOD);

            foundDevice = false;
            mBluetoothAdapter.startLeScan(this);
        }
    }

    public void stopLeScan() {
        mBluetoothAdapter.stopLeScan(this);
        if (!foundDevice) {
            mBleListener.onScanTimeout(getDeviceType());
        }
    }

    public void connectDevice(String address) {
        Log.d(LogTag_BLE, "开始连接……");
        if (address == null) {
            Log.d(LogTag_BLE, "参数错误：address为空");
            return;
        }
        if (mBluetoothAdapter == null) {
            Log.d(LogTag_BLE, "BluetoothAdapter未初始化");
            return;
        }
        try {
            resetGatt();
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
            Log.d(LogTag_BLE, "开始连接……" + mBluetoothDevice);
            mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, GetGattCallback());
            Message msgConnectTimeout = bleHandler.obtainMessage(MSG_CONNECTTIMEOUT);
            bleHandler.sendMessageDelayed(msgConnectTimeout, BLE_CONNECT_TIMEOUT);
        } catch (Exception e) {
            Log.d(LogTag_BLE, "连接出错");
            e.printStackTrace();
        }
    }

    protected abstract BluetoothGattCallback GetGattCallback();

    public abstract void sendCmd(String cmd);

    public void resetGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
        mBluetoothDevice = null;
    }

    @Override
    public void onError(DeviceType deviceType, String errorMsg) {
        Log.d("ble", "onError");
        mBleListener.onError(deviceType, errorMsg);
        resetGatt();
    }

    @Override
    public void onDisconnected(DeviceType deviceType) {
        mBleListener.onDisconnected(deviceType);
        resetGatt();
    }

    @Override
    public void onInitialized(DeviceType deviceType) {
        mBleListener.onInitialized(deviceType);
        bleHandler.removeMessages(MSG_CONNECTTIMEOUT);
        bleHandler.removeMessages(MSG_STOPSCAN);
        Log.d("1-6", "onInitialized");
    }

    @Override
    public void onCmdResponse(DeviceType deviceType, byte[] data) {
        mBleListener.onCmdResponse(deviceType, data);
    }

    @Override
    public void onDataResponse(DeviceType deviceType, byte[] data) {
        mBleListener.onDataResponse(deviceType, data);
    }
}
