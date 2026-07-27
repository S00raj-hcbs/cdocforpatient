package com.choicemmed.s1blelibrary.cmd.command;

import android.text.TextUtils;

import com.choicemmed.s1blelibrary.ble.S1Ble;

/**
 * Author：ZhengZhong on 2016/10/26 17:37
 */

public class S1ConnectDeviceCommand extends S1BaseCommand {
    private static final String TAG = "S1ConnectDeviceCommand";
    private String address;

    public S1ConnectDeviceCommand(S1Ble s1Ble) {
        super(s1Ble);
    }


    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void execute() {
        if (!TextUtils.isEmpty(address)) {
            s1Ble.connectDevice(address);
        } else {
            throw new RuntimeException("macAddress is empty or null");
        }
    }
}