package com.choicemmed.s1blelibrary.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.nfc.Tag;

import com.choicemmed.s1blelibrary.base.BaseBle;
import com.choicemmed.s1blelibrary.base.BleListener;
import com.choicemmed.s1blelibrary.base.DeviceType;
import com.choicemmed.s1blelibrary.gatt.S1GattCallback;
import com.choicemmed.s1blelibrary.utils.LogUtils;

/**
 * Created by zhaozhenhui on 2016/3/24.
 */
public class S1Ble extends BaseBle {
    private static final String DEVICE_UUID_PREFIX = "ba11f08c5f140b0d1070";
    private static final java.lang.String TAG = S1Ble.class.getSimpleName();
    private S1GattCallback s1GattCallback;

    public S1Ble(Context context, BleListener bleListener) {
        super(context, bleListener);
    }

    @Override
    protected DeviceType getDeviceType() {

        return DeviceType.S1;
    }

    @Override
    protected BluetoothGattCallback GetGattCallback() {
        s1GattCallback = new S1GattCallback(this);
        return s1GattCallback;
    }

    @Override
    public void sendCmd(String cmd) {
        try {
            s1GattCallback.sendCmd(mBluetoothGatt, cmd);
        } catch (Exception e) {
            onError(DeviceType.S1, "发送命令失败");
            e.printStackTrace();
        }
    }

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        final String str = bytes2HexString(scanRecord).replace("-", "").toLowerCase();
        new Thread() {
            @Override
            public void run() {
                try {
                    if (!foundDevice && device != null && str.contains(DEVICE_UUID_PREFIX)) {
                        LogUtils.d("onLeScan", "已扫描到蓝牙设备" + device.getAddress());
                        foundDevice = true;
                        stopLeScan();
                        mBleListener.onFoundDevice(getDeviceType(), device.getAddress(), device.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static String bytes2HexString(byte[] a) {

        int len = a.length;
        byte[] b = new byte[len];
        for (int k = 0; k < len; k++) {
            b[k] = a[a.length - 1 - k];
        }

        String ret = "";
        for (int i = 0; i < len; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }

        return ret;
    }
}
