package com.choicemmed.s1blelibrary.cmd.command;

import com.choicemmed.s1blelibrary.ble.S1Ble;

/**
 * Author：ZhengZhong on 2016/10/26 17:32
 */

public class S1ScanBleCommand extends S1BaseCommand {
    private static final String TAG = "S1ScanBleCommand";

    public S1ScanBleCommand(S1Ble s1Ble) {
        super(s1Ble);
    }

    @Override
    public void execute() {
        s1Ble.startLeScan();
    }
}