package com.choicemmed.s1blelibrary.cmd.factory;

import com.choicemmed.s1blelibrary.ble.S1Ble;
import com.choicemmed.s1blelibrary.cmd.command.S1BaseCommand;
import com.choicemmed.s1blelibrary.cmd.command.S1UnitKgCommand;

/**
 * Created by lazy_xia on 17/1/10.
 */

public class IS1UnitKgCommandFactory implements IS1CommandCreator {
    private static final String TAG = IS1UnitKgCommandFactory.class.getSimpleName();

    @Override
    public S1BaseCommand createCommand(S1Ble s1Ble) {
        return new S1UnitKgCommand(s1Ble);
    }
}
