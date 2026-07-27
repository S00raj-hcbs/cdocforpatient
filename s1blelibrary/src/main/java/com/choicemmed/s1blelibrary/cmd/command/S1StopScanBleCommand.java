package com.choicemmed.s1blelibrary.cmd.command;

import com.choicemmed.s1blelibrary.ble.S1Ble;

/**
 * Author：ZhengZhong on 2016/10/26 17:32
 */

public class S1StopScanBleCommand extends S1BaseCommand {
    private static final String TAG = "S1StopScanBleCommand";

    public S1StopScanBleCommand(S1Ble s1Ble) {
        super(s1Ble);
    }

    @Override
    public void execute() {
        s1Ble.stopLeScan();
    }
}