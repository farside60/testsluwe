package io.reisub.devious.autodialog.tasks;

import net.unethicalite.api.widgets.Dialog;

public class PhialsExchange extends DialogTask {
  private int index;
  
  @Override
  public String getStatus() {
    return "Exchanging noted planks";
  }

  @Override
  public boolean validate() {
    if (!config.phialsExchangeAll()) {
      return false;
    }

    index = getIndexStartsWith("Exchange All:");
    
    return index != -1;
  }

  @Override
  public void execute() {
    Dialog.chooseOption(index);
  }
}
