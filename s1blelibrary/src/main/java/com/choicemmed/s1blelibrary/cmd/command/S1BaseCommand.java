package com.choicemmed.s1blelibrary.cmd.command;

import com.choicemmed.s1blelibrary.ble.S1Ble;

/**
 * Author：ZhengZhong on 2016/10/25 17:20
 */

public abstract class S1BaseCommand {
    private static final String TAG = "S1BaseCommand";
    protected S1Ble s1Ble;

    public S1BaseCommand(S1Ble s1Ble) {
        this.s1Ble = s1Ble;
    }

    public abstract void execute();
}