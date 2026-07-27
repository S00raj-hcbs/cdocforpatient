package com.choicemmed.s1blelibrary.cmd.factory;

import com.choicemmed.s1blelibrary.ble.S1Ble;
import com.choicemmed.s1blelibrary.cmd.command.S1BaseCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1UnitLbsCommand;

/**
 * Created by lazy_xia on 17/1/10.
 */

public class IS1UnitLbsCommandFactory implements IS1CommandCreator {
    private static final String TAG = IS1UnitLbsCommandFactory.class.getSimpleName();

    @Override
    public S1BaseCommand createCommand(S1Ble s1Ble) {
        return new S1UnitLbsCommand(s1Ble);
    }
}
