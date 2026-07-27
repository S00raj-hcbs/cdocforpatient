package com.choicemmed.s1blelibrary.cmd.factory;

import com.choicemmed.s1blelibrary.ble.S1Ble;
import com.choicemmed.s1blelibrary.cmd.command.S1BaseCommand;

/**
 * Author：ZhengZhong on 2016/10/28 17:39
 */

public interface IS1CommandCreator {
    S1BaseCommand createCommand(S1Ble s1Ble);
}
