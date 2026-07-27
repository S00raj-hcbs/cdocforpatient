package com.choicemmed.s1blelibrary.cmd.command;

import com.choicemmed.s1blelibrary.ble.S1Ble;

/**
 * Author：ZhengZhong on 2016/10/25 17:29
 */

public class S1MatchPasswordCommand extends S1BaseCommand {
    private static final String TAG = "S1MatchPasswordCommand";
    private static final String cmd = "aa5504b10000";

    public S1MatchPasswordCommand(S1Ble s1Ble) {
        super(s1Ble);
    }

    @Override
    public void execute() {
        s1Ble.sendCmd(cmd);
    }


}