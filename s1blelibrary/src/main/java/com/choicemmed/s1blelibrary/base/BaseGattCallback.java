package com.choicemmed.s1blelibrary.base;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

/**
 * Created by Yu Baoxiang on 2015/3/27.
 */
public abstract class BaseGattCallback extends BluetoothGattCallback {
    protected static final String LogTag_BLE = "BLELog";

    private static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private GattListener mGattListener;

    protected BaseGattCallback(GattListener gattListener) {
        mGattListener = gattListener;
    }

    protected abstract DeviceType getDeviceType();

    protected void onError(String errorMsg) {
        if (mGattListener != null) {
            mGattListener.onError(getDeviceType(), errorMsg);
        }
    }

    protected void onDisconnected() {
        if (mGattListener != null) {
            mGattListener.onDisconnected(getDeviceType());
        }
    }

    protected void onInitialized() {
        if (mGattListener != null) {
            mGattListener.onInitialized(getDeviceType());
        }
    }

    protected void onDataReceived(byte[] data) {
        if (mGattListener != null) {
            mGattListener.onDataResponse(getDeviceType(), data);
        }
    }

    protected void onCommandReceived(byte[] data) {
        if (mGattListener != null) {
            mGattListener.onCmdResponse(getDeviceType(), data);
        }
    }

    protected static boolean setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (gatt == null || characteristic == null) {
            return false;
        }
        if (!gatt.setCharacteristicNotification(characteristic, enabled)) {
            return false;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
        return true;
    }

    protected static boolean setCharacteristicIndication(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (gatt == null || characteristic == null) {
            return false;
        }
        if (!gatt.setCharacteristicNotification(characteristic, enabled)) {
            return false;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
        return true;
    }
}
