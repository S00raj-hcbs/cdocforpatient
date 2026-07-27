package com.choicemmed.s1blelibrary.cmd.command;

import com.choicemmed.s1blelibrary.ble.S1Ble;

/**
 * Created by lazy_xia on 17/1/10.
 */

public class S1UnitLbsCommand extends S1BaseCommand {
    private static final String TAG = S1UnitLbsCommand.class.getSimpleName();
    private static final String cmd = "aa55034101";

    public S1UnitLbsCommand(S1Ble s1Ble) {
        super(s1Ble);
    }

    @Override
    public void execute() {
        s1Ble.sendCmd(cmd);
    }
}
