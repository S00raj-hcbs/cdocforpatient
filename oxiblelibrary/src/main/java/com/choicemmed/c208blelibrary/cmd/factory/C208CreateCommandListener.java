package com.choicemmed.c208blelibrary.cmd.factory;

import com.choicemmed.c208blelibrary.ble.C208Ble;
import com.choicemmed.c208blelibrary.cmd.command.C208BaseCommand;

public abstract interface C208CreateCommandListener
{
  public abstract C208BaseCommand createCommand(C208Ble paramC208Ble);
}


/* Location:              C:\Users\joshu\Desktop\classes.jar!\com\choicemmed\c208blelibrary\cmd\factory\C208CreateCommandListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */