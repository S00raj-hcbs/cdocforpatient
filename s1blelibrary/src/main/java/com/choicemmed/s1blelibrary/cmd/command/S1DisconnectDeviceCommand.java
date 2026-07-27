package com.choicemmed.s1blelibrary.cmd.command;

import com.choicemmed.s1blelibrary.ble.S1Ble;

/**
 * Author：ZhengZhong on 2016/10/27 11:20
 */

public class S1DisconnectDeviceCommand extends S1BaseCommand {
    private static final String TAG = "S1DisconnectDeviceCommand";

    public S1DisconnectDeviceCommand(S1Ble s1Ble) {
        super(s1Ble);
    }

    @Override
    public void execute() {
        s1Ble.resetGatt();
    }
}