package com.choicemmed.s1blelibrary.cmd.factory;

import com.choicemmed.s1blelibrary.ble.S1Ble;
import com.choicemmed.s1blelibrary.cmd.command.S1BaseCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1MatchPasswordCommand;

/**
 * Author：ZhengZhong on 2016/10/28 17:51
 */

public class IS1MatchPasswordCommandFactory implements IS1CommandCreator {
    private static final String TAG = "IS1MatchPasswordCommandFactory";

    @Override
    public S1BaseCommand createCommand(S1Ble s1Ble) {
        return new S1MatchPasswordCommand(s1Ble);
    }
}