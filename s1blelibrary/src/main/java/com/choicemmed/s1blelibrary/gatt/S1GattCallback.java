package com.choicemmed.s1blelibrary.gatt;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.choicemmed.s1blelibrary.base.BaseGattCallback;
import com.choicemmed.s1blelibrary.base.DeviceType;
import com.choicemmed.s1blelibrary.base.GattListener;
import com.choicemmed.s1blelibrary.utils.ByteUtils;
import com.choicemmed.s1blelibrary.utils.LogUtils;

import java.util.List;
import java.util.UUID;


public class S1GattCallback extends BaseGattCallback {
    //C208的数据交互服务及通道
    private static final String DEVICE_UUID_PREFIX = "ba11f08c5f140b0d1070";
    private static final UUID Characteristic_UUID_CD01 = UUID
            .fromString("0000cd01-0000-1000-8000-00805f9b34fb");
    private static final UUID Characteristic_UUID_CD02 = UUID
            .fromString("0000cd02-0000-1000-8000-00805f9b34fb");
    private static final UUID Characteristic_UUID_CD03 = UUID
            .fromString("0000cd03-0000-1000-8000-00805f9b34fb");
    private static final UUID Characteristic_UUID_CD04 = UUID
            .fromString("0000cd04-0000-1000-8000-00805f9b34fb");
    private static final UUID Characteristic_UUID_CD20 = UUID
            .fromString("0000cd20-0000-1000-8000-00805f9b34fb");
    public static final String TAG = "S1GattCallback";
    private static BluetoothGattService s1Service;

    public S1GattCallback(GattListener gattListener) {
        super(gattListener);
    }

    @Override
    protected DeviceType getDeviceType() {
        return DeviceType.S1;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        Log.d(TAG, "onConnectionStateChange");
        super.onConnectionStateChange(gatt, status, newState);
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "异常：改变蓝牙状态失败，status=  onDisconnected" + status);
            onDisconnected();
            return;
        }
        switch (newState) {
            case BluetoothProfile.STATE_CONNECTED: {
                Log.d(TAG, "蓝牙已连接");
                if (gatt.discoverServices()) {
                    Log.d(TAG, "发现服务启动");
                } else {
                    Log.d(TAG, "异常：开始发现服务失败");
                    onError("异常：开始发现服务失败");
                }
                break;
            }
            case BluetoothProfile.STATE_DISCONNECTED: {
                Log.d(TAG, "蓝牙已断开");
                //close
                gatt.close();
                onDisconnected();
                break;
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.d(LogTag_BLE, "异常：发现服务失败，status=" + status);
            onError("异常：发现服务失败，status=" + status);
            return;
        }
        boolean foundService = false;
        List<BluetoothGattService> service = gatt.getServices();
        for (BluetoothGattService service1 : service) {
            String serviceUUID = service1.getUuid().toString();
            String serviceUUID4Compare = serviceUUID.toLowerCase().replace("-", "");
            if (serviceUUID4Compare.contains(DEVICE_UUID_PREFIX)) {
                foundService = true;
                BluetoothGattCharacteristic characteristic = service1.getCharacteristic(Characteristic_UUID_CD01);
                if (setCharacteristicNotification(gatt, characteristic, true)) {
                    s1Service = service1;
                    Log.d(LogTag_BLE, "开始监听notify1成功");
                } else {
                    Log.d(LogTag_BLE, "异常：开始监听Notify1失败");
                    onError("异常：开始监听Notify1失败");
                }
            }
        }

        if (!foundService) {
            Log.d(LogTag_BLE, "异常：发现的服务中不包含血氧数据服务");
            onError("异常：发现的服务中不包含血氧数据服务");
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        byte[] data = characteristic.getValue();
        LogUtils.d(TAG, "<---cmd" + ByteUtils.bytes2HexString(data));
        if (characteristic.getUuid().equals(Characteristic_UUID_CD04) & (data != null) & (data.length > 0)) {
            onDataReceived(data);
            return;
        }
        onCommandReceived(data);


    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.d(LogTag_BLE, "异常:写描述符失败，status=" + status);
            onError("异常:写描述符失败，status=" + status);
            return;
        }

        BluetoothGattService service = descriptor.getCharacteristic()
                .getService();
        if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD01)) {
            BluetoothGattCharacteristic characteristic = service
                    .getCharacteristic(Characteristic_UUID_CD02);
            Log.d(LogTag_BLE, "监听notify1成功");
            if (setCharacteristicNotification(gatt, characteristic, true)) {
                Log.d(LogTag_BLE, "开始监听notify2成功");
            } else {
                Log.d(LogTag_BLE, "异常：开始监听Notify2失败");
                onError("异常：开始监听Notify2失败");
            }
        } else if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD02)) {
            BluetoothGattCharacteristic characteristic = service
                    .getCharacteristic(Characteristic_UUID_CD03);
            Log.d(LogTag_BLE, "监听notify2成功");
            if (setCharacteristicNotification(gatt, characteristic, true)) {
                Log.d(LogTag_BLE, "开始监听notify3成功");
            } else {
                Log.d(LogTag_BLE, "异常：开始监听Notify3失败");
                onError("异常：开始监听Notify3失败");
            }
        } else if (descriptor.getCharacteristic().getUuid().equals(Characteristic_UUID_CD03)) {
            BluetoothGattCharacteristic characteristic = service
                    .getCharacteristic(Characteristic_UUID_CD04);

            Log.d(LogTag_BLE, "监听notify3成功");
            if (setCharacteristicNotification(gatt, characteristic, true)) {
                Log.d(LogTag_BLE, "开始监听notify4成功");
            } else {
                Log.d(LogTag_BLE, "异常：开始监听Notify4失败");
                onError("异常：开始监听Notify4失败");
            }
        } else if (descriptor.getCharacteristic().getUuid()
                .equals(Characteristic_UUID_CD04)) {
            Log.d(LogTag_BLE, "监听notify4成功");
            onInitialized();
        }


    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.d(LogTag_BLE, "异常：写特征状态失败，status=" + status);
            onError("异常：写特征状态失败，status=" + status);
            return;
        }
        Log.d(LogTag_BLE, "写特征成功");
    }

    /**
     * 发送命令
     *
     * @param gatt
     * @param command
     */
    public boolean sendCmd(BluetoothGatt gatt, String command) {
        BluetoothGattCharacteristic write20 = s1Service
                .getCharacteristic(Characteristic_UUID_CD20);
        byte[] value = ByteUtils.cmdString2Bytes(command, true);
        write20.setValue(value);
        LogUtils.d(TAG, "--->cmd" + ByteUtils.bytes2HexString(value));
        return gatt.writeCharacteristic(write20);

    }
}
