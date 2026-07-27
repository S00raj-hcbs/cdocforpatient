package com.choicemmed.bp1blelibrary.cmd.factory;

import com.choicemmed.bp1blelibrary.ble.Bp1Ble;
import com.choicemmed.bp1blelibrary.cmd.command.Bp1BaseCommand;

public abstract interface Bp1CommandCreator
{
  public abstract Bp1BaseCommand createCommand(Bp1Ble paramBp1Ble);
}


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\bp1blelibrary\cmd\factory\Bp1CommandCreator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */