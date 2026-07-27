package com.choicemmed.s1blelibrary.cmd.invoker;

import android.content.Context;

import com.choicemmed.s1blelibrary.Device.S1Device;
import com.choicemmed.s1blelibrary.R;
import com.choicemmed.s1blelibrary.base.BleListener;
import com.choicemmed.s1blelibrary.base.DeviceType;
import com.choicemmed.s1blelibrary.ble.S1Ble;
import com.choicemmed.s1blelibrary.cmd.command.S1BaseCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1ConnectDeviceCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1MatchPasswordCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1UnitKgCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1UnitLbsCommand;
import com.choicemmed.s1blelibrary.cmd.factory.IS1CommandCreator;
import com.choicemmed.s1blelibrary.cmd.factory.IS1ConnectDeviceCommandFactory;
import com.choicemmed.s1blelibrary.cmd.factory.IS1DisconnectDeviceCommandFactory;
import com.choicemmed.s1blelibrary.cmd.factory.IS1MatchPasswordCommandFactory;
import com.choicemmed.s1blelibrary.cmd.factory.IS1ScanBleCommandFactory;
import com.choicemmed.s1blelibrary.cmd.factory.IS1StopScanBleCommandFactory;
import com.choicemmed.s1blelibrary.cmd.factory.IS1UnitKgCommandFactory;
import com.choicemmed.s1blelibrary.cmd.factory.IS1UnitLbsCommandFactory;
import com.choicemmed.s1blelibrary.cmd.listener.S1Listener;
import com.choicemmed.s1blelibrary.cmd.parse.S1ParseData;
import com.choicemmed.s1blelibrary.utils.ByteUtils;
import com.choicemmed.s1blelibrary.utils.LogUtils;

/**
 * Author：ZhengZhong on 2016/10/25 18:54
 */

public class S1Invoker {
    private static final String TAG = "S1Invoker";
    private final Context mContext;
    private S1Ble s1Ble;
    private S1Device s1Device;
    private S1Listener s1Listener;
    private S1BaseCommand previousCommand;

    private S1BaseCommand s1Command;

    public final static int UNIT_KG = 0;
    public final static int UNIT_LBS = 1;
    private int unit;


    public void setUnit(int unit) {
        this.unit = unit;
        switch (unit) {
            case UNIT_KG:
                s1Command = getCommand(new IS1UnitKgCommandFactory());
                break;
            case UNIT_LBS:
                s1Command = getCommand(new IS1UnitLbsCommandFactory());
                break;
        }
        s1Command.execute();
    }


    /**
     * 蓝牙空闲状态
     */
    public static final int BLUETOOTH_FREE = 0;

    /**
     * 蓝牙扫描中状态
     */
    public static final int BLUETOOTH_SCANNING = 2;
    /**
     * 发现设备状态
     */
    public static final int BLUETOOTH_FOUND_DEVICE = 4;
    /**
     * 连接设备中状态
     */
    public static final int BLUETOOTH_CONNECTING_DEVICE = 8;
    /**
     * 获取设备信息中状态
     */
    public static final int BLUETOOTH_GETTING_DEVICE_INFO = 16;
    /**
     * 获取到设备信息状态
     */
    public static final int BLUETOOTH_GOT_DEVICE_INFO = 32;
    /**
     * 蓝牙连接成功状态
     */
    public static final int BLUETOOTH_CONNECT_SUCCESS = 64;
    /**
     * 测量完成状态
     */
    public static final int BLUETOOTH_MEASURE_COMPLETE = 128;

    private int bleState;

    /**
     * 绑定、连接设备标志false-绑定设备，true-连接设备
     */
    private static boolean bindOrConnectState = false;

    public S1Invoker(final Context mContext, S1Listener listener) {
        s1Listener = listener;
        this.mContext = mContext;
        s1Ble = new S1Ble(mContext, new BleListener() {
            @Override
            public void onFoundDevice(DeviceType deviceType, String address, String deviceName) {
                changeState(BLUETOOTH_FOUND_DEVICE);
                s1Device.setDeviceMacAddress(address);
                s1Device.setDeviceName(deviceName);
                S1ConnectDeviceCommand s1Command = (S1ConnectDeviceCommand) getCommand(new IS1ConnectDeviceCommandFactory());
                s1Command.setAddress(address);
                s1Command.execute();
                changeState(BLUETOOTH_CONNECTING_DEVICE);
            }

            @Override
            public void onScanTimeout(DeviceType deviceType) {
                LogUtils.d(TAG, "onScanTimeout....");
                s1Listener.onScanTimeout(mContext.getString(R.string.bind_device_fail));
                changeState(BLUETOOTH_FREE);
            }

            @Override
            public void onError(DeviceType deviceType, String errorMsg) {
                s1Listener.onError(errorMsg);
                changeState(BLUETOOTH_FREE);
            }

            @Override
            public void onDisconnected(DeviceType deviceType) {
                LogUtils.d(TAG, "onDisconnected....断开");
                changeState(BLUETOOTH_FREE);
                s1Listener.onDisconnected();
            }

            @Override
            public void onInitialized(DeviceType deviceType) {
                /*发配对密码命令*/
                LogUtils.d(TAG, "onInitialized....发配对密码命令");
                s1Command = getCommand(new IS1MatchPasswordCommandFactory());
                s1Command.execute();
                previousCommand = new S1MatchPasswordCommand(s1Ble);
                if (!bindOrConnectState) {
                    changeState(BLUETOOTH_GETTING_DEVICE_INFO);
                }
                s1Listener.onBindDeviceSuccess(s1Device);
                s1Listener.onConnectedDeviceSuccess();
            }

            @Override
            public void onCmdResponse(DeviceType deviceType, byte[] result) {
                String data = ByteUtils.bytes2HexString(result);
                /*连接配对密码命令返回数据*/
                if (previousCommand instanceof S1MatchPasswordCommand) {
                    if (!data.contains("55aa")) {
                        s1Listener.onBindDeviceFail(mContext.getString(R.string.exception_read_device_sn));
                        return;
                    }
                    //cmd55aa03b100b4
                    if (bindOrConnectState && result[2] == 0x03 && (result[3] & 0xff) == 0xb1) {
                        boolean matchSuccess = S1ParseData.parseMatchResult(data);
                        if (matchSuccess) {
                            if (s1Listener != null) {
                                s1Listener.onConnectedDeviceSuccess();
                                changeState(BLUETOOTH_CONNECT_SUCCESS);
                            }

                        } else {
                            if (s1Listener != null)
                                s1Listener.onConnectedDeviceFail(mContext.getString(R.string.exception_match_fail));
                        }
                    }
                }
                //设置体重单位返回
                if (result[2] == 0x02
                        && (previousCommand instanceof S1UnitKgCommand) || (previousCommand instanceof S1UnitLbsCommand)) {
                    if (S1ParseData.parseUnitResult(result)) {
                        s1Listener.setUnitSuccess();
                    } else {
                        s1Listener.setUnitError(mContext.getString(R.string.exception_unit_failed));
                    }
                }
            }

            @Override
            public void onDataResponse(DeviceType deviceType, byte[] data) {
                String dataResponse = ByteUtils.bytes2HexString(data);
                LogUtils.d(TAG, dataResponse);

                double weight = S1ParseData.parseWeight(data);
                /*if (unit == UNIT_LBS) {
                    weight = weight * 2.2046;
                }*/
                s1Listener.onDataResponse(weight, s1Device.getDeviceMacAddress());
                changeState(BLUETOOTH_MEASURE_COMPLETE);
            }
        });
        s1Device = new S1Device();
    }

    /**
     * 绑定设备方法
     *
     * @param listener 绑定设备接口
     */
    public void bindDevice() {
        bindOrConnectState = false;

        s1Command = getCommand(new IS1ScanBleCommandFactory());
        s1Command.execute();
        changeState(BLUETOOTH_SCANNING);
    }

    public void stopDeviceScan() {
        bindOrConnectState = false;
        this.s1Command = getCommand(new IS1StopScanBleCommandFactory());
        this.s1Command.execute();
        changeState(2);
    }

    /**
     * 连接设备方法
     *
     * @param s1Device 设备
     */
    public void connectDevice(S1Device s1Device) {
        bindOrConnectState = true;
        S1ConnectDeviceCommand s1Command = (S1ConnectDeviceCommand) getCommand(new IS1ConnectDeviceCommandFactory());
        s1Command.setAddress(s1Device.getDeviceMacAddress());
        s1Command.execute();
        changeState(BLUETOOTH_CONNECTING_DEVICE);
    }

    /**
     * 断开连接方法
     */
    public void disconnectDevice() {
        s1Command = getCommand(new IS1DisconnectDeviceCommandFactory());
        s1Command.execute();
    }


    private S1BaseCommand getCommand(IS1CommandCreator commandFactory) {
        return commandFactory.createCommand(s1Ble);
    }

    private synchronized void changeState(int state) {
        if (state == this.bleState) {
            return;
        }
        s1Listener.onStateChanged(this.bleState, state);
        LogUtils.d(TAG, "onStateChanged " + bleState + " new state " + state);
        this.bleState = state;
    }


}