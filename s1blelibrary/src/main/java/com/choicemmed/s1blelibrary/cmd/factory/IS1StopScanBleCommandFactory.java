package com.choicemmed.s1blelibrary.cmd.factory;

import com.choicemmed.s1blelibrary.ble.S1Ble;
import com.choicemmed.s1blelibrary.cmd.command.S1BaseCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1ScanBleCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1StopScanBleCommand;

/**
 * Author：ZhengZhong on 2016/10/28 17:32
 */

public class IS1StopScanBleCommandFactory implements IS1CommandCreator {
    private static final String TAG = "IS1StopScanBleCommandFactory";

    @Override
    public S1BaseCommand createCommand(S1Ble s1Ble) {
        return new S1StopScanBleCommand(s1Ble);
    }
}