package com.choicemmed.s1blelibrary.cmd.factory;

import com.choicemmed.s1blelibrary.ble.S1Ble;
import com.choicemmed.s1blelibrary.cmd.command.S1BaseCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1ScanBleCommand;

/**
 * Author：ZhengZhong on 2016/10/28 17:32
 */

public class IS1ScanBleCommandFactory implements IS1CommandCreator {
    private static final String TAG = "IS1ScanBleCommandFactory";

    @Override
    public S1BaseCommand createCommand(S1Ble s1Ble) {
        return new S1ScanBleCommand(s1Ble);
    }
}